package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

public class GetBookQuery extends BookQuery{
    private ArrayList<Book> output = new ArrayList();
    private CountDownLatch done = new CountDownLatch(1);
    private boolean isDone = false;
    /**
     * This will call its parent constructore from BookQuery
     * @param userEmail
     */
    public GetBookQuery(String userEmail){

        super(userEmail);
    }
    public void getMyBooks(final ListView listView, final Context context) throws RuntimeException{
        userDoc.collection("myBooks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Book(String argOwner, List<String>argAuthor, String argTitle, int argIsbn, String argDesc)
                                List<String> authors = ( List<String>) document.get("author");
                                Book book = new Book((String) document.get("owner"),authors, (String) document.get("title"),document.getId(),(String) document.get("description"));
                                if (document.get("image uri") != null){
                                    Uri imageUri = Uri.parse((String) document.get("image_uri"));
                                    book.setUri(imageUri);
                                }
                                output.add(book);
                            }
                            if (output.size() > 0){
                                new BookCollection(output,listView,email,context);
                            }

                        } else {
                            throw new RuntimeException("Error getting books");
                        }

                    }
                });
    }
}
