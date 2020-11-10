package com.example.booktracker.boundary;

import com.example.booktracker.entities.BookCollection;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BookQuery {
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    protected String email;
    /**
     * constructor will connect to database and initialized document pertaining to user
     * @author Ivan Penales
     * @param userEmail this must be a valid email that is in the database
     */
    public BookQuery(String userEmail){
        email = userEmail;
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(userEmail);
    }
    public BookQuery(){
        db = FirebaseFirestore.getInstance();
    }
}
