package com.example.booktracker.boundary;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.booktracker.R;
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> bookList = new ArrayList<>();
    private Context context;

    /**
     * Constructor for BookAdapter class
     * @param argContext
     * @param argBookList
     */
    public BookAdapter(Context argContext, ArrayList<Book> argBookList) {
        super(argContext, 0, argBookList);
        this.bookList = argBookList;
        this.context = argContext;
    }

    /**
     * Binds layout inflater to a list view
     * @param position
     * @param convertView
     * @param parent
     * @return the view for corresponding params
     */
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.book_adapter_content, parent, false);
        }

        Book book = bookList.get(position);

        TextView bookTitle = view.findViewById(R.id.title_text);
        TextView bookAuthor = view.findViewById(R.id.author_text);
        TextView bookOwner = view.findViewById(R.id.owner_text);
        TextView bookStatus = view.findViewById(R.id.status_text);

        StringBuilder authors = new StringBuilder();
        for (String s: book.getAuthor()) {
            authors.append(s);
            authors.append(", ");
        }

        bookTitle.setText(book.getTitle());
        bookAuthor.setText(authors.toString());
        bookOwner.setText(book.getOwner());
        bookStatus.setText(book.getStatus());


        return view;
    }

}
