package com.example.booktracker.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.booktracker.R;
import com.example.booktracker.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewUserDialog extends DialogFragment {
    private String profileUsername, profileEmail = null, profilePhone = null;
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        Bundle mArgs = getArguments();
        profileUsername = mArgs.getString("username");
        profileEmail = mArgs.getString("email");
        profilePhone = mArgs.getString("phone");;

        return builder
                .setView(getView())
                .setTitle("User Profile")
                .setMessage(profileUsername + "\n" + profileEmail + "\n" + profilePhone)
                .setPositiveButton("OK", (dialogInterface, i) -> {
                })
                .create();
    }
}
