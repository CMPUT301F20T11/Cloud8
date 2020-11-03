package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity implements ProfileEditDialog.onEditListener {
    private static final String TAG = ProfileActivity.class.getName();
    private Button editButton, logoutButton;
    private TextView uName, uID, email, phone;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private String userName, userID, userEmail, currentEmail, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        uName = findViewById(R.id.username);
        uID = findViewById(R.id.uid);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        if (user != null) {
            userID = user.getUid();
            currentEmail = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(currentEmail);

        editButton = findViewById(R.id.edit_profile_btn);
        logoutButton = findViewById(R.id.logout_btn);

        editButton.setOnClickListener(view -> new ProfileEditDialog().show(getSupportFragmentManager(), "EDIT PROFILE"));
        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this,
                    SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadProfile();
    }

    private void loadProfile() {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null) {
                    userName = doc.getString("username");
                    userEmail = doc.getString("email");
                    userPhone = doc.getString("phone");
                    uName.setText(userName);
                    uID.setText(userID);
                    email.setText(userEmail);
                    phone.setText(userPhone);
                }
            }
        });
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    @Override
    public void onEditOk(String email, String phone) {
        docRef = db.collection("users").document(currentEmail);
        docRef
                .update("phone", phone,
                        "email", email)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Phone number " +
                        "successfully changed!"))
                .addOnFailureListener(e -> Log.d(TAG, "Phone number failed to" +
                        " change!" + e.toString()));
        loadProfile();
    }
}
