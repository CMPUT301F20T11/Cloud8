package com.example.booktracker.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.boundary.getBookQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ViewBookActivity extends AppCompatActivity implements Callback {
    private String isbn;
    private Book emptyBook;

    //=========Text Views================
    private TextView isbnView;
    private TextView ownerView;
    private TextView borrowerView;
    private TextView descView;
    private TextView titleView;
    private TextView authorView;
    private TextView statusView;
    private ImageView imageView;
    //===================================

    /**
     * query database for book data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbook);
        isbn = getIntent().getStringExtra(EXTRA_MESSAGE);
        emptyBook = new Book();
        setTextViews();

        //==============query database for a book==============
        getBookQuery query = new getBookQuery(this);
        query.getABook(isbn,emptyBook,this);
        //=====================================================
    }

    /**
     * Helper method to set the references for the text views
     */
    private void setTextViews(){
        isbnView = findViewById(R.id.viewbook_isbn);
        ownerView = findViewById(R.id.viewbook_owner);
        borrowerView = findViewById(R.id.viewbook_borrower);
        descView = findViewById(R.id.viewbook_desc);
        titleView = findViewById(R.id.viewbook_title);
        authorView = findViewById(R.id.viewbook_author);
        statusView = findViewById(R.id.viewbook_status);
        imageView = findViewById(R.id.viewbook_image);

    }

    /**
     * Helper method to set the contents of the text views to the contents of the book
     * @param book
     */
    private void updateTextViews(Book book){
        //uses the first author
        isbnView.setText(isbn);
        ownerView.setText(book.getOwner());
        borrowerView.setText(book.getBorrower());
        descView.setText(book.getDescription());
        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor().get(0));
        statusView.setText(book.getStatus());
        if (book.getUri()!= null) {
            imageView.setImageURI(Uri.parse(book.getUri()));
        }
    }

    /**
     * This method will be called by the query
     */
    @Override
    public void executeCallback(){
        updateTextViews(emptyBook);
    }

}
