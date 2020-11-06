package com.example.booktracker.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.ui.AddBookActivity;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MyBooksFragment extends Fragment{


    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    private GetBookQuery getQuery;
    private String userEmail;
    private View view;
    private DeleteBookQuery del;
    private BookCollection collection;
//    CustomList customBookList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //=============set attributes=======================
        view = inflater.inflate(R.layout.fragment_my_books, container, false);
        HomeActivity activity = (HomeActivity) getActivity();
        bookList = view.findViewById(R.id.my_book_list);
        userEmail = ((HomeActivity)activity).getUserEmail();
        collection = new BookCollection(new ArrayList<Book>(),bookList,userEmail,view.getContext());
        del = new DeleteBookQuery(userEmail);
        getQuery = (new GetBookQuery(userEmail,collection,view.getContext()));
        //======================================================

        setSelectListener();
        setDeleteListener();
        //=============execute async operation=======
        //books will be displayed after async operation is done
        getQuery.getMyBooks();
        //===========================================
        Button addBookBtn = (Button) view.findViewById(R.id.add_book_button);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AddBookActivity.class);
                intent.putExtra(EXTRA_MESSAGE,userEmail);
                startActivity(intent);
            }
        });
        Button editBookBtn = (Button) view.findViewById(R.id.edit_book_button);
        editBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_book != null) {
                    Intent intent = new Intent(view.getContext(), EditBookActivity.class);
                    intent.putExtra("USER_EMAIL", userEmail);
                    intent.putExtra("BOOK", selected_book);
                    startActivity(intent); }
                else {
                    Toast.makeText(view.getContext(), "No book selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button filterBookBtn = (Button) view.findViewById(R.id.filter_button);
        filterBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //filter fragment
            }
        });
        return view;
    }
    /**
     * Set the callback function to be executed when a book need to be deleted
     */
    private void setDeleteListener(){
        Button deleteBookBtn = (Button) view.findViewById(R.id.delete_book_button);
        deleteBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_book != null && selected_book.getOwner().trim().equals(userEmail.trim())){
                    del.deleteBook(selected_book);//remove book from database
                    collection.deleteBook(selected_book);//remove book from listview
                }else{
                    Toast.makeText(view.getContext(), "Book cant be deleted", Toast.LENGTH_LONG).show();
                }


            }
        });
    }
    /**
     * Set the callback function to keep track of the selected books
     */
    private void setSelectListener(){
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                selected_book = collection.getBook(position);

            }
        });
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
        getQuery.getMyBooks();
    }
}