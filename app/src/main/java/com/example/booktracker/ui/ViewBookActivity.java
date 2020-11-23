package com.example.booktracker.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.booktracker.R;
import com.example.booktracker.boundary.getBookQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ViewBookActivity extends AppCompatActivity implements View.OnClickListener, Callback {
    private String isbn;
    private Book emptyBook;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String uid;
    private String loginEmail;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Map<String, Object> hopperUpdates;

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
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbook);
        isbn = getIntent().getStringExtra(EXTRA_MESSAGE);
        emptyBook = new Book();
        setTextViews();

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = auth.getCurrentUser();
        uid = user.getUid();
        loginEmail = user.getEmail();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("/books/" + isbn);

        // Creating buttons
        Button borrowButton = (Button) findViewById(R.id.borrow_book_button);
        borrowButton.setOnClickListener(this);
        Button giveButton = (Button) findViewById(R.id.give_book_button);
        giveButton.setOnClickListener(this);
        Button returnButton = (Button) findViewById(R.id.return_book_button);
        returnButton.setOnClickListener(this);
        Button receiveButton = (Button) findViewById(R.id.receive_book_button);
        receiveButton.setOnClickListener(this);
        Button addButton = (Button) findViewById(R.id.add_book_button);
        addButton.setOnClickListener(this);


        //==============query database for a book==============
        getBookQuery query = new getBookQuery(this);
        query.getABook(isbn, emptyBook, this);
        //=====================================================
    }

    /**
     * Helper method to set the references for the text views
     */
    private void setTextViews() {
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
     * Helper method to set the contents of the text views to the contents of
     * the book
     *
     * @param book
     */
    private void updateTextViews(Book book) {
        //uses the first author
        isbnView.setText(isbn);
        if (book.getOwner() != null) {
            ownerView.setText(book.getOwnerEmail());
        } else {
            ownerView.setText(book.getStringOwner());
        }
        borrowerView.setText(book.getBorrower());
        descView.setText(book.getDescription());
        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor().get(0));
        statusView.setText(book.getStatus());
        if (book.getUri() != null) {
            imageView.setImageURI(Uri.parse(book.getUri()));
        }
    }

    /**
     * This method will be called by the query
     */
    @Override
    public void executeCallback() {
        updateTextViews(emptyBook);
    }

    /**
     * This method decides which onClick functionality to execute according
     * to the ID of the button
     * which has been pressed.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_book_button:
                Toast.makeText(this, loginEmail, Toast.LENGTH_SHORT).show();
                break;
            case R.id.borrow_book_button:
                // Since we are the borrower in this case, we need to check
                // for book.borrower == none,
                // book.status == unavailable, book != null, and then book
                // .setBorrower("user.email")
                if ((emptyBook != null) && (emptyBook.getStatus().equals("unavailable"))
                        && emptyBook.getBorrower().equals("none")) {
                    emptyBook.setBorrower(loginEmail);
                    Toast.makeText(this, "Book Successfully Borrowed!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to borrow book!",
                            Toast.LENGTH_SHORT).show();
                }
                updateTextViews(emptyBook);
                setHopperUpdates();
                break;
            case R.id.give_book_button:
                // Since we are the owner in this case, we should check for
                // book.owner == user.email
                // and book.borrower == none, and book.status == available,
                // and book != null
                System.out.println(emptyBook.getBorrower().equals("none"));
                System.out.println((emptyBook.getStatus().equals("available")));
                System.out.println((emptyBook.getOwner().equals(loginEmail)));

                if ((emptyBook != null) && (emptyBook.getBorrower().equals("none"))
                        && (emptyBook.getStatus().equals("available")) && (emptyBook.getOwner().containsKey(loginEmail))) {
                    emptyBook.setStatus("unavailable");
                    Toast.makeText(this, "Book Successfully given!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to give book!",
                            Toast.LENGTH_SHORT).show();
                }
                updateTextViews(emptyBook);
                setHopperUpdates();
                break;
            case R.id.return_book_button:
                // Here we are the borrower attempting to hand over the book,
                // so we must check that
                // borrower == user, status == unavailable, then set it to
                // borrower == none
                if ((emptyBook != null) && emptyBook.getBorrower().equals(loginEmail)
                        && emptyBook.getStatus().equals("unavailable")) {
                    emptyBook.setBorrower("none");
                    Toast.makeText(this, "Book Successfully Returned!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Return Failed!", Toast.LENGTH_SHORT).show();
                }
                updateTextViews(emptyBook);
                setHopperUpdates();
                break;
            case R.id.receive_book_button:
                // Here we are the owner receiving a book that has been
                // returned, so we must check
                // that we own the book, no one is borrowing it, and then set
                // status to available
                if ((emptyBook != null) && (emptyBook.getBorrower().equals("none"))
                        && emptyBook.getOwner().containsKey(loginEmail)) {
                    emptyBook.setStatus("available");
                    Toast.makeText(this, "Book Successfully Received!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to receive book!",
                            Toast.LENGTH_SHORT).show();
                }
                updateTextViews(emptyBook);
                setHopperUpdates();
                break; 
        }
    }


    /**
     * setHopperUpdates will be called after the user clicks one of the buttons to give, borrow,
     * receive, or accept a book. After the button is pressed, it changes the book attributes and
     * setHopperUpdates will save the changes to the database.
     */
    private void setHopperUpdates(){
        hopperUpdates = new HashMap<>();
        hopperUpdates.put("borrower", borrowerView.getText().toString());
        hopperUpdates.put("status", statusView.getText().toString());
        ref.updateChildren(hopperUpdates);
    }
}

