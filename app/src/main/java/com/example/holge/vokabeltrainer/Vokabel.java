package com.example.holge.vokabeltrainer;

/**
 * Created by holge on 27.08.2016.
 * Vokabel class
 */
public class Vokabel {
    public Vokabel(String deutsch, String latein, int lektion, int buch)
    {
        this.deutsch = deutsch;
        this.latein = latein;
        this.lektion = lektion;
        this.buch = buch;
    }

    private String latein;
    private String deutsch;
    private int buch;
    private int lektion;

    public int getBuch() {
        return buch;
    }

    public void setBuch(int buch) {
        this.buch = buch;
    }

    public int getLektion() {
        return lektion;
    }

    public void setLektion(int lektion) {
        this.lektion = lektion;
    }

    public CharSequence getLatein() {
        return latein;
    }

    public void setLatein(String latein) {
        this.latein = latein;
    }

    public CharSequence getDeutsch() {
        return deutsch;
    }

    public void setDeutsch(String deutsch) {
        this.deutsch = deutsch;
    }
}
