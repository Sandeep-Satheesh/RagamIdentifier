package com.sandeepdev.ragamidentifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nex3z.togglebuttongroup.ToggleButtonGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.Character.isDigit;

public class MainActivity extends AppCompatActivity {
    int addedJanyaRagas = 0, addedMelakartaRagas = 0, addedArohanams = 0, addedAvarohanams = 0, totalAdded = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHandler handler = new DBHandler(this);
        if (!handler.getDatabaseName().equals("ragamsDB") || !(handler.getRagamsCount() == 943)) {
            InitializeDB init = new InitializeDB(this);
            init.execute(null, null, null);
        }
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        final CircularToggle aro_s = findViewById(R.id.tb_aro_s),
                aro_S = findViewById(R.id.tb_aro_S),
                avaro_S = findViewById(R .id.tb_avaro_S),
                avaro_s = findViewById(R.id.tb_avaro_s);
        aro_S.setEnabled(false); aro_S.setChecked(true);
        aro_s.setEnabled(false); aro_s.setChecked(true);
        avaro_S.setEnabled(false); avaro_S.setChecked(true);
        avaro_s.setEnabled(false); avaro_s.setChecked(true);

        final ToggleButtonGroup arohanam = findViewById(R.id.arohanam), avarohanam = findViewById(R.id.avarohanam);

        Button cmdIdentifyRagam = findViewById(R.id.cmdIDRagam);

        cmdIdentifyRagam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] swarams = {"R1", "R2", "G1", "G2", "M1", "M2", "P", "D1", "D2", "N1", "N2"};
                int[] aroh_buttons = {R.id.togg_Aro_R1, R.id.togg_Aro_R2, R.id.togg_Aro_G1, R.id.togg_Aro_G2, R.id.togg_Aro_M1, R.id.togg_Aro_M2, R.id.togg_Aro_P, R.id.togg_Aro_D1, R.id.togg_Aro_D2, R.id.togg_Aro_N1, R.id.togg_Aro_N2};
                int[] avaroh_buttons = {R.id.togg_Avaro_R1, R.id.togg_Avaro_R2, R.id.togg_Avaro_G1, R.id.togg_Avaro_G2, R.id.togg_Avaro_M1, R.id.togg_Avaro_M2, R.id.togg_Avaro_P, R.id.togg_Avaro_D1, R.id.togg_Avaro_D2, R.id.togg_Avaro_N1, R.id.togg_Avaro_N2};

                StringBuilder arohString = new StringBuilder("S "), avarohString = new StringBuilder("S ");

                for (int i = 0; i < arohanam.getChildCount(); i++)
                    if (arohanam.isChecked(aroh_buttons[i]))
                        arohString.append(swarams[i]).append(" ");
                arohString.append("S");

                for (int i = avarohanam.getChildCount() - 1; i >= 0; i--)
                    if (avarohanam.isChecked(avaroh_buttons[i]))
                        avarohString.append(swarams[i]).append(" ");

                avarohString.append("S");
                GetRagamFromDB getRagam = new GetRagamFromDB(MainActivity.this, arohString.toString(), avarohString.toString());
                getRagam.execute(null, null, null);
            }
        });
    }
}
class InitializeDB extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;
    private MainActivity main;

    InitializeDB(MainActivity activity) {
        dialog = new ProgressDialog(activity);
        main = activity;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Initializing database, please wait...");
        dialog.show();
        dialog.setCancelable(false);
    }
    @Override
    protected Void doInBackground(Void... args) {
        // do background work here
        DBHandler handler = new DBHandler(main.getBaseContext());
        handler.openDBForBatchInsert();
        InputStream ragamsList = main.getResources().openRawResource(
                main.getResources().getIdentifier("ragams",
                        "raw", main.getPackageName()));
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(ragamsList));
            String[] currentRecord = new String[4];
            String line;
            short ct = 0;
            int ctr = 0;
            while ((line = br.readLine()) != null) {
                if (line.length() <= 3) continue;

                switch (ct) {
                    case 0:
                        if (!isDigit(line.charAt(0))) { currentRecord[0] = line; main.addedJanyaRagas++; }
                        else {
                            int x;
                            for (x = 0; x < line.length(); x++)
                                if (!isDigit(line.charAt(x))) break;
                            currentRecord[3] = line.substring(x + 1);
                            main.addedMelakartaRagas++;
                            currentRecord[0] = "-";
                        }
                        ctr++;
                        break;
                    case 1:
                        currentRecord[1] = line;
                        main.addedArohanams++;
                        break;
                    case 2:
                        currentRecord[2] = line;
                        main.addedAvarohanams++;
                }
                ct++;
                if (ct == 3) {
                    final String s = (currentRecord[0].equals("-")) ? currentRecord[3] : currentRecord[0];
                    final int finalCtr = ctr;
                    main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage("Inserting record #" + finalCtr + ": " + s);
                        }
                    });

                    handler.addRagam(ctr, currentRecord[0], currentRecord[1], currentRecord[2], currentRecord[3]);
                    ct = 0;
                }
            }
            main.totalAdded = ctr;
            handler.closeDB();
            br.close();
        } catch (final IOException e) {
            Toast.makeText(main.getApplicationContext(), "Error while writing to db: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();
        Toast.makeText(main.getApplicationContext(), "Added: " + main.addedMelakartaRagas + " Melakartas, " + main.addedJanyaRagas + " Janya ragas, " + main.addedArohanams + " Arohanams, " + main.addedAvarohanams + " Avarohanams, " + main.totalAdded + " in total.", Toast.LENGTH_LONG).show();
    }
}

class GetRagamFromDB extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;
    private MainActivity main;
    private String aroh, avaroh;
    private Ragam ragam;

    GetRagamFromDB (MainActivity activity, String _aroh, String _avaroh) {
        dialog = new ProgressDialog(activity);
        main = activity;
        aroh = _aroh;
        avaroh = _avaroh;
    }

    @Override
    protected void onPreExecute() {
        dialog.setMessage("Scanning database, please wait...");
        dialog.show();
        dialog.setCancelable(false);
    }
    @Override
    protected Void doInBackground(Void... args) {
        // do background work here
        DBHandler handler = new DBHandler(main.getBaseContext());
        ragam = handler.getRagam(aroh, avaroh);
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();
        if (ragam == null)
            Toast.makeText(main.getApplicationContext(), "No ragam exists with the given combination of arohanam: " + aroh + " and avarohanam: " + avaroh, Toast.LENGTH_LONG).show();
        else if (ragam.getMelakartaRagaName().equals("-")) Toast.makeText(main.getApplicationContext(), "Ragam name: " + ragam.getMelakartaRagaName() + ". It is a melakarta ragam!", Toast.LENGTH_LONG).show();
        else Toast.makeText(main.getApplicationContext(), "Ragam name: " + ragam.getRagamName() + ". It is a derivative of the melakarta ragam: " + ragam.getMelakartaRagaName(), Toast.LENGTH_LONG).show();
    }
}