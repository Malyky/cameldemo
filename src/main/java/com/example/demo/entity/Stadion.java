package com.example.demo.entity;

public class Stadion {


    private String name;

    private int sitzplaetze;

    private boolean ueberdacht;

    public Stadion(String name, int sitzplaetze, boolean ueberdacht) {
        this.name = name;
        this.sitzplaetze = sitzplaetze;
        this.ueberdacht = ueberdacht;
    }

    public Stadion() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSitzplaetze() {
        return sitzplaetze;
    }

    public void setSitzplaetze(int sitzplaetze) {
        this.sitzplaetze = sitzplaetze;
    }

    public boolean isUeberdacht() {
        return ueberdacht;
    }

    public void setUeberdacht(boolean ueberdacht) {
        this.ueberdacht = ueberdacht;
    }
}
