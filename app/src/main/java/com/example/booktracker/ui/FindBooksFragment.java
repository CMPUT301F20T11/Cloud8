package com.example.booktracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookAdapter;
import com.example.booktracker.entities.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class FindBooksFragment extends Fragment {
    private static final String TAG = FindBooksFragment.class.getName();
    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private DocumentSnapshot userDoc;
    private String userSelected;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_book, container,
                false);
        // include custom viewUser action for this fragment
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        bookList = view.findViewById(R.id.books_found);

        bookDataList = new ArrayList<>();
        bookAdapter = new BookAdapter(view.getContext(), bookDataList);
        bookList.setAdapter(bookAdapter);

        SearchView searchView = view.findViewById(R.id.book_search);

        setSelectListener();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchText) {
                searchBooks(searchText);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return view;
    }

    private void setSelectListener() {
        bookList.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = bookDataList.get(position);
            userSelected = selected_book.getOwner();
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    private void searchBooks(String searchText) {
        bookDataList.clear();
        db.collectionGroup("myBooks").whereEqualTo(
                "description", searchText.trim())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    }
                    else {
                        List<Book> results = queryDocumentSnapshots.toObjects(Book.class);
                        bookDataList.addAll(results);
                        Log.d(TAG, "onSuccess: " + bookDataList);
                        }
                    bookAdapter.notifyDataSetChanged();
                    })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error getting data!",
                        Toast.LENGTH_LONG).show());
    }

    private Boolean getUserDoc(String owner) {
        db = FirebaseFirestore.getInstance();
        if (owner == null) {
            return false;
        } else {
            docRef =
                    db.collection("users").document(owner);
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
        userDialog.show(getParentFragmentManager(), "VIEW BOOK USER");
    }

}