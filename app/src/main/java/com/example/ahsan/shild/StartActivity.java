package com.example.ahsan.shild;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegister;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegister = findViewById(R.id.reg_button);
        mLogin = findViewById(R.id.login_button);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentR = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intentR);
            }
        });
    }
}
