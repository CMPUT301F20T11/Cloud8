package com.example.booktracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public class BorrowedBooksFragment extends Fragment implements View.OnClickListener {
    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_borrowed_books, container, false);
    }

    // know what book is referenced when view profile option selected
    @Override
    public void onClick(View v) {
        bookList.setOnItemClickListener((adapter, v1, position, id) -> selected_book = bookDataList.get(position));
    }
}