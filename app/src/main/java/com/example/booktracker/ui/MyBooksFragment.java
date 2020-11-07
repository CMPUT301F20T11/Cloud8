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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class MyBooksFragment extends Fragment implements ViewUserDialog.OnFragmentInteractionListener {
    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    private GetBookQuery getQuery;
    private String userEmail, userSelected;
    private View view;
    private DeleteBookQuery del;
    private BookCollection collection;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private DocumentSnapshot userDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //=============set attributes=======================
        view = inflater.inflate(R.layout.fragment_my_books, container, false);
        HomeActivity activity = (HomeActivity) getActivity();
        bookList = view.findViewById(R.id.my_book_list);
        userEmail = ((HomeActivity) activity).getUserEmail();
        collection = new BookCollection(new ArrayList<>(), bookList,
                userEmail, view.getContext());
        del = new DeleteBookQuery(userEmail);
        getQuery = (new GetBookQuery(userEmail, collection, view.getContext()));
        setHasOptionsMenu(true);
        //======================================================

        setSelectListener();
        setDeleteListener();
        setViewListener();

        //=============execute async operation=======
        //books will be displayed after async operation is done
        getQuery.getMyBooks();
        //===========================================
        Button addBookBtn = (Button) view.findViewById(R.id.add_book_button);
        addBookBtn.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(),
                    AddBookActivity.class);
            intent.putExtra(EXTRA_MESSAGE, userEmail);
            startActivity(intent);
        });
        Button editBookBtn = (Button) view.findViewById(R.id.edit_book_button);
        editBookBtn.setOnClickListener(view -> {
            if (selected_book != null) {
                Intent intent = new Intent(view.getContext(),
                        EditBookActivity.class);
                intent.putExtra("USER_EMAIL", userEmail);
                intent.putExtra("BOOK", selected_book);
                startActivity(intent);
            } else {
                Toast.makeText(view.getContext(), "No book selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button filterBookBtn = (Button) view.findViewById(R.id.filter_button);
        filterBookBtn.setOnClickListener(view -> {
            //filter fragment
        });

        return view;
    }

    private void setViewListener() {
        Button viewBookBtn = (Button) view.findViewById(R.id.view_book_button);
        viewBookBtn.setOnClickListener(view -> {
            if (selected_book != null) {
                Intent intent = new Intent(view.getContext(),
                        ViewBookActivity.class);
                intent.putExtra(EXTRA_MESSAGE, selected_book.getIsbn());
                startActivity(intent);
            }
        });
    }

    /**
     * Set the callback function to be executed when a book need to be deleted
     */
    private void setDeleteListener() {
        Button deleteBookBtn =
                (Button) view.findViewById(R.id.delete_book_button);
        deleteBookBtn.setOnClickListener(view -> {
            if (selected_book != null && selected_book.getOwner().trim().equals(userEmail.trim())) {
                del.deleteBook(selected_book);//remove book from database
                collection.deleteBook(selected_book);//remove book from
                // listview
            } else {
                Toast.makeText(view.getContext(), "Book cant be deleted",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Set the callback function to keep track of the selected books
     */
    private void setSelectListener() {
        bookList.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = collection.getBook(position);
            userSelected = selected_book.getOwner();
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    private Boolean getUserDoc(String owner) {
        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(userEmail);
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

    /**
     * Refresh the listView when the user return the the HomeActivity in case
     * an update to
     * the BookCollection was made
     */
    @Override
    public void onResume() {
        //this is needed to refresh the list of books displayed when the user
        // goes back to the
        //home activity
        super.onResume();
        getQuery.getMyBooks();
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
                ViewUserDialog userDialog = new ViewUserDialog();
                userDialog.setTargetFragment(MyBooksFragment.this, 420);
                userDialog.setStyle(STYLE_NO_TITLE, 0);
                userDialog.show(getParentFragmentManager(), "VIEW USER");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public DocumentSnapshot getProfile() {
        return userDoc;
    }

    @Override
    public void onOk() {
    }
}