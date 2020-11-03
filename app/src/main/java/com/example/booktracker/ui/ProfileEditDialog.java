package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
    private EditText email, phone;
    private onEditListener listener;

    public interface onEditListener {
        void onEditOk(String email, String phone);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onEditListener) {
            listener = (onEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onEditListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View view =
                LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_profile, null);
        final ProfileActivity activity = (ProfileActivity) getActivity();
        String profileEmail = activity.getUserEmail();
        String profilePhone = activity.getUserPhone();

        // Displays the user's info in the input fields for editing
        email = view.findViewById(R.id.edit_email);
        email.setText(profileEmail);

        phone = view.findViewById(R.id.edit_phone);
        phone.setText(profilePhone);

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
                String newEmail = email.getText().toString().trim();
                String newPhone = phone.getText().toString().trim();

                boolean closeDialog = false;
                boolean allValid = true;

                if (newEmail.isEmpty()) {
                    allValid = false;
                    email.setError("This field cannot be left empty!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    allValid = false;
                    email.setError("Please enter a valid email!");
                    email.requestFocus();
                }

                if (newPhone.isEmpty()) {
                    allValid = false;
                    phone.setError("This field cannot be left empty.");
                    phone.requestFocus();
                } else if (!Patterns.PHONE.matcher(newPhone).matches()) {
                    allValid = false;
                    email.setError("Please enter a valid email!");
                    email.requestFocus();
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
