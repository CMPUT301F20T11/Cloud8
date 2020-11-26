package com.example.booktracker.boundary;


import androidx.annotation.NonNull;

import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddBookQuery extends BookQuery {
    private BookCollection bookList;
    private QueryOutput queryOutput;
    private QueryOutputCallback outputCallback;

    /**
     * This will call its parent constructor from BookQuery
     *
     * @param userEmail
     */
    public AddBookQuery(String userEmail, QueryOutput argQueryOutput,
                        QueryOutputCallback callback) {
        super(userEmail);
        queryOutput = argQueryOutput;
        outputCallback = callback;
    }

    public AddBookQuery(String userEmail) {
        super(userEmail);
    }

    public AddBookQuery() {}
    /**
     * This will add the book to the adapter and the database if its not
     * already there
     *
     * @param newBook book to be checked if in db
     */
    public void addBook(Book newBook) {
        db.collection("books").
                document(newBook.getIsbn())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot res = task.getResult();
                        if (Objects.requireNonNull(res).exists()) {
                            // book is already in the database
                            if (queryOutput != null) {
                                queryOutput.setOutput("Book is already " +
                                        "owned by someone");
                                outputCallback.displayQueryResult("not " +
                                        "successful");
                            }
                        } else {
                            loadUsername(newBook);
                            addToDb(newBook);
                        }
                    } else {
                        queryOutput.setOutput("Error when adding book");
                        outputCallback.displayQueryResult("not successful");
                    }
                });
    }

    /**
     * @param newBook book to extract data from
     * @return A hashmap matching the key value pairs of a book in firestore
     * @author Ivan Penales
     */
    private HashMap<String, Object> getData(Book newBook) {
        HashMap<String, Object> data = new HashMap<>();
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put(newBook.getOwnerEmail(), newBook.getOwnerName());
        data.put("status", newBook.getStatus());
        data.put("isbn", newBook.getIsbn());
        data.put("title", newBook.getTitle());
        data.put("owner", nestedData);
        data.put("borrower", newBook.getBorrower());
        data.put("description", newBook.getDescription());
        data.put("author", newBook.getAuthor());
        data.put("image_uri", newBook.getUri());
        data.put("local_image_uri", newBook.getLocalUri());
        return data;
    }

    /**
     * This will add a book to firestore
     *
     * @param newBook book to add to firestore
     * @author Ivan Penales
     */
    private void addToDb(Book newBook) {
        HashMap<String, Object> data = getData(newBook);
        final CollectionReference bookCollection = db.collection("books");
        bookCollection
                .document(newBook.getIsbn())
                .set(data).addOnCompleteListener(task -> {
                    final DocumentReference bookReference = bookCollection.document(newBook.getIsbn());
                    HashMap<String, Object> userBook = new HashMap<>();
                    userBook.put("bookReference", bookReference);
                    if (!newBook.getStatus().equals("")) {
                        userDoc.collection(newBook.getStatus())
                                .document(newBook.getIsbn())
                                .set(userBook);
                    }
                    //book is always added to myBook list regardless of its status
                    userDoc.collection("myBooks")
                            .document(newBook.getIsbn())
                            .set(userBook).addOnSuccessListener(aVoid -> {
                        if (queryOutput != null) {
                            queryOutput.setOutput("Added book successfully");
                            outputCallback.displayQueryResult("successful");
                        }
                    }).addOnFailureListener(e -> {
                        if (queryOutput != null) {
                            queryOutput.setOutput("couldn't add book");
                            outputCallback.displayQueryResult("not successful");
                        }
                    });
                });

    }

    /**
     * This will add the reference to a book to the user's document
     * @param newBook book to be added
     * @param borrowerEmail email of the person who is receiving the book
     */
    public void addBookBorrower(Book newBook,String borrowerEmail){
        HashMap<String,Object> data = new HashMap<>();
        data.put("bookReference", db.collection("books").document(newBook.getIsbn()));
        db.collection("users").document(borrowerEmail).collection(newBook.getStatus())
                .document(newBook.getIsbn())
                .set(data);
    }
    public void loadUsername(Book book) {
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null) {
                    Map<String, String> owner = new HashMap<>();
                    String username = doc.getString("username");
                    String email = doc.getId();
                    owner.put(email, username);
                    if (book.getOwner() != null) {
                        book.getOwner().put(email, username);
                    } else {
                        book.setOwner(owner);
                    }
                }
            }
        });
    }
}
            
