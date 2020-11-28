package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment implements ProfileEditDialog.onEditListener {
    private static final String TAG = ProfileFragment.class.getName();
    private TextView nameText, emailText, phoneText;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private String userName, userEmail, loginEmail, userPhone;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        nameText = profileView.findViewById(R.id.username);
        emailText = profileView.findViewById(R.id.email);
        phoneText = profileView.findViewById(R.id.phone);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        if (user != null) {
            loginEmail = user.getEmail();
        }
        HomeActivity activity = (HomeActivity) getActivity();
        activity.notifRefresh();
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(loginEmail);

        Button editButton = profileView.findViewById(R.id.edit_profile_btn);
        Button logoutButton = profileView.findViewById(R.id.logout_btn);

        editButton.setOnClickListener(view -> {
            ProfileEditDialog editDialog = new ProfileEditDialog();
            editDialog.setTargetFragment(ProfileFragment.this, 1337);
            editDialog.show(getParentFragmentManager(), "EDIT PROFILE");
        });

        logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(profileView.getContext(),
                    SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadProfile(loginEmail);
        return profileView;
    }

    private void loadProfile(String loginEmail) {
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null) {
                    userName = doc.getString("username");
                    userEmail = doc.getString("email");
                    userPhone = doc.getString("phone");

                    nameText.setText(userName);
                    if (userEmail != null) {
                        emailText.setText(userEmail);
                    } else {
                        docRef =
                                db.collection("users").document(loginEmail);
                        docRef.update("email", loginEmail);
                        emailText.setText(loginEmail);
                    }
                    phoneText.setText(userPhone);
                }
            }
        });
    }

    public String getProfileEmail() {
        return emailText.getText().toString();
    }

    public String getProfilePhone() {
        return phoneText.getText().toString();
    }

    @Override
    public void onEditOk(String email, String phone) {
        loginEmail = user.getEmail();
        docRef
                .update("phone", phone,
                        "email", email)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Phone number " +
                        "successfully changed!"))
                .addOnFailureListener(e -> Log.d(TAG, "Phone number failed to" +
                        " change!" + e.toString()));
        loadProfile(loginEmail);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_view_user);
        if (item != null) {
            item.setVisible(false);
        }
    }
}
