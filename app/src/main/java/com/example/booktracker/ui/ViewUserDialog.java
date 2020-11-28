package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;

public class ViewUserDialog extends DialogFragment {
    private String profileName, profileEmail, profilePhone;

    public static ViewUserDialog newInstance(String user, String email, String phone) {
        ViewUserDialog userDialog = new ViewUserDialog();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("email", email);
        args.putString("phone", phone);
        userDialog.setArguments(args);
        return userDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final View view =
                LayoutInflater.from(getActivity()).inflate(R.layout.fragment_view_user, null);

        Bundle bundle = getArguments();
        if (bundle != null) {
            profileName = bundle.getString("user");
            profileEmail = bundle.getString("email");
            profilePhone = bundle.getString("phone");
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

        Button button = view.findViewById(R.id.view_user_exit_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
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
            Button okButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            okButton.setOnClickListener(view -> dialog.dismiss());
        }
    }
}
