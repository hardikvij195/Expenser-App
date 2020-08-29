package com.hvtechnologies.expensesapp;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class SpinnerListAdapter extends ArrayAdapter<ExpClass> {


    public SpinnerListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }



}
