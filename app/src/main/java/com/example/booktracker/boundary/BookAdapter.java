package com.example.booktracker.boundary;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.booktracker.R;
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

/**
 * ArrayAdapter class for the Book entity
 *
 * @author Edlee Ducay
 */
public class BookAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> bookList;
    private Context context;

    /**
     * Constructor for BookAdapter class
     *
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
     *
     * @param position
     * @param convertView
     * @param parent
     * @return the view for corresponding params
     */
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.book_adapter_content, parent, false);
        }
        Book book = bookList.get(position);

        ImageView imageView = view.findViewById(R.id.book_imageView);
        TextView titleView = view.findViewById(R.id.book_adapter_title);
        TextView ownerView = view.findViewById(R.id.book_adapter_owner);
        TextView descView = view.findViewById(R.id.book_adapter_desc);
        TextView statusView = view.findViewById(R.id.book_adapter_status);

        StringBuilder authors = new StringBuilder();
        for (String s : book.getAuthor()) {
            authors.append(s);
            authors.append(", ");
        }

        String title = book.getTitle();
        String desc = book.getDescription();
        String status = book.getStatus();
        switch (status) {
            case "available":
                statusView.setBackground(this.context.getResources().getDrawable(R.drawable.status_available, null));
                break;
            case "borrowed":
                statusView.setBackground(this.context.getResources().getDrawable(R.drawable.status_borrowed, null));
                break;
            case "requested":
                statusView.setBackground(this.context.getResources().getDrawable(R.drawable.status_requested, null));
                break;
            case "accepted":
                statusView.setBackground(this.context.getResources().getDrawable(R.drawable.status_accepted, null));
                break;
        }

        if (book.getOwner() != null) {
            String owner = book.getOwnerName();
            titleView.setText(title);
            ownerView.setText(owner);
            descView.setText(desc);
            statusView.setText(status);
            if (book.getUri() != null) {
                Glide.with(view).load(book.getUri()).into(imageView);
            } else {
                Glide.with(view).load(R.drawable.ic_stock_book_photo_foreground).into(imageView);
            }
        }

        return view;
    }
}
