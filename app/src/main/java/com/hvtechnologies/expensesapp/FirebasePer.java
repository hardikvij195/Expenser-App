package com.hvtechnologies.expensesapp;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePer {


    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }


    public static FirebaseDatabase falseDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(false);
        }
        return mDatabase;
    }


}
