package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;

import javax.security.auth.login.LoginException;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void sendSignUp(View view){
        //
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
