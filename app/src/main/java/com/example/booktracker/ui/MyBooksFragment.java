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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.ui.AddBookActivity;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MyBooksFragment extends Fragment implements View.OnClickListener {


    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    private GetBookQuery getQuery;
//    CustomList customBookList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);
        HomeActivity activity = (HomeActivity) getActivity();


        //=============execute async operation===============
        String userEmail = ((HomeActivity)activity).getUserEmail();
        //books will be displayed after async operation is done
        getQuery = (new GetBookQuery(userEmail));
        getQuery.getMyBooks((ListView) view.findViewById(R.id.my_book_list),view.getContext());
        //===================================================
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
                //edit book frag
            }
        });
        Button filterBookBtn = (Button) view.findViewById(R.id.filter_button);
        filterBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //filter fragment
            }
        });
        Button deleteBookBtn = (Button) view.findViewById(R.id.delete_book_button);
        deleteBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete frag (confirm)
            }
        });

        return view;
    }

    // know what book is referenced when view profile option selected
    @Override
    public void onClick(View v) {
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                selected_book = bookDataList.get(position);
            }
        });
    }


}