package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;

public class FilterFragment extends DialogFragment {
    private Button lent;
    private Button requested;
    private Button accepted;
    private Button available;
    private Button borrowed;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
        return builder
                .setView(view)
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }
    }

    /**
     * This will bind the Button views in fragment_filter.xml to the attributes of FilterFragment
     */
    private void bindViews() {
        borrowed = view.findViewById(R.id.borrowed);
        accepted = view.findViewById(R.id.accepted);
        available = view.findViewById(R.id.available);

    }

    /**
     * set listeners for all buttons
     */
    private void setAllListeners() {
        accepted_listener();
        borrowed_listener();
        available_listener();
    }

    /**
     * set listener for no filter books filter
     */
    private void accepted_listener() {
        accepted.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooks();
            getDialog().dismiss();
        });
    }

    /**
     * set listener for borrowed books filter
     */
    private void borrowed_listener() {
        borrowed.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooks("borrowed");
            getDialog().dismiss();
        });
    }

    /**
     * set listener for available books filter
     */
    private void available_listener() {
        available.setOnClickListener(v -> {
            parentActivity.getQuery().getMyBooks("available");
            getDialog().dismiss();
        });
    }
}
