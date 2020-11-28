package com.example.booktracker.entities;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Notification {
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    protected String toEmail, fromEmail, toToken;
    protected String serverKey = "key=AAAAl96duTc:APA91bEKW32AUHrIY88CsZ-o2M58X0JfSnfXs2j8HBpW-p5SdxYjsDX9OV6RKGc43qgoGS7T0ocW2dIBjWDuFER_OqenB8pGWVmJZaQDKw5p1LEadZSC3KnM3X0fjQXZbYyUW7Bahndc";
    protected String contentType = "application/json";

    /**
     * Contructor for the abstract notification class
     * Initializes the firestore references
     * @param from
     * @param to
     */
    public Notification(String from, String to) {
        fromEmail = from;
        toEmail = to;
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(fromEmail);
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getToEmail() {
        return toEmail;
    }
}