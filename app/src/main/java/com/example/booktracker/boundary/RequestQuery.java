package com.example.booktracker.boundary;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestQuery implements Callback {
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    protected String toEmail;
    protected FirebaseUser user;
    protected ArrayList<Request> outputRequests;
    protected Book book;
    protected Context context;
    protected RequestQuery instance = this;
    protected RequestCollection requestCollection;
    /**
     * constructor will connect to database and initialized document
     * pertaining to user
     *
     * @param userEmail this must be a valid email that is in the database
     * @author Edlee Ducay
     */
    public RequestQuery(String userEmail, RequestCollection argReqCollection, Context argContext) {
        toEmail = userEmail;
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(userEmail);
        context = argContext;
        requestCollection = argReqCollection;
    }

    public RequestQuery() {
        db = FirebaseFirestore.getInstance();
    }

    public void getRequests() {
        CollectionReference requestsCollection = userDoc.collection("incomingRequests");
        requestsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    outputRequests = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String fromEmail = document.getString("email");
                        String fromUsername = document.getString("username");
                        String isbn = document.getString("bookISBN");
                        book = new Book();
                        getBookQuery query = new getBookQuery(context);
                        query.getABook(isbn, book, instance);

                        Request request = new Request(fromEmail, toEmail, book, context);
                        request.setFromUsername(fromUsername);
                        outputRequests.add(request);
                    }
                    if (outputRequests.size() > 0) {
                        requestCollection.setRequestList(outputRequests);
                        requestCollection.displayRequests();

                    }
                }
            }

        });


    }

    /**
     * This method will be called by the query
     */
    @Override
    public void executeCallback() {
        //updateTextViews(emptyBook);
    }

}


