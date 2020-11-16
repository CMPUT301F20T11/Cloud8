package com.example.booktracker.boundary;


import androidx.annotation.NonNull;

import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;



import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AddBookQuery extends BookQuery{
    private BookCollection bookList;
    private QueryOutput queryOutput;
    private QueryOutputCallback outputCallback;
    /**
     * This will call its parent constructor from BookQuery
     * @param userEmail
     */
    public AddBookQuery(String userEmail, QueryOutput argQueryOutput, QueryOutputCallback callback){
        super(userEmail);
        queryOutput = argQueryOutput;
        outputCallback = callback;
    }
    public AddBookQuery(String userEmail){
        super(userEmail);
    }
    /**
     * This will add the book to the adapter and the database if its not already there
     * @param newBook book to be checked if in db
     */
    public void addBook(Book newBook) {
        db.collectionGroup("myBooks").whereEqualTo("isbn", newBook.getIsbn().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> list = Objects.requireNonNull(task.getResult()).getDocuments();
                            if (list.size() > 0) {
                                //book is already in the database
                                if (queryOutput != null){
                                    queryOutput.setOutput("Book is already owned by someone");
                                    outputCallback.displayQueryResult("not successful");
                                }
                            }else{
                                addToDb(newBook);
                            }
                        }else {
                            queryOutput.setOutput("Error when adding book");
                            outputCallback.displayQueryResult("not successful");
                        }
                    }
                });
    }
    /**
     * @author Ivan Penales
     * @param newBook book to extract data from
     * @return A hashmap matching the key value pairs of a book in firestore
     */
    private HashMap<String,Object> getData(Book newBook){
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("status",newBook.getStatus());
        data.put("isbn",newBook.getIsbn());
        data.put("title",newBook.getTitle());
        data.put("owner",newBook.getOwner());
        data.put("borrower",newBook.getBorrower());
        data.put("description",newBook.getDescription());
        data.put("author",newBook.getAuthor());
        data.put("image_uri", newBook.getUri());
        data.put("local_image_uri", newBook.getLocalUri());
        return data;
    }
    /**
     * This will add a book to firestore
     * @author Ivan Penales
     * @param newBook book to add to firestore
     */
    private void addToDb(Book newBook){
        HashMap<String,Object> data = getData(newBook);
        if (newBook.getStatus() != ""){
            userDoc.collection(newBook.getStatus())
                    .document(newBook.getIsbn())
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (queryOutput != null){
                        queryOutput.setOutput("Added book succesfully");
                        outputCallback.displayQueryResult("successful");
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (queryOutput != null){
                        queryOutput.setOutput("couldn't add book");
                        outputCallback.displayQueryResult("not successful");
                    }
                }
            });
        }
        //book is always added to myBook list regardless of its status
        userDoc.collection("myBooks")
                .document(newBook.getIsbn())
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if (queryOutput != null){
                    queryOutput.setOutput("Added book succesfully");
                    outputCallback.displayQueryResult("successful");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (queryOutput != null){
                    queryOutput.setOutput("couldn't add book");
                    outputCallback.displayQueryResult("not successful");
                }
            }
        });
    }

}