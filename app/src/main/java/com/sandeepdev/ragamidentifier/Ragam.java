package com.sandeepdev.ragamidentifier;

public class Ragam {
    private int id;
    private String ragamName;
    private String melakartaRagaName;
    private String arohanam;
    private String avarohanam;

    public Ragam(){   }

    Ragam(int _id, String _ragamName, String _arohanam, String _avarohanam, String _melakartaRagamName){
        this.id = _id;
        this.arohanam = _arohanam;
        this.ragamName = _ragamName;
        this.avarohanam = _avarohanam;
        melakartaRagaName = _melakartaRagamName;
    }

    public String getMelakartaRagaName() {
        return melakartaRagaName;
    }

    String getRagamName() {
        return ragamName;
    }

    int getId() {
        return id;
    }

    String getArohanam() {
        return arohanam;
    }

    String getAvarohanam() {
        return avarohanam;
    }
}