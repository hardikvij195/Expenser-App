package com.hvtechnologies.expensesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    RelativeLayout r1 ;
    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            r1.setVisibility(View.VISIBLE);

        }
    };

    ImageButton Login ;
    FirebaseAuth mAuth ;
    GoogleSignInClient mGoogle ;
    public static int RC_SIGN_IN = 1 ;

    TextView PrivacyP ;
    CheckBox CheckBtn ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme1);
        setContentView(R.layout.activity_main);
        FirebasePer.getDatabase();



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CheckBtn = (CheckBox)findViewById(R.id.chbox);
        PrivacyP = (TextView)findViewById(R.id.textView6);

        r1 = (RelativeLayout)findViewById(R.id.rely1);
        handler.postDelayed(runnable , 1500);
        Login = (ImageButton) findViewById(R.id.LoginActBtn);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogle = GoogleSignIn.getClient(MainActivity.this , gso);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            Intent mainIntent = new Intent(MainActivity.this, ExpHomeActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();

        }

        PrivacyP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri3 = Uri.parse("http://hvtechnologies.github.io/ExpenserApp_PrivacyPolicy/"); // missing 'http://' will cause crashed
                Intent intent3 = new Intent(Intent.ACTION_VIEW, uri3);
                startActivity(intent3);


            }
        });

        CheckBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    Login.setEnabled(true);

                }else {

                    Login.setEnabled(false);

                }

            }
        });



    }


    private void signIn() {

        Intent signInIntent = mGoogle.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Toast.makeText(MainActivity.this ,"Failed -- "  + e.getMessage(), Toast.LENGTH_SHORT ).show();


                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this ,"signInWithCredential:success" , Toast.LENGTH_SHORT ).show();
                            Intent mainIntent = new Intent(MainActivity.this, ExpHomeActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mainIntent);
                            finish();

                        } else {

                            Toast.makeText(MainActivity.this ,"Failed -- " + task.getException().getMessage() , Toast.LENGTH_SHORT ).show();


                        }

                    }
                });


    }








}
