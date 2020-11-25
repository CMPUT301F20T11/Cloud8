package com.example.booktracker.ui;

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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.ResultAdapter;
import com.example.booktracker.boundary.getBookQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class FindBooksFragment extends Fragment implements Callback {
    private static final String TAG = FindBooksFragment.class.getName();
    private ListView bookList;
    private ArrayAdapter<Book> resAdapter;
    private ArrayList<Book> bookDataList;
    private Book selected_book = null;
    private FirebaseFirestore db;
    private DocumentSnapshot userDoc;
    private String userSelected, searchText, userEmail;
    private getBookQuery query;
    private FindBooksFragment instance = this;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_book, container,
                false);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        bookList = view.findViewById(R.id.books_found);
        query = new getBookQuery();
        bookDataList = new ArrayList<Book>();
        setSelectListener();
        HomeActivity home = (HomeActivity) getActivity();
        userEmail = home.getUserEmail();

        SearchView searchView = view.findViewById(R.id.book_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                bookDataList.clear();
                query.getBooks(instance,bookDataList);
                searchText = queryText;
                searchView.clearFocus();
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

        Button requestBtn = view.findViewById(R.id.request_book_button);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_book != null) {
                    Request request = new Request(userEmail, userSelected, selected_book, getContext());
                    request.sendRequest();
                } else {
                    Toast.makeText(view.getContext(), "No book selected", Toast.LENGTH_SHORT).show();
                }
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
    public void executeCallback(){
        searchBooks(searchText);
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