package com.example.booktracker.boundary;

import android.net.Uri;

import com.example.booktracker.entities.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

public class DeleteBookQuery extends BookQuery{
    public DeleteBookQuery(String email){
        super(email);
    }

    /**
     * This will query the database and delete a book
     * @param book book to be deleted
     */
    public void deleteBook(Book book){
        if (book.getStatus() != ""&& book.getStatus() != null) {
            userDoc.collection(book.getStatus())
                    .document(book.getIsbn())
                    .delete();
            if (book.getLocalUri() != null) {
                StorageReference deleteRef = storageReference.child("images/users/" + uid + "/" + Uri.parse(book.getLocalUri()).getLastPathSegment());
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
            StorageReference deleteRef = storageReference.child("images/users/" + uid + "/" + Uri.parse(book.getLocalUri()).getLastPathSegment());
            deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String toast_output = "Delete Complete";
                }
            });
        }
    }
}
