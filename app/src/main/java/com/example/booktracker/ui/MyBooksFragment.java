package com.example.booktracker.ui.Books;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.entities.Book;
import com.example.booktracker.ui.Activities.AddBookActivity;

import java.util.ArrayList;

public class MyBooksFragment extends Fragment implements View.OnClickListener {


    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
//    CustomList customBookList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);

        Button addBookBtn = (Button) view.findViewById(R.id.add_book_button);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AddBookActivity.class)); }
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