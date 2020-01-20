package com.example.ahsan.shild;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RegisterActivity extends AppCompatActivity {


    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mBio;
    private EditText mContactNo;

    private Button mRegButton;
    private ImageButton mSetupImageBtn;
    private DatabaseReference mDatabase;

    private ProgressBar mprogressBar;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private DatabaseReference mdatabaseUsers;
    private StorageReference mStorageImage;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mSetupImageBtn = (ImageButton) findViewById(R.id.profile_image_button);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("profile_images ");

        mNameField = (EditText) findViewById(R.id.name_field);
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);
        mRegButton = (Button) findViewById(R.id.reg_button);
        mContactNo = findViewById(R.id.number_field);
        mBio = findViewById(R.id.bio_field);

        mprogressBar = (ProgressBar) findViewById(R.id.progressBar);


        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery_intent = new Intent();
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent, GALLERY_REQUEST);
            }
        });


        mRegButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mRegButton.getBackground().setAlpha(90);
                        break;

                    case MotionEvent.ACTION_UP:
                        mRegButton.getBackground().setAlpha(255);
                        startRegistration();
                        break;
                }

                return false;
            }
        });


    }

    private void startRegistration() {


        final String name = mNameField.getText().toString().trim();
        final String bio = mBio.getText().toString().trim();
        final String email1 = mEmailField.getText().toString().trim();
        final String password1 = mPasswordField.getText().toString().trim();
        final String contactNo = mContactNo.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email1) || TextUtils.isEmpty(password1) || mImageUri == null){

            Toast.makeText(RegisterActivity.this,"Field can't be empty",Toast.LENGTH_SHORT).show();

        }else {

            //Toast.makeText(RegisterActivity.this,"Yes in registration 2",Toast.LENGTH_SHORT).show();

            mprogressBar.setVisibility(View.VISIBLE);
            mRegButton.setText("Signing in..");

            mAuth.createUserWithEmailAndPassword(email1, password1).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    //Toast.makeText(RegisterActivity.this,"Yes in registration 3",Toast.LENGTH_SHORT).show();

                    if (!task.isSuccessful()){
                        //Toast.makeText(RegisterActivity.this,"Failed Registration!!",Toast.LENGTH_LONG).show();
                        mprogressBar.setVisibility(View.INVISIBLE);
                        //FirebaseAuthException e = (FirebaseAuthException )task.getException();
                        Toast.makeText(RegisterActivity.this, "Failed Registration: ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Please wait a while...!!",Toast.LENGTH_SHORT).show();

                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        String user_id = mAuth.getCurrentUser().getUid();
                        String searchName = name.toLowerCase();
                        mdatabaseUsers = mDatabase.child(user_id);

                        mdatabaseUsers.child("name").setValue(name);
                        mdatabaseUsers.child("bio").setValue(bio);
                        mdatabaseUsers.child("search_name").setValue(searchName);
                        mdatabaseUsers.child("device_token").setValue(deviceToken);
                        mdatabaseUsers.child("contact_no").setValue("tel:+88"+contactNo);


                        //Toast.makeText(RegisterActivity.this,"Yes 1",Toast.LENGTH_SHORT).show();


                        StorageReference filePath = mStorageImage.child(mImageUri.getLastPathSegment());
                        filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                @SuppressWarnings("VisibleForTests") String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                mdatabaseUsers.child("image").setValue(downloadUrl);

                                //Toast.makeText(RegisterActivity.this,"Yes 2 image",Toast.LENGTH_SHORT).show();

                            }
                        });


                        //Toast.makeText(RegisterActivity.this,"Yes 3",Toast.LENGTH_SHORT).show();

                        mprogressBar.setVisibility(View.INVISIBLE);
                        Intent mainIntent = new Intent(RegisterActivity.this,SearchActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                mSetupImageBtn.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }


}
