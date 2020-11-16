package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookAdapter;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.User;
import com.example.booktracker.ui.ScanActivity;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Placeholder fragment to navigate to Scan activity from Nav drawer
 */
public class ScanFragment extends Fragment implements View.OnClickListener {
    //declarations
    ListView bookListView;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selectedBook = null;
    ArrayList<String> authors;
    private static final int ACTIVITY_REQUEST_CODE = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        Intent intent = new Intent(view.getContext(), ScanActivity.class);
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        // set list adapter
        bookListView = (ListView) view.findViewById(R.id.scanned_books_list);
        bookDataList = new ArrayList<Book>();
        bookAdapter = new BookAdapter(view.getContext(), bookDataList);
        bookListView.setAdapter(bookAdapter);
        authors = new ArrayList<String>();

        // Set on click for borrow button (US 06.02.01)
        Button borrowButton = (Button) view.findViewById(R.id.borrow_book_button);
        borrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since we are the borrower in this case, we need to check for book.borrower == none,
                // book.status == available, book != null, and then book.setBorrower("user.email")
                if ((selectedBook.getStatus() == "unavailable") && (selectedBook.getBorrower() == "none") && (selectedBook != null)) {
                    //set borrower properly here later
                    selectedBook.setBorrower("USER EMAIL HERE");
                    bookAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "Book Successfully Borrowed!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "Failed to borrow book!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // On click for give button (US 06.01.01)
        Button giveButton = (Button) view.findViewById(R.id.give_book_button);
        giveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Since we are the owner in this case, we should check for book.owner == user.email
                // and book.borrower == none, and book.status == available, and book != null
                if ((selectedBook.getStatus() == "available") && (selectedBook.getBorrower() != "none") && (selectedBook != null)) {
                    selectedBook.setStatus("unavailable");
                    bookAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "Book Successfully given!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "Failed to borrow book!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set on click for return button (07.01.01)
        Button returnButton = (Button) view.findViewById(R.id.return_book_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here we are the borrower attempting to hand over the book, so we must check that
                // borrower == user, status == unavailable, then set it to borrower == none
                if (selectedBook != null){
                    selectedBook.setBorrower("none");
                    bookAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "Book Successfully Returned!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "Return Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set on click for receiving button (US 07.02.01)
        Button receiveButton = (Button) view.findViewById(R.id.receive_book_button);
        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Here we are the owner receiving a book that has been returned, so we must check
                // that we own the book, no one is borrowing it, and then set status to available
                if ((selectedBook.getBorrower() == "none") && (selectedBook != null)) {
                    selectedBook.setStatus("available");
                    bookAdapter.notifyDataSetChanged();
                    Toast.makeText(view.getContext(), "Book Successfully Received!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(view.getContext(), "Failed to receive book!", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set on click listener for items in list, will select the position of book
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                selectedBook = bookDataList.get(itemPosition);
            }
        });

        return view;

    }

    /**
     * onActivityResult: When the scan result comes back, this function will be called and we will
     * add the scanned book to a list where we can view the books scanned so far.
     * @author Andrew Wood <awood@ualberta.ca>
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //get isbn string
                authors.add("author");
                String isbnResult = data.getStringExtra("isbn");
                Book scannedBook = new Book(isbnResult, authors, "title", isbnResult, "desc");
                bookAdapter.add(scannedBook);
            }
        }
    }

    /**
     * This method assigns an on click listener to each item in our list view such that when the
     * user taps on an item, this function will assign a variable to track which book it is.
     * @author Andrew Wood <awood@ualberta.ca>
     * @param v
     */
    @Override
    public void onClick(View v) {
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                selectedBook = bookDataList.get(position);
            }
        });
    }
}
