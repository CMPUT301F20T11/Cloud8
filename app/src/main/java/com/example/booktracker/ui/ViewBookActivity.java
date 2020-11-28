package com.example.booktracker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ViewBookActivity extends AppCompatActivity implements View.OnClickListener, QueryOutputCallback, Callback {
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
    private DocumentReference documentReference;
    private FirebaseFirestore db;
    private UpdateQuery updateQuery;
    private QueryOutput queryOutput;
    private ViewBookActivity instance = this;

    //========= Text Views ==============
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
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("books").document(isbn);
        updateQuery = new UpdateQuery();
        queryOutput = new QueryOutput();
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
        GetBookQuery query = new GetBookQuery(this);
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
        isbnView.setText("ISBN: " + isbn);
        if (book.getOwner() != null) {
            ownerView.setText("Owner: " + book.getOwnerEmail());
        }
        if (book.getBorrower() != null || book.getBorrower() == "none") {
            borrowerView.setText("Borrower: " + book.getBorrower());
        } else {
            borrowerView.setText("Borrower: none");
        }
        descView.setText(book.getDescription());
        titleView.setText(book.getTitle());
        authorView.setText(book.getAuthor().get(0));
        statusView.setText(book.getStatus());
        String status = book.getStatus();
        if (status.equals("available")) {
            statusView.setBackground(this.getResources().getDrawable(R.drawable.status_available, null));
        } else if (status.equals("borrowed")) {
            statusView.setBackground(this.getResources().getDrawable(R.drawable.status_borrowed, null));
        } else if (status.equals("requested")) {
            statusView.setBackground(this.getResources().getDrawable(R.drawable.status_requested, null));
        } else if (status.equals("accepted")) {
            statusView.setBackground(this.getResources().getDrawable(R.drawable.status_accepted, null));
        }
        if (book.getUri() != null) {
            Glide.with(this).load(book.getUri()).into(imageView);
        }
    }
    /**
     * This method will be called by the query
     */
    @Override
    public void executeCallback() {
        updateTextViews(emptyBook);
    }

    @Override
    public void displayQueryResult(String result) {
        Toast.makeText(instance, queryOutput.getOutput(), Toast.LENGTH_SHORT).show();
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
                if ((emptyBook != null) && (emptyBook.getStatus().equals("available"))
                        && (emptyBook.getBorrower() == null || emptyBook.getBorrower().equals("none")) || emptyBook.getBorrower().equals("")) {
                    //need to check if the book is already in the list of accepted books for this user
                    updateQuery.borrowBook(emptyBook.getIsbn(), user.getEmail(), instance, queryOutput);
                } else {
                    Toast.makeText(this, "Failed to borrow book!",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.give_book_button:
                // Since we are the owner in this case, we should check for
                // book.owner == user.email
                // and book.borrower == none, and book.status == available,
                // and book != null
                if ((emptyBook != null) && ((emptyBook.getBorrower() == null || emptyBook.getBorrower().equals("none")) || emptyBook.getBorrower().equals(""))
                        && (emptyBook.getStatus().equals("available")) && (emptyBook.getOwner().containsKey(loginEmail))) {
                    updateQuery.lendBook(emptyBook.getIsbn(), emptyBook.getOwnerEmail(), instance, queryOutput);
                } else {
                    Toast.makeText(this, "Failed to give book!",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.return_book_button:
                // Here we are the borrower attempting to hand over the book,
                // so we must check that
                // borrower == user, status == unavailable, then set it to
                // borrower == none
                if ((emptyBook != null) && emptyBook.getBorrower().equals(loginEmail)
                        && emptyBook.getStatus().equals("unavailable")) {
                    updateQuery.returnBook(emptyBook.getIsbn(),user.getEmail(),instance,queryOutput);
                } else {
                    Toast.makeText(this, "Return Failed!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.receive_book_button:
                // Here we are the owner receiving a book that has been
                // returned, so we must check
                // that we own the book, no one is borrowing it, and then set
                // status to available
                if ((emptyBook != null) && !(emptyBook.getBorrower().equals("none") || emptyBook.getBorrower() == null || emptyBook.getBorrower().equals(""))
                        && emptyBook.getOwner().containsKey(loginEmail)) {
                    updateQuery.acceptReturn(emptyBook.getIsbn(),user.getEmail(),instance,queryOutput);
                } else {
                    Toast.makeText(this, "Failed to receive book!",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * setHopperUpdates will be called after the user clicks one of the buttons to give, borrow,
     * receive, or accept a book. After the button is pressed, it changes the book attributes and
     * setHopperUpdates will save the changes to the database.
     */
    private void setHopperUpdates() {
        hopperUpdates = new HashMap<>();
        hopperUpdates.put("borrower", borrowerView.getText().toString());
        hopperUpdates.put("status", statusView.getText().toString());
        ref.updateChildren(hopperUpdates);
    }

    private void updateFirebase() {
        hopperUpdates = new HashMap<>();
        hopperUpdates.put("borrower", borrowerView.getText().toString());
        hopperUpdates.put("status", statusView.getText().toString());
        documentReference.update(hopperUpdates);
    }
}

