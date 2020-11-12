package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;

public class ViewUserDialog extends DialogFragment {
    private String profileName, profileEmail, profilePhone;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final MyBooksFragment booksFragment = (MyBooksFragment) getTargetFragment();
        final View view =
                LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_user, null);

        if (booksFragment != null) {
            profileName = booksFragment.getProfile().getString("username");
            profileEmail = booksFragment.getProfile().getString("email");
            profilePhone = booksFragment.getProfile().getString("phone");
        }

        // Displays the user's info in the input fields for editing

        TextView title = view.findViewById(R.id.view_Title);
        title.setPaintFlags(title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        TextView userText = view.findViewById(R.id.view_User);
        userText.setText(profileName);

        TextView emailText = view.findViewById(R.id.view_Email);
        emailText.setText(profileEmail);

        TextView phoneText = view.findViewById(R.id.view_Phone);
        phoneText.setText(profilePhone);

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                })
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button okButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(view -> dialog.dismiss());
        }
    }
}
