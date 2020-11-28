package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class BorrowedBooksFragment extends Fragment {
    Book selected_book = null;
    private ListView listView;
    private ArrayList<Book> bookList;
    private GetBookQuery getBookQuery;
    private BookCollection books;
    private View view;
    private final Request selected_request = null;
    private String email, userSelected;
    private HomeActivity activity;
    private Button viewButton;
    private DocumentSnapshot userDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_borrowed_books, container, false);
        setHasOptionsMenu(true);
        activity = (HomeActivity) getActivity();
        email = activity.getUserEmail();
        bookList = new ArrayList<>();
        listView = view.findViewById(R.id.my_book_list);
        viewButton = view.findViewById(R.id.view_button_borrowed);
        books = new BookCollection(bookList, listView, email, view.getContext());
        getBookQuery = new GetBookQuery(activity.getUserEmail(), books, view.getContext());
        setViewListener();
        setSelectListener();

        return view;
    }

    private void setViewListener() {
        viewButton.setOnClickListener(view -> {
            if (selected_book != null) {
                Intent intent = new Intent(view.getContext(), ViewBookActivity.class);
                intent.putExtra(EXTRA_MESSAGE, selected_book.getIsbn());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBookQuery.getMyBooks("borrowed");
        activity.notifRefresh();
    }

    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = books.getBook(position);
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
                return false;
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