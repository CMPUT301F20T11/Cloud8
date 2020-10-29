package com.example.booktracker.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.ui.Activities.MainActivity;
import com.example.booktracker.ui.Activities.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private Button signUpButton;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button signInButton = findViewById(R.id.sign_in_button);
        Button signUpButton = findViewById(R.id.sign_up_button);
        emailEditText =(EditText) findViewById(R.id.email_field);
        passwordEditText =(EditText) findViewById(R.id.password_field);
        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString().trim();
                final String password = passwordEditText.getText().toString().trim();
                userLogin(email, password);


            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void userLogin(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("An email is required!");
            emailEditText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please Enter a valid email!");
            emailEditText.requestFocus();
        }
        if(password.isEmpty()){
            passwordEditText.setError("A password is required!");
            passwordEditText.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "User successfully signed in!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignInActivity.this, MainActivity.class));


                }else{
                    Toast.makeText(SignInActivity.this, "Login unsuccessful, please check your credentials!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        });
    }
}
