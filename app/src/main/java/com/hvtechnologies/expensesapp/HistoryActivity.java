package com.hvtechnologies.expensesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements ExpListAdapter.OnNoteListener {



    RecyclerView recyclerView ;
    ExpListAdapter adapter ;
    List<ExpClass> mListTT = new ArrayList<>() ;
    DatabaseReference Ref2 ;
    String Uid ;
    DatabaseReference Ref1 ;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        FirebasePer.getDatabase();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uid = user.getUid();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView1);
        adapter = new ExpListAdapter(  mListTT , getApplicationContext() , this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        MobileAds.initialize(this, "ca-app-pub-1000976024491613/7664285065");
        mAdView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("8D5B3A1F0658C0E52F9126A55ABFC604").build();
        mAdView.loadAd(adRequest);


        Ref2 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/") ;
        Ref2.keepSynced(true);
        Ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mListTT.clear();
                adapter.notifyDataSetChanged();

                if(dataSnapshot.exists()){

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){


                        String Key = dataSnapshot1.getKey();
                        String Date = dataSnapshot1.child("Date").getValue().toString();
                        String Note = dataSnapshot1.child("Note").getValue().toString();
                        String Amount = dataSnapshot1.child("Amount").getValue().toString();
                        String Credit = dataSnapshot1.child("CD").getValue().toString();

                        int Amt = Integer.parseInt(Amount);
                        boolean cr ;
                        if(Credit.equals("C")){

                            cr = true;
                        }else {

                            cr = false;
                        }
                        mListTT.add(new ExpClass(Key , Date , Note , Amt , cr));
                        adapter.notifyDataSetChanged();

                    }
                    Collections.reverse(mListTT);
                    adapter.notifyDataSetChanged();


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void OnNoteClick(final int position) {



        final AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(HistoryActivity.this)
                .setCancelable(false);
        View mView2 = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        final TextView Txt = (TextView) mView2.findViewById(R.id.textView4);
        final EditText EdTxt = (EditText) mView2.findViewById(R.id.editText3);
        final Button save2 = (Button) mView2.findViewById(R.id.button3);
        final Button canc2 = (Button) mView2.findViewById(R.id.button2);
        final Button del2 = (Button) mView2.findViewById(R.id.button);

        mBuilder2.setView(mView2);
        final AlertDialog dialog2 = mBuilder2.create();
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.show();

        final String Date = mListTT.get(position).getDate();
        final String Amount = Integer.toString(mListTT.get(position).getAmount());
        String Note = mListTT.get(position).getNote();

        EdTxt.setText(Note);

        if(mListTT.get(position).isCredit()){

           Txt.setText("Date : " + Date + "\n +" + Amount );

        }else {

            Txt.setText("Date : " + Date + "\n -" + Amount);

        }

        save2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String K = mListTT.get(position).getKey();

                if(mListTT.get(position).isCredit()){

                    if(!EdTxt.getText().toString().isEmpty()) {

                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/" + K  ) ;
                        Ref1.keepSynced(true);
                        HashMap<String,String> dataMap = new HashMap<String, String>();
                        dataMap.put("Amount" , Amount);
                        dataMap.put("Note" , EdTxt.getText().toString());
                        dataMap.put("Date" , Date);
                        dataMap.put("CD" ,"C" );
                        Ref1.setValue(dataMap);
                        adapter.notifyDataSetChanged();

                    } else {


                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/" + K  ) ;
                        Ref1.keepSynced(true);
                        HashMap<String,String> dataMap = new HashMap<String, String>();
                        dataMap.put("Amount" , Amount);
                        dataMap.put("Note" ,"No Note Added");
                        dataMap.put("Date" , Date);
                        dataMap.put("CD" ,"C" );
                        Ref1.setValue(dataMap);
                        adapter.notifyDataSetChanged();

                    }

                }else {

                    if(!EdTxt.getText().toString().isEmpty()) {

                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/" + K  ) ;
                        Ref1.keepSynced(true);
                        HashMap<String,String> dataMap = new HashMap<String, String>();
                        dataMap.put("Amount" , Amount);
                        dataMap.put("Note" , EdTxt.getText().toString());
                        dataMap.put("Date" , Date);
                        dataMap.put("CD" ,"D" );
                        Ref1.setValue(dataMap);
                        adapter.notifyDataSetChanged();

                    } else {

                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/" + K  ) ;
                        Ref1.keepSynced(true);
                        HashMap<String,String> dataMap = new HashMap<String, String>();
                        dataMap.put("Amount" , Amount);
                        dataMap.put("Note" , "No Note Added");
                        dataMap.put("Date" , Date);
                        dataMap.put("CD" ,"D" );
                        Ref1.setValue(dataMap);
                        adapter.notifyDataSetChanged();


                    }

                }

                adapter.notifyDataSetChanged();
                dialog2.dismiss();
                Toast.makeText(HistoryActivity.this , "Transaction Saved" , Toast.LENGTH_SHORT).show();


            }
        });

        del2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Key = mListTT.get(position).getKey();
                Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/"+ Uid + "/"+ Key ) ;
                Ref1.keepSynced(true);
                Ref1.removeValue();
                adapter.notifyDataSetChanged();

                Toast.makeText(HistoryActivity.this , "Transaction Deleted" , Toast.LENGTH_SHORT).show();
                dialog2.dismiss();

            }
        });

        canc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.dismiss();

            }
        });



    }

}
