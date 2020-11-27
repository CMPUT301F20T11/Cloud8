package com.example.booktracker.boundary;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

public class DeleteBookQuery extends BookQuery {
    public DeleteBookQuery(String email) {
        super(email);
    }
    public DeleteBookQuery() {

    }

    /**
     * This will query the database and delete a book
     *
     * @param book book to be deleted
     */
    public void deleteBook(Book book) {
        db.collection("books")
                .document(book.getIsbn())
                .delete();
        if (book.getStatus() != "" && book.getStatus() != null) {
            userDoc.collection(book.getStatus())
                    .document(book.getIsbn())
                    .delete();
            if (book.getLocalUri() != null) {
                StorageReference deleteRef = storageReference.child("images" +
                        "/users/" + uid + "/" + Uri.parse(book.getLocalUri()).getLastPathSegment());
                deleteRef.delete().addOnSuccessListener(aVoid -> {
                    String toast_output = "Delete Complete";
                });
            }
        }
        userDoc.collection("myBooks")
                .document(book.getIsbn())
                .delete();
        if (book.getLocalUri() != null) {
            StorageReference deleteRef = storageReference.child("images/users" +
                    "/" + uid + "/" + Uri.parse(book.getLocalUri()).getLastPathSegment());
            deleteRef.delete().addOnSuccessListener(aVoid -> {
                String toast_output = "Delete Complete";
            });
        }
    }
    private void delBookRef(String status,String id,String userEmail){
        db.collection("users")
                .document(userEmail)
                .collection(status)
                .document(id)
                .delete();
    }
    /**
     * This will delete a book from the requested collection of the user
     * @param isbn isbn of the book to be deleted
     * @param email email of the user
     */
    public void deleteBookRequested(String isbn,String email){
        delBookRef("requested",isbn,email);
    }
    /**
     * This will delete a book from the incoming request collection
     * @param isbn isbn of the book to be deleted
     * @param requesterEmail email of the user who made a request for the book
     * @param ownerEmail email of the person who owns the book
     */
    public void deleteBookIncoming(String isbn,String requesterEmail,String ownerEmail){
        delBookRef("incomingRequests",isbn+"-"+requesterEmail,ownerEmail);
    }
    public void deleteBookList(String category,String email){
        CollectionReference collec = db.collection("users").document(email).collection(category);
        collec.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot res = task.getResult();
                        for (DocumentSnapshot doc:res){
                            String id = doc.getId();
                            if (id != null){
                                collec.document(id).delete();
                            }
                        }
                    }
                });
    }
}
