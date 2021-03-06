package com.example.booktracker.boundary;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

public class DeleteBookQuery extends BookQuery {

    public DeleteBookQuery(String email) {
        super(email);
    }

    public DeleteBookQuery() {}

    /**
     * This will query the database and delete a book
     *
     * @param book book to be deleted
     */
    public void deleteBook(Book book) {
        db.collection("books")
                .document(book.getIsbn())
                .delete();
        if (!book.getStatus().equals("") && book.getStatus() != null) {
            userDoc.collection(book.getStatus())
                    .document(book.getIsbn())
                    .delete();
            if (book.getLocalUri() != null) {
                StorageReference deleteRef = storageReference.child("images" +
                        "/users/" + uid + "/" + Uri.parse(book.getLocalUri()).getLastPathSegment());
                deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String toast_output = "Delete Complete";
                    }
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

    private void delBookRef(String status, String id, String userEmail){
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
    public void deleteBookRequested(String isbn, String email){
        delBookRef("requested", isbn, email);
    }
    /**
     * This will delete a book from the requested collection of the user
     * @param isbn isbn of the book to be deleted
     * @param email email of the user
     */
    public void deleteBookAccepted(String isbn, String email){
        delBookRef("accepted", isbn, email);
    }
    /**
     * This will delete a book from the incoming request collection
     * @param isbn isbn of the book to be deleted
     * @param requesterEmail email of the user who made a request for the book
     * @param ownerEmail email of the person who owns the book
     */
    public void deleteBookIncoming(String isbn, String requesterEmail, String ownerEmail){
        delBookRef("incomingRequests",isbn+"-"+requesterEmail, ownerEmail);
    }

    public void deleteBookList(String category,String email){
        CollectionReference colRef = db.collection("users").document(email).collection(category);
        colRef.get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot res = task.getResult();
                    for (DocumentSnapshot doc : res){
                        String id = doc.getId();
                        if (id != null) {
                            colRef.document(id).delete();
                        }
                    }
                });
    }
    public void deleteAllRequest(String isbn,String email){
        db.collection("users").document(email)
                .collection("incomingRequests")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot res = task.getResult();
                for (DocumentSnapshot doc:res){
                    if (doc.getId().contains(isbn)){
                        db.collection("users").document(email)
                                .collection("incomingRequests")
                                .document(doc.getId()).delete();
                    }
                }
            }
        });
    }

    /**
     * this will delete the accepted request of the person who requested the book
     * @param book
     */
    public void deletePotentialBorrower(Book book){
        db.collection("books").document(book.getIsbn()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot res = task.getResult();
                        if (res.getString("potentialBorrower") != null){
                            deleteBookAccepted(book.getIsbn(),res.getString("potentialBorrower"));
                        }
                    }
                });
    }
}
