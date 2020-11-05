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
    private BookCollection bookList;
    /**
     * This will call its parent constructore from BookQuery
     * @param userEmail
     */
    public GetBookQuery(String userEmail,BookCollection argBookList){
        super(userEmail);
        bookList = argBookList;
    }

    /**
     * This will query the data base and use the initialize a Book Collection object which
     * modifies the listView
     * @param context Context of the app this is being rendered in
     * @throws RuntimeException
     */
    public void getMyBooks(final Context context) throws RuntimeException{
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
                                bookList.setBookList(output);
                                bookList.displayBooks();
                            }

                        } else {
                            throw new RuntimeException("Error getting books");
                        }

                    }
                });
    }
    /**
     * This will query the data base and use the initialize a Book Collection object which
     * modifies the listView. A category is specified to get specific list of books.
     * @param listView ListView to be modified
     * @param context Context of the app this is being rendered in
     * @param category this will be the id of the collection of books in the user document
     * @throws RuntimeException
     */
    public void getMyBooks(final ListView listView, final Context context,String category) throws RuntimeException{
        userDoc.collection(category)
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
                                bookList.setBookList(output);
                                bookList.displayBooks();
                            }

                        } else {
                            throw new RuntimeException("Error getting books");
                        }

                    }
                });
    }
}
