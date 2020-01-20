package com.example.ahsan.shild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ShildActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    SharedPreferences settings;
    String myKey;

    private EditText editText;
    private Button voiceButton;
    private Button logOut;

    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    private String code = "help";

    private Thread repeatTaskThread;
    ArrayList<String> matches;


    private GoogleMap mMap;

    GoogleApiClient mGoogleaApiClint;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private TextView keyText;




    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference mNotifications;
    private DatabaseReference mDatabase;
    private DatabaseReference mHelpingHandDatabase;


    private LatLng helpLocation;
    private FirebaseUser current_user;
    private String helpingHandContactNo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shild);

        editText = (EditText) findViewById(R.id.editText);
        keyText = findViewById(R.id.keyText);
        logOut = findViewById(R.id.logout_button);


        Intent intent = getIntent();
        String score = intent.getStringExtra("KEY");
        //String number = intent.getStringExtra("NUMBER");

        //helpingHand = Integer.parseInt(number);

        myKey = String.valueOf(score);

        keyText.setText(myKey);


        mAuth = FirebaseAuth.getInstance();




//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user == null) {
//
//                    Toast.makeText(ShildActivity.this, "Great Job!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(ShildActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        };

        current_user = FirebaseAuth.getInstance().getCurrentUser();


        mNotifications = FirebaseDatabase.getInstance().getReference().child("notifications");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_user.getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String helpingHandId = dataSnapshot.child("my_true_hand").getValue().toString();
                mHelpingHandDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(helpingHandId);

                mHelpingHandDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(ShildActivity.this,"yes 2",Toast.LENGTH_SHORT).show();

                        helpingHandContactNo  = dataSnapshot.child("contact_no").getValue().toString();
                        Toast.makeText(ShildActivity.this,"yes 3",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Toast.makeText(ShildActivity.this,"yes 4",Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toast.makeText(ShildActivity.this,"yes 5",Toast.LENGTH_SHORT).show();






        //voice part

        checkPermission();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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

                    if (matches.get(0).toString().equals(myKey)) {
                        Toast.makeText(ShildActivity.this, "Um in Trouble!", Toast.LENGTH_LONG).show();

                        helpMeh();


                    } else if (matches.get(0).toString().indexOf(myKey) != -1) {
                        Toast.makeText(ShildActivity.this, "Um in Trouble!", Toast.LENGTH_LONG).show();

                        helpMeh();


                    }
                    editText.setText(matches.get(0));

                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        voiceButton = (Button) findViewById(R.id.button);

        voiceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSpeechRecognizer.stopListening();
                        editText.setHint("You will see the input here");

                        break;
                    case MotionEvent.ACTION_DOWN:
                        editText.setText("");
                        editText.setHint("Recognizing.....");
                        voiceButton.setText("On my job!");

                        RepeatTask();
                        break;
                }

                return false;
            }
        });


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ShildActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });



    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.shild_menu,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.action_id_logout){
//            mAuth.signOut();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void helpMeh() {

        //String helpingHandId;


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               String helpingHandId = dataSnapshot.child("my_true_hand").getValue().toString();
                mHelpingHandDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(helpingHandId);
               //helpingHandContactNo  = dataSnapshot.child("contact_no").getValue().toString();            //------********-----//

                HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from",current_user.getUid());
                notificationData.put("message","Help");

                mNotifications.child(helpingHandId).push().setValue(notificationData);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





         //------********-----//

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("helpRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        helpLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(helpLocation).title("Trouble Occurs here!"));

        Toast.makeText(ShildActivity.this,"Calling: "+helpingHandContactNo,Toast.LENGTH_LONG).show();







        //---------------*******CALL********------------------------

        Intent callIntent = new Intent(Intent.ACTION_CALL);                                                //------********-----//
        callIntent.setData(Uri.parse(helpingHandContactNo));
        //callIntent.setData(Uri.parse("tel:+8801778619115"));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);


    }


    //voice

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();

            }
        }
    }

    private void RepeatTask()
    {
        repeatTaskThread = new Thread()
        {
            public void run()
            {
                while (true)
                {

//                    FetchURL fu = new FetchURL();
//                    fu.Run("http://192.168.0.10/joins.txt");
//                    String o = fu.getOutput();
                    // Update TextView in runOnUiThread
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                            //txtJoins.setText("Last connections: \n" + o);
                        }
                    });
                    try
                    {
                        // Sleep for
                        Thread.sleep(5000);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        repeatTaskThread.start();
    }



    //map


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClint();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClint() {
        mGoogleaApiClint = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleaApiClint.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        //Toast.makeText(DriverMapActivity.this,"DriverMapActivity",Toast.LENGTH_LONG).show();

        if (getApplicationContext()!=null){



            mLastLocation = location;
            LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LocationFound");

            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));

        } else {

            Toast.makeText(ShildActivity.this,"Not found",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);       // high will decrease battery fast

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleaApiClint, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(firebaseAuthListener);

//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            Intent intent = new Intent(ShildActivity.this,StartActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mAuth.removeAuthStateListener(firebaseAuthListener);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DriverAvailable");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId );
    }




}
