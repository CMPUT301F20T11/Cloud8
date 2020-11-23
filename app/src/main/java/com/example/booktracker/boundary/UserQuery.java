package com.example.booktracker.boundary;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserQuery {

    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    protected String email, username, phone, token;
    private Context context;
    private User user;

    public UserQuery(String userEmail, Context argContext) {
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(userEmail);

        email = userEmail;
        context = argContext;
        setUserObject();
    }

    private void setUserObject() {
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        username = (String) document.get("username");
                        phone = (String) document.get("phone");
                        token = (String) document.get("token");
                        user = new User(username, email, phone, token);
                    }
                }
            }
        });
    }

    public User getUserObject() {
        return user;
    }
}
