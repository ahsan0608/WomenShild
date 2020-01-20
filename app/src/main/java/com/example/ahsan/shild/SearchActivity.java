package com.example.ahsan.shild;


import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.util.Constants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mDatabase;
    private StorageReference mStorageImage;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

//        mAuth.addAuthStateListener(firebaseAuthListener);
//
//        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user==null){
//                    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        };


        mSearchField = (EditText) findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) findViewById(R.id.searchButton);
        mResultList = (RecyclerView) findViewById(R.id.resultList);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));


        mSearchBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mSearchBtn.getBackground().setAlpha(90);

                        break;
                    case MotionEvent.ACTION_UP:
                        mSearchBtn.getBackground().setAlpha(255);
                        String searchText = mSearchField.getText().toString().toLowerCase();
                        firebaseUserSearch(searchText);
                        break;
                }
                return false;
            }
        });

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mAuth.removeAuthStateListener(firebaseAuthListener);
//    }


    private void firebaseUserSearch(String searchText) {



        Query firebaseSearchQuery = mUserDatabase.orderByChild("search_name").startAt(searchText).endAt(searchText + "\\uf8ff");


        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.list_layout,
                UsersViewHolder.class,
                firebaseSearchQuery
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {

                final String trueHandId = getRef(position).getKey();

                viewHolder.setDetails(getApplicationContext(),model.getName(),model.getStatus(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(SearchActivity.this,trueHandId,Toast.LENGTH_LONG).show();
                        String userId = mAuth.getCurrentUser().getUid();
                        mDatabase = mUserDatabase.child(userId);
                        mDatabase.child("my_true_hand").setValue(trueHandId);

                        Intent intent = new Intent(SearchActivity.this, SpeechRecognizeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        };

        mResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        TextView user_status;
        ImageView user_image;

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setDetails(Context context, String name, String status, String image) {
            user_name = mView.findViewById(R.id.name_text);
            user_status = mView.findViewById(R.id.status_text);
            user_image = mView.findViewById(R.id.profile_image);

            user_name.setText(name);
            user_status.setText(status);

            Glide.with(context).load(image).into(user_image);

        }


    }


}
