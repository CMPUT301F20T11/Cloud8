package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RequestQuery{
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
     * turn the requested book into a book object
     * @param res result of get query
     * @param emptyBook book to be filled up by data attained from query
     */
    private void parseBook(DocumentSnapshot res,Book emptyBook){
        if (res.get("image_uri") != null) {
            Uri imageUri =
                    Uri.parse((String) res.get(
                            "image_uri"));
            emptyBook.setUri(imageUri.toString());
        }
        if (res.get("local_image_uri") != null) {
            Uri localImageUri =
                    Uri.parse((String) res.get(
                            "local_image_uri"));
            emptyBook.setLocalUri(localImageUri.toString());
        }
        List<String> authors =
                (List<String>) res.get("author");

        if (res.get("owner") instanceof String) {
            String stringOwner =
                    (String) res.get("owner");
            emptyBook.setStringOwner(stringOwner);
        } else {
            HashMap<String, String> owner =
                    (HashMap<String, String>) res.get("owner");
            emptyBook.setOwner(owner);
        }
        emptyBook.setAuthor(authors);
        emptyBook.setIsbn((String) res.get("isbn"));
        emptyBook.setTitle((String) res.get("title"));
        emptyBook.setDescription((String) res.get(
                "description"));
        emptyBook.setStatus((String) res.get("status"));
    }
    /**
     * This will get the list of books that is in the incomingRequests collection
     */
    public void getRequests(String requestCollectionId) {
        CollectionReference requestsCollection = userDoc.collection(requestCollectionId);
        requestsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    outputRequests = new ArrayList<>();
                    outputSize = task.getResult().size();
                    if (task.getResult().size() > 0 ){

                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                            DocumentReference docRef = (DocumentReference) document.get("bookReference");
                            DocumentReference userRef = (DocumentReference) document.get("from");
                            Task bookDoc = docRef.get();
                            Task userDoc = userRef.get();
                            Tasks.whenAllComplete(bookDoc,userDoc).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Task<?>>> task) {
                                    ArrayList<Task<?>> res = (ArrayList<Task<?>>) task.getResult();
                                    DocumentSnapshot res1 = (DocumentSnapshot) res.get(0).getResult(); //this is the book
                                    DocumentSnapshot res2 = (DocumentSnapshot) res.get(1).getResult(); //this is the user
                                    curFromEmail = (String)res2.get("email");
                                    curFromUsername = (String) res2.getString("username");
                                    book = new Book();
                                    parseBook(res1,book);
                                    Request request = new Request(curFromEmail, toEmail, book, context);
                                    request.setFromUsername(curFromUsername);
                                    outputRequests.add(request);
                                    if (outputRequests.size() == outputSize && outputRequests.size() > 0) {
                                        requestCollection.setRequestList(outputRequests);
                                        requestCollection.displayRequests();
                                    }

                                }
                            });
                        }
                    }else{
                        requestCollection.clearList();
                    }
                }
            }
        });
    }


}