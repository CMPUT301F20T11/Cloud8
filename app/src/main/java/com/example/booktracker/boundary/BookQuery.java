package com.example.booktracker.boundary;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class BookQuery {
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    protected String email;
    protected StorageReference storageReference;
    protected FirebaseAuth auth;
    protected FirebaseUser user;
    protected String uid;

    /**
     * Constructor will connect to database and initialized document
     * pertaining to user
     *
     * @param userEmail this must be a valid email that is in the database
     * @author Ivan Penales
     */
    public BookQuery(String userEmail) {
        email = userEmail;
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(userEmail);
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            uid = user.getUid();
        }
    }
    public BookQuery() {
        db = FirebaseFirestore.getInstance();
    }
}
