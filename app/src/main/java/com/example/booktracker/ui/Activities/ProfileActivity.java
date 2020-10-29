package com.example.booktracker.ui.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    }


    @Override
    public void onBackPressed(){
        // code here to show dialog
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
}
