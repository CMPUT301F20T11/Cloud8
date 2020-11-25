package com.example.booktracker.ui;

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
import com.example.booktracker.boundary.NotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private Button signUpButton;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    //==============Get db=================
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    //===============================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Button signInButton = findViewById(R.id.sign_in_button);
        Button signUpButton = findViewById(R.id.sign_up_button);
        emailEditText =(EditText) findViewById(R.id.email_field);
        passwordEditText =(EditText) findViewById(R.id.password_field);
        mAuth = FirebaseAuth.getInstance();

        //==============Get db=================
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        //===============================
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
                    //send the email to home activity
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    String email = emailEditText.getText().toString().trim();
                    intent.putExtra(EXTRA_MESSAGE,email);
                    // Update user token for device
                    NotificationService service = new NotificationService();
                    service.updateToken();

                    startActivity(intent);

                }else{
                    Toast.makeText(SignInActivity.this, "Login unsuccessful, please check your credentials!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        });
    }
}
