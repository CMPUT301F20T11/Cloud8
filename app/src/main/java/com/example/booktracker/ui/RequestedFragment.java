package com.example.booktracker.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.getBookQuery;
import com.example.booktracker.entities.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class RequestedFragment extends Fragment {

    private ListView listView;
    private String userEmail, userSelected, lastStatus;
    private BookCollection bookCollection;
    private getBookQuery getQuery;
    private View view;
    private Book selected_book = null;
    private DocumentSnapshot userDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requested, container, false);
        HomeActivity activity = (HomeActivity) getActivity();
        userEmail = activity.getUserEmail();
        listView = view.findViewById(R.id.requested_booklist);
        bookCollection = new BookCollection(new ArrayList<Book>(), listView, userEmail, view.getContext());
        getQuery = (new getBookQuery(userEmail, bookCollection,view.getContext()));
        getQuery.getMyBooks("requestedBooks");
        lastStatus = "";

        setSelectListener();
        setViewListener();


        return view;
    }

    private void setViewListener() {
        Button viewBookBtn = (Button) view.findViewById(R.id.requested_view_book_button);
        viewBookBtn.setOnClickListener(view -> {
            if (selected_book != null) {
                Intent intent = new Intent(view.getContext(), ViewBookActivity.class);
                intent.putExtra(EXTRA_MESSAGE,selected_book.getIsbn());
                startActivity(intent);
            }
        });
    }

    /**
     * Set the callback function to keep track of the selected books
     */
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = bookCollection.getBook(position);
            userSelected = selected_book.getOwnerEmail();
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    private Boolean getUserDoc(String owner) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (owner == null) {
            return false;
        } else {
            DocumentReference docRef = db.collection("users").document(owner);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        userDoc = doc;
                    }
                }
            });
        }
        return true;
    }

    /**
     * Refresh the listView when the user return the the HomeActivity in case an update to
     * the BookCollection was made
     */
    @Override
    public void onResume() {
        //this is needed to refresh the list of books displayed when the user goes back to the
        //home activity
        super.onResume();
        if (lastStatus.equals("")) {
            getQuery.getMyBooks("requestedBooks");
        } else {
            getQuery.getMyBooks(lastStatus);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_user) {
            if (getUserDoc(userSelected)) {
                showUserDialog(userDoc);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserDialog(DocumentSnapshot userDoc) {
        String username = userDoc.getString("username");
        String email = userDoc.getString("email");
        String phone = userDoc.getString("phone");
        ViewUserDialog userDialog = ViewUserDialog.newInstance(username, email, phone);
        userDialog.setStyle(STYLE_NO_TITLE, 0);
        userDialog.show(getParentFragmentManager(), "VIEW USER");
    }

}
