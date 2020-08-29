package com.hvtechnologies.expensesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
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

import static java.security.AccessController.getContext;

public class ExpHomeActivity extends AppCompatActivity implements ExpListAdapter.OnNoteListener {


    DrawerLayout myDrawer;
    NavigationView navigationView;
    ActionBarDrawerToggle myTog ;
    TextView Welcome , Balance ;
    FirebaseAuth mAuth ;
    GoogleSignInClient mGoogle ;
    RecyclerView recyclerView ;
    ExpListAdapter adapter ;
    List<ExpClass> mListTT = new ArrayList<>() ;
    DatabaseReference Ref1 , Ref2 ;
    String Uid ;
    Integer Bal = 0  ;
    private AdView mAdView;
    int CountAds ;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_home);
        FirebasePer.getDatabase();

        Welcome = (TextView)findViewById(R.id.Welcome);
        Balance = (TextView)findViewById(R.id.Balance);
        GetCount();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uid = user.getUid();
        MobileAds.initialize(this, "ca-app-pub-1000976024491613/7664285065");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("8D5B3A1F0658C0E52F9126A55ABFC604").build();

        mAdView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1000976024491613/7281335205");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView1);
        adapter = new ExpListAdapter(  mListTT , getApplicationContext() , this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);



        Ref2 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/") ;
        Ref2.keepSynced(true);
        Ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mListTT.clear();
                adapter.notifyDataSetChanged();
                Date date = new Date();  // to get the date
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // getting date in this format
                final String formattedDate = df.format(date.getTime());

                Bal = 0 ;
                if(dataSnapshot.exists()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                        String Id = dataSnapshot1.getKey();
                        String Date = dataSnapshot1.child("Date").getValue().toString();
                        String Note = dataSnapshot1.child("Note").getValue().toString();
                        String Amount = dataSnapshot1.child("Amount").getValue().toString();
                        String Credit = dataSnapshot1.child("CD").getValue().toString();
                        int Amt = Integer.parseInt(Amount);

                        if(Credit.equals("C")){

                            Bal = Bal + Amt ;

                        }else {

                            Bal = Bal - Amt;
                        }


                        if(Id.contains(formattedDate)) {

                            boolean cr ;
                            if(Credit.equals("C")){

                                cr = true;
                            }else {

                                cr = false;
                            }
                            mListTT.add(new ExpClass(Id , Date , Note , Amt , cr));
                            adapter.notifyDataSetChanged();
                        }

                    }

                    Collections.reverse(mListTT);
                    adapter.notifyDataSetChanged();

                }
                Balance.setText("Balance :  " + Bal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Welcome.setText("Welcome,");
        myDrawer = (DrawerLayout)findViewById(R.id.myDraw);
        navigationView = (NavigationView)findViewById(R.id.Nav);
        myTog = new ActionBarDrawerToggle(this , myDrawer , R.string.app_name , R.string.app_name);
        myDrawer.addDrawerListener(myTog);
        myTog.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogle = GoogleSignIn.getClient(ExpHomeActivity.this , gso);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.Credit:


                        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpHomeActivity.this)
                                .setCancelable(false);
                        View mView = getLayoutInflater().inflate(R.layout.credit_dialog, null);
                        final EditText Amount = (EditText) mView.findViewById(R.id.editText2);
                        final EditText Note = (EditText) mView.findViewById(R.id.editText3);
                        final Button canc1 = (Button) mView.findViewById(R.id.button2);
                        final Button ok1 = (Button) mView.findViewById(R.id.button);
                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        ok1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if( !Amount.getText().toString().isEmpty()){

                                    if(!Note.getText().toString().isEmpty()){

                                        Date date = new Date();  // to get the date
                                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); // getting date in this format
                                        final String formattedDate = df.format(date.getTime());
                                        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy"); // getting date in this format
                                        final String RealDate = df2.format(date.getTime());

                                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/" + Uid + "/" + formattedDate + "/") ;
                                        Ref1.keepSynced(true);
                                        HashMap<String,String> dataMap = new HashMap<String, String>();
                                        dataMap.put("Amount" , Amount.getText().toString());
                                        dataMap.put("Note" , Note.getText().toString());
                                        dataMap.put("Date" , RealDate);
                                        dataMap.put("CD" ,"C");
                                        Ref1.setValue(dataMap);
                                        adapter.notifyDataSetChanged();


                                    }else {

                                        Date date = new Date();  // to get the date
                                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); // getting date in this format
                                        final String formattedDate = df.format(date.getTime());
                                        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy"); // getting date in this format
                                        final String RealDate = df2.format(date.getTime());

                                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/"+ Uid + "/"+ formattedDate + "/") ;
                                        Ref1.keepSynced(true);
                                        HashMap<String,String> dataMap = new HashMap<String, String>();
                                        dataMap.put("Amount" , Amount.getText().toString());
                                        dataMap.put("Note" , "No Note Added");
                                        dataMap.put("Date" , RealDate);
                                        dataMap.put("CD" ,"C");
                                        Ref1.setValue(dataMap);
                                        adapter.notifyDataSetChanged();

                                    }


                                    Toast.makeText(ExpHomeActivity.this , "Transaction Added" , Toast.LENGTH_SHORT).show();

                                }else{

                                    Toast.makeText(ExpHomeActivity.this , "Amount cannot be empty" , Toast.LENGTH_SHORT).show();
                                }

                                SaveCount();
                            }
                        });

                        canc1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                                SaveCount();

                            }
                        });

                        break;

                    case R.id.Debit:



                        final AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(ExpHomeActivity.this)
                                .setCancelable(false);
                        View mView2 = getLayoutInflater().inflate(R.layout.debit_dialog, null);
                        final EditText Amount2 = (EditText) mView2.findViewById(R.id.editText2);
                        final EditText Note2 = (EditText) mView2.findViewById(R.id.editText3);
                        final Button canc2 = (Button) mView2.findViewById(R.id.button2);
                        final Button ok2 = (Button) mView2.findViewById(R.id.button);
                        mBuilder2.setView(mView2);
                        final AlertDialog dialog2 = mBuilder2.create();
                        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog2.show();


                        ok2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if( !Amount2.getText().toString().isEmpty()){

                                    if(!Note2.getText().toString().isEmpty()){

                                        Date date = new Date();  // to get the date
                                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); // getting date in this format
                                        final String formattedDate = df.format(date.getTime());
                                        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy"); // getting date in this format
                                        final String RealDate = df2.format(date.getTime());

                                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/"+ Uid + "/"+ formattedDate + "/") ;
                                        Ref1.keepSynced(true);
                                        HashMap<String,String> dataMap = new HashMap<String, String>();
                                        dataMap.put("Amount" , Amount2.getText().toString());
                                        dataMap.put("Note" , Note2.getText().toString());
                                        dataMap.put("Date" , RealDate);
                                        dataMap.put("CD" ,"D");
                                        Ref1.setValue(dataMap);
                                        adapter.notifyDataSetChanged();


                                    }else {

                                        Date date = new Date();  // to get the date
                                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss"); // getting date in this format
                                        final String formattedDate = df.format(date.getTime());
                                        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy"); // getting date in this format
                                        final String RealDate = df2.format(date.getTime());

                                        Ref1 = FirebaseDatabase.getInstance().getReference("Wallet/"+ Uid + "/"+ formattedDate + "/") ;
                                        Ref1.keepSynced(true);
                                        HashMap<String,String> dataMap = new HashMap<String, String>();
                                        dataMap.put("Amount" , Amount2.getText().toString());
                                        dataMap.put("Note" , "No Note Added");
                                        dataMap.put("Date" , RealDate);
                                        dataMap.put("CD" ,"D");
                                        Ref1.setValue(dataMap);
                                        adapter.notifyDataSetChanged();

                                    }


                                    Toast.makeText(ExpHomeActivity.this , "Transaction Added" , Toast.LENGTH_SHORT).show();

                                }else{

                                    Toast.makeText(ExpHomeActivity.this , "Amount cannot be empty" , Toast.LENGTH_SHORT).show();
                                }
                                SaveCount();

                            }
                        });

                        canc2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog2.dismiss();
                                SaveCount();

                            }
                        });

                        break;

                    case R.id.History:

                        SaveCount();
                        Intent mainIntent1 = new Intent(ExpHomeActivity.this, HistoryActivity.class);
                        startActivity(mainIntent1);

                        break;


                    case R.id.Rate:


                        Uri uri1 = Uri.parse("http://play.google.com/store/apps/details?id=com.hvtechnologies.expensesapp"); // missing 'http://' will cause crashed
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
                        startActivity(intent1);

                        break;


                    case R.id.About:

                        Uri uri2 = Uri.parse("http://hvtechnologies.github.io/Hv-Technologies-Website/"); // missing 'http://' will cause crashed
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                        startActivity(intent2);

                        break;

                    case R.id.Apps:

                        Uri uri3 = Uri.parse("http://play.google.com/store/apps/developer?id=Hv+Technologies"); // missing 'http://' will cause crashed
                        Intent intent3 = new Intent(Intent.ACTION_VIEW, uri3);
                        startActivity(intent3);

                        break;

                    case R.id.LogOut:


                        mAuth.signOut();
                        mGoogle.signOut();
                        Intent mainIntent = new Intent(ExpHomeActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                        finish();

                        break;


                }

                return true;

            }
        });






    }


    public void GetCount(){

        SharedPreferences sh = getSharedPreferences("Count" , MODE_PRIVATE);
        CountAds = sh.getInt("Counting" , 0);

    }

    public void SaveCount(){

        CountAds++;

        SharedPreferences sharedPreferences = getSharedPreferences("Count", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putInt("Counting", CountAds);

        if(CountAds%10 == 0){

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(myTog.onOptionsItemSelected(item)){
            SaveCount();
            return true;

        }
        SaveCount();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void OnNoteClick(int position) {
        SaveCount();

    }
}
