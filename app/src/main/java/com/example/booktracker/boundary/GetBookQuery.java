package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.NotifCount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class GetBookQuery extends BookQuery {
    private ArrayList<Book> outputBooks = new ArrayList();
    private Book output;
    private CountDownLatch done = new CountDownLatch(1);
    private boolean isDone = false;
    private BookCollection bookList;
    private Context context;

    /**
     * This will call its parent constructor from BookQuery
     *
     * @param userEmail
     * @param argBookList Book collectoin containing listView
     */
    public GetBookQuery(String userEmail, BookCollection argBookList,
                        Context argContext) {
        super(userEmail);
        bookList = argBookList;
        context = argContext;
    }

    /**
     * This constructor will be used for querying a single book
     *
     * @param argContext
     */
    public GetBookQuery(Context argContext) {
        super();
        context = argContext;
    }

    /**
     * This constructor will be used for getting the books collection
     */
    public GetBookQuery() {
        super();
    }
    /**
     * This will turn the document that resulted from a query to a Book object
     * @param document Document from firestore query
     * @return
     */
    private Book docToBook(DocumentSnapshot document){
        List<String> authors = (List<String>) document.get("author");
        HashMap<String, String> owner = (HashMap<String, String>) document.get("owner");
        Book book = new Book(owner, authors, (String) document.get("title"), document.getId(), (String) document.get("description"));
        if (document.get("image_uri") != null) {
            Uri imageUri = Uri.parse((String) document.get("image_uri"));
            book.setUri(imageUri.toString());
        }
        if (document.get("local_image_uri") != null) {
            Uri localImageUri = Uri.parse((String) document.get("local_image_uri"));
            book.setLocalUri(localImageUri.toString());
        }
        return book;
    }
    /**
     * get will get all contents of the specified collection reference and output it
     * @param reference
     */
    private void get(CollectionReference reference){
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int querySize = task.getResult().size();
                        outputBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");
                            bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    outputBooks.add(docToBook(document));
                                    if (querySize == outputBooks.size() && outputBooks.size() > 0) {
                                        bookList.setBookList(outputBooks);
                                        bookList.displayBooks();
                                        outputBooks = new ArrayList();
                                        //empty outputBooks to clear results from last query
                                    } else {
                                        //in case there no matches clear the current
                                        // list
                                        bookList.clearList();
                                    }

                                }
                            }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                //every step of the loop check if the list of books is full
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                }
                            });
                        }
                    } else {
                        throw new RuntimeException("Error getting books");
                    }

                });
    }
    /**
     * This will query the data base and use Book Collection object to modify
     * the listView
     *
     * @throws RuntimeException
     */
    public void getMyBooks() throws RuntimeException {
        get(userDoc.collection("myBooks"));
    }

    /**
     * This will query the database and use the BookCollection object to
     * modify the listView. A category is specified to get specific list of
     * books.
     *
     * @param category this will be the status of the books
     * @throws RuntimeException
     */
    public void getMyBooks(String category) throws RuntimeException {
        get(userDoc.collection(category));
    }

    /**
     * This will fill up the contents of an empty book by querying firestore
     * for a book.
     *
     * @param isbn      isbn of the book were looking for.
     * @param emptyBook Book to be populated.
     * @param callback  callback is a method in the class that called this
     *                  method. Callback must
     *                  contain the code that depends on the result of this
     *                  query.
     */
    public void getABook(String isbn, Book emptyBook, Callback callback) {

        db
                .collection("books")
                .document(isbn)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot res = task.getResult();
                        if (res.exists()) {
                            if (res != null) {
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
                                emptyBook.setIsbn(isbn);
                                emptyBook.setTitle((String) res.get(
                                        "title"));
                                emptyBook.setDescription((String) res.get(
                                        "description"));
                                emptyBook.setStatus((String) res.get(
                                        "status"));
                                callback.executeCallback();
                            }
                        }
                    }
                });
    }

    /**
     * This will query the database to get a list of books
     * @param callback Callback will be the instance of the class that called this method
     * @param bookList This will be the list of books the will get populated with all
     *                 the books in the database
     */
    public void getBooks(Callback callback,ArrayList<Book> bookList){
        db.collection("books").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot res = task.getResult();
                        for (DocumentSnapshot doc:res){
                            bookList.add(docToBook(doc));
                        }
                        callback.executeCallback();
                    }
                });
    }
    public void getNotif(Callback callback, NotifCount count,String email){
        db.collection("users").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.get("incomingCount") != null){
                    count.setIncoming((long) res.get("incomingCount"));
                }
                if (res.get("acceptedCount") != null){
                    count.setAccepted((long) res.get("acceptedCount"));
                }

                callback.executeCallback();
            }
        });
    }
}