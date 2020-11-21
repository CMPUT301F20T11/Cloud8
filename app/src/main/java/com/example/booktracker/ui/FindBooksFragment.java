package com.example.booktracker.ui;

import android.os.Bundle;
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
import com.example.booktracker.boundary.ResultAdapter;
import com.example.booktracker.entities.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class FindBooksFragment extends Fragment {
    private static final String TAG = FindBooksFragment.class.getName();
    private ListView bookList;
    private ArrayAdapter<Book> resAdapter;
    private ArrayList<Book> bookDataList;
    private Book selected_book = null;
    private FirebaseFirestore db;
    private DocumentSnapshot userDoc;
    private String userSelected;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_book, container,
                false);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        bookList = view.findViewById(R.id.books_found);
        getBooks();
        setSelectListener();

        SearchView searchView = view.findViewById(R.id.book_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                searchView.clearFocus();
                searchBooks(queryText);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    bookList.setAdapter(null);
                }
                return false;
            }
        });

        return view;
    }

    private void setSelectListener() {
        bookList.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = resAdapter.getItem(position);
            if (selected_book.getOwner() != null) {
                userSelected = selected_book.getOwnerEmail();
            } else {
                userSelected = selected_book.getStringOwner();
            }
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    private void searchBooks(String searchText) {
        ArrayList<Book> results = new ArrayList<>();
        for (Book found : bookDataList) {
            if (containsKeyword(found.getDescription(), searchText)) {
                results.add(found);
            }
        }
        if (results.isEmpty()) {
            Toast.makeText(getContext(), "No matching books!",
                    Toast.LENGTH_SHORT).show();
        }
        updateBookList(results);
    }

    private void getBooks() {
        db.collectionGroup("myBooks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No books found!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        bookDataList = new ArrayList<>();
                        List<DocumentSnapshot> booksDocs =
                                queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc : booksDocs) {
                            List<String> authors = (List<String>) doc.get(
                                    "author");
                            if (doc.get("owner") instanceof String) {
                                String stringOwner = (String) doc.get("owner");
                                Book ogBook = new Book(stringOwner, authors,
                                        (String) doc.get("title"),
                                        doc.getId(), (String) doc.get(
                                        "description"));
                                bookDataList.add(ogBook);
                            } else {
                                HashMap<String, String> owner =
                                        (HashMap<String, String>) doc.get(
                                                "owner");
                                Book book = new Book(owner, authors,
                                        (String) doc.get("title"),
                                        doc.getId(), (String) doc.get(
                                        "description"));
                                bookDataList.add(book);
                            }
                        }
                    }
                    updateBookList(bookDataList);
                    bookList.setAdapter(null);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Error getting books!",
                        Toast.LENGTH_SHORT).show());
    }

    private void updateBookList(ArrayList<Book> newList) {
        resAdapter = new ResultAdapter(getContext(), newList);
        bookList.setAdapter(resAdapter);
        resAdapter.notifyDataSetChanged();
    }

    private boolean containsKeyword(String source, String input) {
        return Pattern.compile(Pattern.quote(input),
                Pattern.CASE_INSENSITIVE).matcher(source).find();
    }

    private Boolean getUserDoc(String owner) {
        if (owner == null) {
            return false;
        } else {
            DocumentReference userRef = db.collection("users").document(owner);
            userRef.get().addOnCompleteListener(task -> {
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
        ViewUserDialog userDialog = ViewUserDialog.newInstance(username,
                email, phone);
        userDialog.setStyle(STYLE_NO_TITLE, 0);
        userDialog.show(getParentFragmentManager(), "VIEW BOOK USER");
    }

}