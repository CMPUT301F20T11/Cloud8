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
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookAdapter;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
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
    private GetBookQuery query;
    private UpdateQuery updateQuery;
    private FindBooksFragment instance = this;
    private HomeActivity home;
    private Button viewButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_book, container,
                false);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        bookList = view.findViewById(R.id.books_found);
        query = new GetBookQuery();
        bookDataList = new ArrayList<>();
        home = (HomeActivity) getActivity();
        userEmail = home.getUserEmail();
        updateQuery = new UpdateQuery();
        home.notifRefresh();
        viewButton = view.findViewById(R.id.view_button);
        setViewListener();
        setSelectListener();

        SearchView searchView = view.findViewById(R.id.book_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                bookDataList.clear();
                query.getBooks(instance, bookDataList);
                searchText = queryText;
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateBookList(bookDataList);
                }
                return false;
            }
        });

        Button requestBtn = view.findViewById(R.id.request_book_button);
        requestBtn.setOnClickListener(v -> {
            if (selected_book != null) {
                Request request = new Request(userEmail, userSelected, selected_book, getContext());
                updateQuery.incrementNotif(request.getToEmail(),"incomingCount");
                request.sendRequest();
                bookDataList = new ArrayList<>();
                query.getBooks(instance, bookDataList);
            } else {
                Toast.makeText(v.getContext(), "No book selected", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void setSelectListener() {
        bookList.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = resAdapter.getItem(position);
            if (selected_book.getOwner() != null) {
                userSelected = selected_book.getOwnerEmail();
            }
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        query.getBooks(instance, bookDataList);
        home.notifRefresh();
    }
    private ArrayList<String> lowerCaseString(List<String> arg){
        ArrayList<String> out = new ArrayList<String>();
        for (String auth:arg){
            out.add(auth.toLowerCase());
        }
        return out;
    }
    private void searchBooks(String searchText) {
        ArrayList<Book> results = new ArrayList<>();

        for (Book found : bookDataList) {
            if (searchDescription(found.getDescription(), searchText) && !found.getStatus().equals("accepted") && !found.getStatus().equals("borrowed")) {
                results.add(found);
            }
            if (found.getKeywordList() != null) {
                if (containsKeyword(found.getKeywords(), searchText) && !found.getStatus().equals("accepted") && !found.getStatus().equals("borrowed")) {
                    results.add(found);
                }
            }
        }
        if (results.isEmpty()) {
            Toast.makeText(getContext(), "No matching books!",
                    Toast.LENGTH_SHORT).show();
        }
        updateBookList(results);
    }

    public void executeCallback() {
        if (searchText == null) {
            updateBookList(bookDataList);
        } else {
            searchBooks(searchText);
        }
    }

    private void updateBookList(ArrayList<Book> newList) {
        resAdapter = new BookAdapter(getContext(), newList);
        bookList.setAdapter(resAdapter);
        resAdapter.notifyDataSetChanged();
    }

    private boolean searchDescription(String desc, String input) {
        return Pattern.compile(Pattern.quote(input),
                Pattern.CASE_INSENSITIVE).matcher(desc).find();
    }

    private boolean containsKeyword(String keywords, String input) {
        return Pattern.compile("\\b" + input + "\\b",
                Pattern.CASE_INSENSITIVE).matcher(keywords).find();
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
        ViewUserDialog userDialog = ViewUserDialog.newInstance(username, email, phone);
        userDialog.setStyle(STYLE_NO_TITLE, 0);
        userDialog.show(getParentFragmentManager(), "VIEW BOOK USER");
    }
}