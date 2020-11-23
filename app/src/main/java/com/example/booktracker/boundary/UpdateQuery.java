package com.example.booktracker.boundary;

import androidx.annotation.NonNull;

import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
        DocumentReference bookRef =
                db.collection("books").document(oldBook.getIsbn());
        bookRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot res = task.getResult();
                        if (res.exists()){
                            bookRef.update(data);
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                queryOutput.setOutput("Book " +
                        "successfully edited");
                callback.displayQueryResult(
                        "successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queryOutput.setOutput("Book " +
                        "successfully edited");
                callback.displayQueryResult(
                        "successful");
            }
        });
    }

    /**
     * This will change the status of the book by moving it to a different collection in the
     * user document
     * @param book book to be changed
     * @param status status to change too
     * @param user email of the user which contains the book
     */
    public void changeBookStatus(Book book,String status,String user){
        String bookStatus = book.getStatus();
        String bookIsbn = book.getIsbn();
        if (bookStatus != status){
            DocumentReference userDoc =  db.collection("users").document(user);
            DocumentReference bookRef = db.collection("books").document(book.getIsbn());
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("bookReference",bookRef);
            //====================delete reference in old status collection================
            userDoc
                    .collection(bookStatus)
                    .document(bookIsbn)
                    .delete();
            //==============================================================================
            //==================add reference to the new status collection==================
            userDoc
                    .collection(status)
                    .document(bookIsbn)
                    .set(data);
            //==============================================================================
        }
    }
    private void deleteOldBook(CollectionReference colRef,String isbn){
        colRef.document(isbn).delete();
    }
}
