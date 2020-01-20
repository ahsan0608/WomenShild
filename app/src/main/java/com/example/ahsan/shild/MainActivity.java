package com.example.ahsan.shild;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    //private String code = "help";
    private static final int RC_SIGN_IN =1;


    private EditText mEmail, mPassword;
    private Button mLogin, mReg;
    private SignInButton googleButton;

    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private ProgressBar mprogressBar;
    private GoogleApiClient mGoogleApiClient;





    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this,ShildActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };



        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
        mReg = (Button) findViewById(R.id.registration);

        mLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:

                        mLogin.getBackground().setAlpha(255);

                        final String email = mEmail.getText().toString();
                        final String pass = mPassword.getText().toString();

                        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){

                            Toast.makeText(MainActivity.this,"Field can't be empty",Toast.LENGTH_SHORT).show();

                        } else {

                            mprogressBar.setVisibility(View.VISIBLE);

                            mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        //mprogressBar.setVisibility(View.INVISIBLE);
                                        //FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                        Toast.makeText(MainActivity.this, "Failed Login: ", Toast.LENGTH_SHORT).show();
                                        mprogressBar.setVisibility(View.INVISIBLE);

                                    } else {

                                        Toast.makeText(MainActivity.this, "Sign in Successful ", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mprogressBar.setVisibility(View.INVISIBLE);
                                        startActivity(intent);
                                        finish();

                                        //String deviceToken = FirebaseInstanceId.getInstance().getToken();
//                            String userId = mAuth.getCurrentUser().getUid();
//
//                            mUserDatabase.child(userId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//
//                                }
//                            });





                                    }
                                }
                            });

                        }





                        break;
                    case MotionEvent.ACTION_DOWN:

                        mLogin.getBackground().setAlpha(90);

                        break;
                }
                return false;
            }
        });


        mReg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mReg.getBackground().setAlpha(95);

                        break;
                    case MotionEvent.ACTION_UP:
                        mReg.getBackground().setAlpha(255);
                        Intent regActivity = new Intent(MainActivity.this,RegisterActivity.class);
                        regActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(regActivity);

                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }





    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}
