package com.example.emsismartpresence;

public class RattrapageItem {
    private String module;
    private String date;
    private String heure;

    public RattrapageItem(String module, String date, String heure) {
        this.module = module;
        this.date = date;
        this.heure = heure;
    }

    public String getModule() { return module; }
    public String getDate() { return date; }
    public String getHeure() { return heure; }
}
