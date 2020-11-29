package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;

public class FilterFragment extends DialogFragment {
    private Button accepted;
    private Button available;
    private Button borrowed;
    private Button myBooks;
    private View view;
    private MyBooksFragment parentActivity;

    /**
     * Set the parent activity which is MyBooksFragment to use its methods
     * @param argParent parent activity of type MyBooksFragment
     */
    public FilterFragment(MyBooksFragment argParent) {
        parentActivity = argParent;
    }

    /**
     * Bind buttons and create Dialog fragment
     * @param savedInstanceState
     * @return Dialog with functional buttons for filtering
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_filter,null, false);
        bindViews();
        setAllListeners();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .create();
    }

    /**
     * This will bind the Button views in fragment_filter.xml to the attributes of FilterFragment
     */
    private void bindViews() {
        accepted = view.findViewById(R.id.accepted);
        available = view.findViewById(R.id.available);
        borrowed = view.findViewById(R.id.borrowed);
        myBooks = view.findViewById(R.id.my_books);
    }

    /**
     * set listeners for all buttons
     */
    private void setAllListeners() {
        all_books_listener();
        accepted_listener();
        borrowed_listener();
        available_listener();
    }

    /**
     * display all books the user owns
     */
    private void all_books_listener(){
        myBooks.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooks();
            parentActivity.setLastStatus("myBooks");
            getDialog().dismiss();
        });
    }
    /**
     * set listener for no filter books filter
     */
    private void accepted_listener() {
        accepted.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooksStatus(parentActivity.getEmail(),"accepted");
            parentActivity.setLastStatus("accepted");
            getDialog().dismiss();
        });
    }

    /**
     * set listener for borrowed books filter
     */
    private void borrowed_listener() {
        borrowed.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooksStatus(parentActivity.getEmail(),"borrowed");
            parentActivity.setLastStatus("borrowed");
            getDialog().dismiss();
        });
    }

    /**
     * set listener for available books filter
     */
    private void available_listener() {
        available.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooksStatus(parentActivity.getEmail(),"available");
            parentActivity.setLastStatus("available");
            getDialog().dismiss();
        });
    }
}
