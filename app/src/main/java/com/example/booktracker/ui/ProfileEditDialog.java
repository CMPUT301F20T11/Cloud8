package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;

public class ProfileEditDialog extends DialogFragment {
    private EditText emailText, phoneText;
    private onEditListener listener;
    private String profileEmail = null, profilePhone = null;

    public interface onEditListener {
        void onEditOk(String email, String phone);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final ProfileFragment fragment = (ProfileFragment) getTargetFragment();
        final View view =
                LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_profile, null);
        if (fragment != null) {
            profileEmail = fragment.getProfileEmail();
            profilePhone = fragment.getProfilePhone();
        }
        listener = (onEditListener) getTargetFragment();

        // Displays the user's info in the input fields for editing
        emailText = view.findViewById(R.id.edit_email);
        emailText.setText(profileEmail);

        phoneText = view.findViewById(R.id.edit_phone);
        phoneText.setText(profilePhone);

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Edit Contact Information")
                .setNegativeButton("Cancel", null)
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
            okButton.setOnClickListener(view -> {
                String newEmail = emailText.getText().toString().trim();
                String newPhone = phoneText.getText().toString().trim();

                boolean closeDialog = false;
                boolean allValid = true;

                if (newEmail.isEmpty()) {
                    allValid = false;
                    emailText.setError("This field cannot be left empty!");
                    emailText.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    allValid = false;
                    emailText.setError("Please enter a valid email!");
                    emailText.requestFocus();
                }

                if (newPhone.isEmpty()) {
                    allValid = false;
                    phoneText.setError("This field cannot be left empty.");
                    phoneText.requestFocus();
                } else if (!Patterns.PHONE.matcher(newPhone).matches()) {
                    allValid = false;
                    phoneText.setError("Please enter a valid phone number!");
                    phoneText.requestFocus();
                }

                if (allValid) {
                    listener.onEditOk(newEmail, newPhone);
                    closeDialog = true;
                }

                if (closeDialog) {
                    dialog.dismiss();
                }
            });
        }
    }
}
