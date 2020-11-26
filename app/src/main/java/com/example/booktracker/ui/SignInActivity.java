package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.boundary.NotificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SignInActivity extends AppCompatActivity {
    private Button signInButton;
    private Button signUpButton;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth mAuth;
    private long lastClickTime = 0;
    //==============Get db=================
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    //=====================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        emailEditText = findViewById(R.id.email_field);
        passwordEditText = findViewById(R.id.password_field);
        mAuth = FirebaseAuth.getInstance();

        //==============Get db=================
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");
        //=====================================
        signInButton.setOnClickListener(v -> {
            if ((SystemClock.elapsedRealtime() - lastClickTime) < 1000) {
                return;
            }
            final String email = emailEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString().trim();
            userLogin(email, password);
            lastClickTime = SystemClock.elapsedRealtime();
        });

        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void userLogin(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("An email is required!");
            emailEditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please Enter a valid email!");
            emailEditText.requestFocus();
        }

        if (password.isEmpty()) {
            passwordEditText.setError("A password is required!");
            passwordEditText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignInActivity.this, "User successfully signed in!", Toast.LENGTH_SHORT).show();
                // send the email to home activity
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                String loginEmail = emailEditText.getText().toString().trim();
                intent.putExtra(EXTRA_MESSAGE, loginEmail);

                // Update user token for device
                NotificationService service = new NotificationService();
                service.updateToken();

                startActivity(intent);
                //finish();

            } else {
                Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
