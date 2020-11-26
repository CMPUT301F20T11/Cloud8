package com.example.booktracker.boundary;

import android.content.Context;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    private String curIsbn;
    private String curFromEmail;
    private String curFromUsername;
    private int outputSize;
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

    /**
     * This will get the list of books that is in the incomingRequests collection
     */
    public void getRequests() {
        CollectionReference requestsCollection = userDoc.collection("incomingRequests");
        requestsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                outputRequests = new ArrayList<>();
                outputSize = task.getResult().size();
                if (task.getResult().size() > 0 ){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        DocumentReference docRef = (DocumentReference) document.get("bookReference");
                        DocumentReference userRef = (DocumentReference) document.get("from");
                        docRef.get().addOnCompleteListener(task14 -> {
                            DocumentSnapshot doc = task14.getResult();
                            curIsbn = (String) doc.get("isbn");
                        }).addOnCompleteListener(task13 -> userRef.get().addOnCompleteListener(task12 -> {
                            DocumentSnapshot userDoc = task12.getResult();
                            curFromEmail = userDoc.getString("email");
                            curFromUsername = userDoc.getString("username");
                        }).addOnCompleteListener(task1 -> {
                            book = new Book();
                            getBookQuery query = new getBookQuery(context);
                            query.getABook(curIsbn, book, instance);
                        }));

                    }
                }else{
                    requestCollection.clearList();
                }
            }
        });
    }

    /**
     * This method will be called by the query
     */
    @Override
    public void executeCallback() {
        Request request = new Request(curFromEmail, toEmail, book, context);
        request.setFromUsername(curFromUsername);
        outputRequests.add(request);
        if (outputRequests.size() == outputSize && outputRequests.size() > 0) {
            requestCollection.setRequestList(outputRequests);
            requestCollection.displayRequests();
        }
    }
}


