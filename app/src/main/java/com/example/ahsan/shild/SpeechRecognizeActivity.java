package com.example.ahsan.shild;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechRecognizeActivity extends AppCompatActivity {


    private EditText editText;
    private Button voiceButton;
    private Button saveKeyButton;

    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;
    private FirebaseAuth mAuth;

    private String code = "help";

    //private Thread repeatTaskThread;

    ArrayList<String> matches;



    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    private String key= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_recognize);

        mAuth = FirebaseAuth.getInstance();

        saveKeyButton = (Button) findViewById(R.id.saveKey_button);


        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("My current location");



        checkPermission();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());

        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {

                matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {

                    if (matches.get(0).toString().equals(code)) {
                        Toast.makeText(SpeechRecognizeActivity.this,"Um in Trouble!",Toast.LENGTH_LONG).show();
                    }else if (matches.get(0).toString().indexOf(code) != -1){
                        Toast.makeText(SpeechRecognizeActivity.this,"Um in Trouble!",Toast.LENGTH_LONG).show();
                    }
                    editText.setText(matches.get(0));


                    key = matches.get(0).toString();


                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        editText = (EditText) findViewById(R.id.editText);
        voiceButton = (Button) findViewById(R.id.button);

        voiceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:

                        mSpeechRecognizer.stopListening();
                        //saveKeyButton.setEnabled(false);
                        voiceButton.getBackground().setAlpha(255);
                        voiceButton.setText("Tap and Speak");
                        editText.setHint("Check your key here...");


                        break;
                    case MotionEvent.ACTION_DOWN:

                        //saveKeyButton.setEnabled(false);

                        voiceButton.setText("Listening");
                        voiceButton.getBackground().setAlpha(100);
                        editText.setText("");
                        editText.setHint("Listening.....");
                        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

                        //RepeatTask();
                        break;
                }

                return false;
            }
        });


        saveKeyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:

                        saveKeyButton.getBackground().setAlpha(255);
                        Intent i = new Intent(SpeechRecognizeActivity.this,ShildActivity.class);
                        i.putExtra("KEY",key);
                        //i.putExtra("NUMBER",number);
                        startActivity(i);


                        break;
                    case MotionEvent.ACTION_DOWN:

                        saveKeyButton.getBackground().setAlpha(90);


                        break;
                }

                return false;
            }
        });
    }







    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();

            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(SpeechRecognizeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

//    private void RepeatTask()
//    {
//        repeatTaskThread = new Thread()
//        {
//            public void run()
//            {
//                while (true)
//                {
//
//                    // Update TextView in runOnUiThread
//                    runOnUiThread(new Runnable()
//                    {
//                        @Override
//                        public void run()
//                        {
//                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//                            //txtJoins.setText("Last connections: \n" + o);
//                        }
//                    });
//                    try
//                    {
//                        // Sleep for
//                        Thread.sleep(5000);
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        repeatTaskThread.start();
//    }
}
