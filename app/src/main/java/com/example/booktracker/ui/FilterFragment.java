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
    Button accepted;
    Button requested;
    Button myBooks;
    Button available;
    Button borrowed;
    View view;
    MyBooksFragment parentActivity;

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
                .inflate(R.layout.fragment_filter,null);
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
        accepted = view.findViewById(R.id.lent);
        requested  = view.findViewById(R.id.requested);
        myBooks = view.findViewById(R.id.my_books);
        available = view.findViewById(R.id.available);
        borrowed = view.findViewById(R.id.borrowed);
    }

    /**
     * set listeners for all buttons
     */
    private void setAllListeners() {
        accepted_listener();
        requested_listener();
        my_books_listener();
        borrowed_listener();
        available_listener();
    }
    /**
     * set listener for accepted books filter
     */
    private void accepted_listener() {
        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getQuery().getMyBooks("accepted");
                getDialog().dismiss();
            }
        });
    }
    /**
     * set listener for requested books filter
     */
    private void requested_listener() {
        requested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getQuery().getMyBooks("requested");
                getDialog().dismiss();
            }
        });
    }
    /**
     * set listener for no filter books filter
     */
    private void my_books_listener() {
        myBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getQuery().getMyBooks();
                getDialog().dismiss();
            }
        });
    }
    /**
     * set listener for borrowed books filter
     */
    private void borrowed_listener() {
        borrowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getQuery().getMyBooks("borrowed");
                getDialog().dismiss();
            }
        });
    }
    /**
     * set listener for available books filter
     */
    private void available_listener() {
        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.getQuery().getMyBooks("available");
                getDialog().dismiss();
            }
        });
    }
}
