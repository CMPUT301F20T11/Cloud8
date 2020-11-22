package com.example.booktracker.boundary;

import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateQuery {
    private FirebaseFirestore db;

    public UpdateQuery() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Updates a book in firestore if it exist
     *
     * @param oldBook  oldBook containing necessary data
     * @param callback callback function to be called to show the result of
     *                 query
     * @param data     data containing the updates to be made to the book
     */
    public void updateBook(Book oldBook, QueryOutputCallback callback,
                           HashMap<String, Object> data,
                           QueryOutput queryOutput) {
        if (oldBook.getOwner() != null) {
            DocumentReference bookRef =
                    db.collection("users").document(oldBook.getOwnerEmail());
            bookRef.get().addOnCompleteListener(task -> {
                DocumentSnapshot res = task.getResult();
                if (res != null) {
                    bookRef.collection("myBooks")
                            .document(oldBook.getIsbn())
                            .update(data)
                            .addOnSuccessListener(aVoid -> {
                                queryOutput.setOutput("Book " +
                                        "successfully edited");
                                callback.displayQueryResult(
                                        "successful");
                            }).addOnFailureListener(e -> {
                        queryOutput.setOutput("Error book cannot be " +
                                "edited");
                        callback.displayQueryResult("not successful");
                    });
                }
            });
        } else {
            DocumentReference oldBookRef =
                    db.collection("users").document(oldBook.getStringOwner());
            oldBookRef.get().addOnCompleteListener(task -> {
                DocumentSnapshot res = task.getResult();
                if (res != null) {
                    oldBookRef.collection("myBooks")
                            .document(oldBook.getIsbn())
                            .update(data)
                            .addOnSuccessListener(aVoid -> {
                                queryOutput.setOutput("Book " +
                                        "successfully edited");
                                callback.displayQueryResult(
                                        "successful");
                            }).addOnFailureListener(e -> {
                        queryOutput.setOutput("Error book cannot be " +
                                "edited");
                        callback.displayQueryResult("not successful");
                    });
                }
            });
        }
    }
}
