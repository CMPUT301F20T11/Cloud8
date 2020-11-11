package com.example.booktracker.boundary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.ui.ProfileFragment;
import com.example.booktracker.ui.ViewUserDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class GetUserQuery extends Fragment {
    private String profileUsername,profileEmail=null,profilePhone=null;
    protected DocumentReference userDoc;
    protected FirebaseFirestore db;
    private Context context;
    /**
     * This will call its parent constructor from BookQuery
     *
     * @param userEmail
     * @param argContext
     */
    public GetUserQuery(String userEmail, Context argContext) {
        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(userEmail);
        context = argContext;
    }

    public void ViewUser() {
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task){
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    profileUsername=document.getString("username");
                    profileEmail=document.getString("email");
                    profilePhone=document.getString("phone");

                    ViewUserDialog userDialog = new ViewUserDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", profileUsername);
                    bundle.putString("email", profileEmail);
                    bundle.putString("phone", profilePhone);
                    userDialog.setArguments(bundle);
                    userDialog.show(getFragmentManager(), "ViewUserDialog");
                }
            }
        });
    }
}
