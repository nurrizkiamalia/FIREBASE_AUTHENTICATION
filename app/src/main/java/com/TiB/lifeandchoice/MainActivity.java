package com.TiB.lifeandchoice;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    Button regis, lgn;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        regis = findViewById(R.id.toRegis);
        lgn = findViewById(R.id.toLgn);

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
    }


    public void nyambungin(View view) {
        switch (view.getId()){
            case R.id.toRegis:
                Intent i1 = new Intent(this,registration.class);
                startActivity(i1);
                break;

            case R.id.toLgn:
                Intent i2 = new Intent(this,login.class);
                startActivity(i2);
                break;
        }
        finish();
    }
}