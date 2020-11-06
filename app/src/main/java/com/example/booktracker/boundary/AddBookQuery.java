package com.example.booktracker.boundary;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.entities.User;
import com.example.booktracker.ui.AddBookActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBookQuery extends BookQuery{
    private BookCollection bookList;
    private String queryOutput = "";

    /**
     * This will call its parent constructore from BookQuery
     * @param userEmail
     */

    public AddBookQuery(String userEmail){
        super(userEmail);
    }

    /**
     * This will add the book to the adapter and the database if its not already there
     * @author Ivan Penales
     * @param newBook book to be added
     */
    public String addBook(Book newBook){

        addToDb(newBook);// add book to database
        return queryOutput;
    }

    /**
     * @author Ivan Penales
     * @param newBook book to extract data from
     * @return A hashmap matching the key value pairs of a book in firestore
     */
    private HashMap<String,Object> getData(Book newBook){
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("title",newBook.getTitle());
        data.put("owner",newBook.getOwner());
        data.put("borrower",newBook.getBorrower());
        data.put("description",newBook.getDescription());
        data.put("author",newBook.getAuthor());
        if (newBook.getUri() != null){
            data.put("image_uri", newBook.getUri());
        }
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
                   queryOutput =  "Added book succesfully";
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    queryOutput = "couldn't add book";
                }
            });
        }
        //book is always added to myBook list regardless of its status
        userDoc.collection("myBooks")
                .document(newBook.getIsbn())
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                queryOutput =  "Added book succesfully";
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queryOutput = "couldnt add book";
            }
        });
    }

}