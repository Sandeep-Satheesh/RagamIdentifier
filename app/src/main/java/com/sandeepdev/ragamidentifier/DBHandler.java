package com.sandeepdev.ragamidentifier;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DBHandler extends SQLiteOpenHelper {

    private Context c;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ragamsDB";
    private static final String TABLE_RAGAMS = "ragams";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "ragamName";
    private static final String KEY_MELAKARTANAME = "melakartaRagamName";
    private static final String KEY_AROHANAM = "arohanam";
    private static final String KEY_AVAROHANAM = "avarohanam";

    private SQLiteDatabase db;
    private ContentValues values;

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;
        //3rd argument to be passed is CursorFactory instance  
    }
    void openDBForBatchInsert() {
        db = this.getWritableDatabase();
        values = new ContentValues();
    }
    void closeDB() {
        db.close();
        values.clear();
    }
    // Creating Tables  
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAGAMS);
        String CREATE_RAGAMS_TABLE = "CREATE TABLE " + TABLE_RAGAMS + "("
                + KEY_ID + " INTEGER," + KEY_NAME + " TEXT, "
                + KEY_AROHANAM + " TEXT, " + KEY_AVAROHANAM + " TEXT," + KEY_MELAKARTANAME + " TEXT)";
        db.execSQL(CREATE_RAGAMS_TABLE);
    }

    // Upgrading database  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed  
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RAGAMS);

        // Create tables again  
        onCreate(db);
    }

    // code to add the new ragam  
    void addRagam(int id, String ragaName, String aroh, String avaroh, String melakartaName) {
        db.execSQL("INSERT INTO " + TABLE_RAGAMS + " VALUES (" + id + ",\"" + ragaName + "\", \"" + aroh + "\", \"" + avaroh + "\", \"" + melakartaName + "\")");
    }

    // code to get the single ragam  
    Ragam getRagam(String aroh, String avaroh) {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RAGAMS, new String[] { KEY_ID,
                        KEY_NAME, KEY_AROHANAM, KEY_AVAROHANAM, KEY_MELAKARTANAME }, KEY_AROHANAM + "=? AND " + KEY_AVAROHANAM + "=?",
                new String[] { aroh, avaroh }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor == null || cursor.getCount() == 0) return null;
        Ragam ragam = new Ragam(cursor.getInt(0),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        cursor.close();
        db.close();
        // return ragam
        return ragam;
    }

    public int getRagamsCount() {
        db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RAGAMS, new String[] {KEY_ID},null, null, null, null, null);
        // return count
        int ct = cursor.getCount();
        db.close();
        cursor.close();
        return ct;
    }
}  
