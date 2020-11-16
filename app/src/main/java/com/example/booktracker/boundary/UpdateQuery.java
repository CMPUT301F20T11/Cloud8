package com.example.booktracker.boundary;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateQuery {
    private FirebaseFirestore db;
    public UpdateQuery(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Updates a book in firestore if it exist
     * @param oldBook oldBook containing necessary data
     * @param callback callback function to be called to show the result of query
     * @param data  data containing the updates to be made to the book
     */
    public void updateBook(Book oldBook, QueryOutputCallback callback, HashMap<String,Object> data , QueryOutput queryOutput) {
        DocumentReference bookRef = db.collection("users").document(oldBook.getOwner());
        bookRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot res = task.getResult();
                if (res.exists()){
                    bookRef.collection("myBooks")
                            .document(oldBook.getIsbn())
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    queryOutput.setOutput("Book successfully edited");
                                    callback.displayQueryResult("successful");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            queryOutput.setOutput("Error book cannot be edited");
                            callback.displayQueryResult("not successful");
                        }
                    });
                }


            }
        });
    }
}
