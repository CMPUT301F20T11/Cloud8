package com.example.booktracker.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText new_username_field, editTextPassword, editTextEmailAddress, editTextPhone;
    private Button confirmSignUp;

    private FirebaseAuth mAuth;
    //==============Get db=================
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    //===============================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        confirmSignUp = (Button) findViewById(R.id.sign_up_send);
        confirmSignUp.setOnClickListener((View.OnClickListener) this);

        new_username_field = (EditText) findViewById(R.id.new_username_field);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        //==============Get db=================
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("users");

        //===============================

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_send:
                registerUser();
        }
    }
        private void registerUser () {
            final String username = new_username_field.getText().toString().trim();
            final String email = editTextEmailAddress.getText().toString().trim();
            final String phoneNumber = editTextPhone.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty()) {
                new_username_field.setError("A name is required!");
                new_username_field.requestFocus();
                return;
            }
            if (phoneNumber.isEmpty()) {
                editTextPhone.setError("A phone number is required for contact.");
                editTextPhone.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                editTextEmailAddress.setError("An email is required!");
                editTextEmailAddress.requestFocus();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmailAddress.setError("Please provide a valid email!");
                editTextEmailAddress.requestFocus();
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("A password is required!");
                editTextPassword.requestFocus();
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("password needs to be at least 6 characters.      ");
                editTextPassword.requestFocus();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //======================store user data in firestore================================
                                HashMap<String,String> data = new HashMap<String,String>();
                                if (email.length() > 0){
                                    data.put("username",username);
                                    data.put("phone",phoneNumber);
                                    collectionReference
                                            .document(email)
                                            .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUpActivity.this, "User successfully registered!", Toast.LENGTH_LONG).show();
                                            //====Ivan: made it so that the activity automatically exits==
                                            try{
                                                Thread.sleep(2000);
                                            }catch (InterruptedException e){
                                                Thread.currentThread().interrupt();
                                            }
                                            finish();
                                            //============================================================
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, "Failed to register user, please try again.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                //==========================================================
                            } else {
                                Toast.makeText(SignUpActivity.this, "Failed to register user, please try again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

}

