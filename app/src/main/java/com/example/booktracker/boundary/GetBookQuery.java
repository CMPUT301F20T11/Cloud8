package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.ui.ScanFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

public class GetBookQuery extends BookQuery{
    private ArrayList<Book> outputBooks = new ArrayList();
    private Book output;
    private CountDownLatch done = new CountDownLatch(1);
    private boolean isDone = false;
    private BookCollection bookList;
    private Context context;
    /**
     * This will call its parent constructore from BookQuery
     * @param userEmail
     * @param argBookList Book collectoin containing listView
     */
    public GetBookQuery(String userEmail,BookCollection argBookList,Context argContext){
        super(userEmail);
        bookList = argBookList;
        context = argContext;
    }

    /**
     * This will query the data base and use the initialize a Book Collection object which
     * modifies the listView
     * @throws RuntimeException
     */
    public void getMyBooks() throws RuntimeException{
        userDoc.collection("myBooks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            outputBooks = new ArrayList<Book>();//
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Book(String argOwner, List<String>argAuthor, String argTitle, int argIsbn, String argDesc)
                                List<String> authors = ( List<String>) document.get("author");
                                Book book = new Book((String) document.get("owner"),authors, (String) document.get("title"),document.getId(),(String) document.get("description"));
                                if (document.get("image uri") != null){
                                    Uri imageUri = Uri.parse((String) document.get("image_uri"));
                                    book.setUri(imageUri);
                                }
                                outputBooks.add(book);
                            }
                            if (outputBooks.size() > 0){
                                bookList.setBookList(outputBooks);
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
     * @param category this will be the id of the collection of books in the user document
     * @throws RuntimeException
     */
    public void getMyBooks(String category) throws RuntimeException{
        userDoc.collection(category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            outputBooks = new ArrayList<Book>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Book(String argOwner, List<String>argAuthor, String argTitle, int argIsbn, String argDesc)
                                List<String> authors = ( List<String>) document.get("author");
                                Book book = new Book((String) document.get("owner"),authors, (String) document.get("title"),document.getId(),(String) document.get("description"));
                                if (document.get("image uri") != null){
                                    Uri imageUri = Uri.parse((String) document.get("image_uri"));
                                    book.setUri(imageUri);
                                }
                                outputBooks.add(book);
                            }
                            if (outputBooks.size() > 0){
                                bookList.setBookList(outputBooks);
                                bookList.displayBooks();
                            }

                        } else {
                            throw new RuntimeException("Error getting books");
                        }

                    }
                });
    }

    /**
     * This will fill up the contents of an empty book by querying firestore for a book
     * @param isbn
     * @param emptyBook
     */
    public void getABook(String isbn, Book emptyBook, ScanFragment activity){
        userDoc.collection("myBooks")
                .document(isbn)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot res = task.getResult();
                            if (res != null){
                                List<String> authors = ( List<String>) res.get("author");
                                emptyBook.setAuthor(authors);
                                emptyBook.setIsbn(isbn);
                                emptyBook.setTitle((String) res.get("title"));
                                emptyBook.setOwner((String) res.get("owner"));
                                emptyBook.setDescription((String) res.get("description"));
                                activity.displayDescription();
                            }
                        }
                    }
                });
    }
}
