package com.hvtechnologies.expensesapp;

public class ExpClass {

    String Key ,Date , Note ;
    int Amount ;
    boolean Credit ;

    public ExpClass(String key, String date, String note, int amount, boolean credit) {
        Key = key;
        Date = date;
        Note = note;
        Amount = amount;
        Credit = credit;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public boolean isCredit() {
        return Credit;
    }

    public void setCredit(boolean credit) {
        Credit = credit;
    }
}
