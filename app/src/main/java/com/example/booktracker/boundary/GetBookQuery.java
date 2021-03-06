package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.NotifCount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class GetBookQuery extends BookQuery {
    private ArrayList<Book> outputBooks = new ArrayList<>();
    private Book output;
    private CountDownLatch done = new CountDownLatch(1);
    private boolean isDone = false;
    private BookCollection bookList;
    private Context context;
    private int counter = 0;
    private String userEmail;

    /**
     * This will call its parent constructor from BookQuery
     *
     * @param userEmail
     * @param argBookList Book collection containing listView
     */
    public GetBookQuery(String userEmail, BookCollection argBookList,
                        Context argContext) {
        super(userEmail);
        bookList = argBookList;
        context = argContext;
        this.userEmail = email;
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
    private Book docToBook(DocumentSnapshot document) {
        List<String> authors = (List<String>) document.get("author");
        HashMap<String, String> owner =
                (HashMap<String, String>) document.get("owner");
        Book book = new Book(owner, authors, (String) document.get("title"),
                document.getId(), (String) document.get("description"));
        if (document.get("image_uri") != null) {
            Uri imageUri = Uri.parse((String) document.get("image_uri"));
            book.setUri(imageUri.toString());
        }
        if (document.get("local_image_uri") != null) {
            Uri localImageUri = Uri.parse((String) document.get("local_image_uri"));
            book.setLocalUri(localImageUri.toString());
        }
        book.setStatus(document.getString("status"));
        book.setBorrower("borrower");
        return book;
    }

    /**
     * This will query the database for the specified books and display the status
     * @param reference
     * @param status
     */
    private void getStatus(CollectionReference reference, String status) {
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int querySize = task.getResult().size();
                        outputBooks = new ArrayList<>();
                        counter = 0;
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");

                            if (bookRef != null){
                                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (task.isSuccessful()){
                                            counter++;
                                            if (doc.exists()) {
                                                if (status == "requested"){
                                                    if (doc.getString("status").equals("available")){
                                                        outputBooks.add(docToBook(doc));
                                                    }else{
                                                        reference.document(doc.getId()).delete();
                                                    }
                                                }else if(status.equals("accepted")){
                                                    String temp = getEmail((HashMap<String, String>) doc.get("owner"));
                                                    if (!getEmail((HashMap<String, String>) doc.get("owner")).equals(userEmail)){
                                                        outputBooks.add(docToBook(doc));
                                                    }
                                                }else {
                                                    outputBooks.add(docToBook(doc));
                                                }


                                            } else {
                                                reference.document(bookRef.getId()).delete();
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    // every step of the loop check if the list of books is full
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (querySize == counter && outputBooks.size() > 0) {
                                            bookList.displayBooksStatus(status, outputBooks);
                                            outputBooks = new ArrayList<>();
                                            // empty outputBooks to clear results from last query
                                        } else {
                                            // in case there no matches clear the current
                                            // list
                                            if (status.equals("accepted")){

                                            }else{
                                                bookList.clearList();
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        throw new RuntimeException("Error getting books");
                    }

                });
    }

    /**
     * get will get all contents of the specified collection reference and output it
     * @param reference
     */
    private void get(CollectionReference reference){
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int querySize = task.getResult().size();
                        outputBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");
                            if (bookRef != null){
                                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (task.isSuccessful()) {
                                            if (doc.exists()) {
                                                outputBooks.add(docToBook(doc));
                                            } else {
                                                reference.document(bookRef.getId()).delete();
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    // every step of the loop check if the list of books is full
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (querySize == outputBooks.size() && outputBooks.size() > 0) {
                                            bookList.setBookList(outputBooks);
                                            bookList.displayBooks();
                                            outputBooks = new ArrayList<>();
                                            // empty outputBooks to clear results from last query
                                        } else {
                                            // in case there no matches clear the current
                                            // list
                                            bookList.clearList();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        throw new RuntimeException("Error getting books");
                    }
                });
    }

    /**
     * this will query the myBooks collection
     * @param user
     * @param status
     */
    public void getMyBooksStatus(String user,String status){
        CollectionReference reference = db.collection("users").document(user)
                .collection("myBooks");
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int querySize = task.getResult().size();
                        outputBooks = new ArrayList<>();
                        counter = 0;
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");
                            if (bookRef != null){
                                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (task.isSuccessful()){
                                            if (doc.exists()) {
                                                if (doc.getString("status").equals(status)){
                                                    outputBooks.add(docToBook(doc));

                                                }
                                            } else {
                                                reference.document(bookRef.getId()).delete();
                                            }
                                            counter++;
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    // every step of the loop check if the list of books is full
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (querySize == counter && outputBooks.size() > 0) {
                                            bookList.setBookList(outputBooks);
                                            bookList.displayBooksStatus(status,outputBooks);
                                            outputBooks = new ArrayList<>();
                                            counter = 0;
                                            // empty outputBooks to clear results from last query
                                        } else {
                                            // in case there no matches clear the current
                                            // list
                                            bookList.clearList();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        throw new RuntimeException("Error getting books");
                    }

                });
    }
    /**
     * get will get all contents of the specified collection reference and output it
     * @param email email of the user who owns the book collection
     */
    public void getAvailable(String email) {
        CollectionReference reference = db.collection("users")
                .document(email).collection("myBooks");
        reference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final int querySize = task.getResult().size();
                        outputBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document :
                                Objects.requireNonNull(task.getResult())) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");
                            if (bookRef != null){
                                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (task.isSuccessful()){
                                            if (doc.exists() && doc.getString("status").equals("available")) {
                                                outputBooks.add(docToBook(doc));
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    // every step of the loop check if the list of books is full
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (querySize == outputBooks.size() && outputBooks.size() > 0) {
                                            bookList.setBookList(outputBooks);
                                            bookList.displayBooks();
                                            outputBooks = new ArrayList<>();
                                            // empty outputBooks to clear results from last query
                                        } else {
                                            // in case there no matches clear the current
                                            // list
                                            bookList.clearList();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        throw new RuntimeException("Error getting books");
                    }

                });
    }

    private ArrayList<QueryDocumentSnapshot> concatQueries(QuerySnapshot query1, QuerySnapshot query2) {
        ArrayList<QueryDocumentSnapshot> out = new ArrayList<>();
        for (QueryDocumentSnapshot doc : query1) {
            out.add(doc);
        }
        for (QueryDocumentSnapshot doc : query2) {
            out.add(doc);
        }
        return out;
    }
    /**
     * This will get the email of the user that is stored in firestore
     * @param owner
     * @return
     */
    private String getEmail(HashMap<String, String> owner) {
        String email = "";
        for (Map.Entry<String, String> entry: owner.entrySet()){
            email = entry.getKey();
        }
        return email;
    }
    /**
     * getAll will get all the borrowed and owned books
     * @param email email of user that owns the collection
     */
    public void getAll(String email) {
        DocumentReference reference = db.collection("users").document(email);
        Task all = reference.collection("myBooks").get();
        Task borrowed = reference.collection("borrowed").get();
        Tasks.whenAllComplete(all, borrowed)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        ArrayList<Task<?>> res = (ArrayList<Task<?>>) task.getResult();
                        QuerySnapshot res1 = (QuerySnapshot) res.get(0).getResult();
                        QuerySnapshot res2 = (QuerySnapshot) res.get(1).getResult();
                        ArrayList<QueryDocumentSnapshot> res3 = concatQueries(res1, res2);
                        int querySize = res3.size();
                        outputBooks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : res3) {
                            DocumentReference bookRef = (DocumentReference) document.get("bookReference");
                            if (bookRef != null) {
                                bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot doc = task.getResult();
                                        if (task.isSuccessful()) {
                                            if (doc.exists()) {
                                                outputBooks.add(docToBook(doc));
                                            } else {
                                                reference.collection("myBooks").document(bookRef.getId()).delete();
                                                reference.collection("borrowed").document(bookRef.getId()).delete();
                                            }
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    //every step of the loop check if the list of books is full
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (querySize == outputBooks.size() && outputBooks.size() > 0) {
                                            bookList.setBookList(outputBooks);
                                            bookList.displayBooks();
                                            outputBooks = new ArrayList<>();
                                            // empty outputBooks to clear results from last query
                                        } else {
                                            // in case there no matches clear the current
                                            // list
                                            bookList.clearList();
                                        }
                                    }
                                });
                            }
                        }
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

    public void getBooksCategory(String category) throws RuntimeException {
        getStatus(userDoc.collection(category),category);
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
        db.collection("books").document(isbn)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot res = task.getResult();
                        if (res.exists()) {
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
                            if (res.get("owner") != null) {
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
                            emptyBook.setBorrower(res.getString("borrower"));
                            callback.executeCallback();
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
    public void getBooks(Callback callback, ArrayList<Book> bookList) {
        db.collection("books").get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot res = task.getResult();
                    for (DocumentSnapshot doc : res) {
                        bookList.add(docToBook(doc));
                    }
                    callback.executeCallback();
                });
    }

    public void getNotif(Callback callback, NotifCount count, String email){
        db.collection("users").document(email).get().addOnCompleteListener(task -> {
            DocumentSnapshot res = task.getResult();
            if (res.get("incomingCount") != null) {
                count.setIncoming((long) res.get("incomingCount"));
            }
            if (res.get("acceptedCount") != null) {
                count.setAccepted((long) res.get("acceptedCount"));
            }
            callback.executeCallback();
        });
    }

    /**
     * this will fill the book with the longitude and lattitude of the of the meeting point
     * @param callback called when book is filled
     * @param book book to be filled
     */
    public void getLatLong(Callback callback, Book book) {
        db.collection("books").document(book.getIsbn()).get().addOnCompleteListener(task -> {
            DocumentSnapshot res = task.getResult();
            Double lat = res.getDouble("lat");
            Double lon = res.getDouble("lon");
            if (lat != null && lon != null) {
                book.setLat(lat);
                book.setLon(lon);
                callback.executeCallback();
            }
        });
    }
}