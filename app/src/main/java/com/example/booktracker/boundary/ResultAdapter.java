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

public class ResultAdapter extends ArrayAdapter<Book> {
    private ArrayList<Book> bookList;
    private Context context;

    public ResultAdapter(Context argContext, ArrayList<Book> argBookList) {
        super(argContext, 0, argBookList);
        this.bookList = argBookList;
        this.context = argContext;
    }

    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.result_adapter_content, parent, false);
        }
        Book book = bookList.get(position);

        ImageView imageView = view.findViewById(R.id.result_image);
        TextView titleView = view.findViewById(R.id.result_title);
        TextView descView = view.findViewById(R.id.result_desc);
        TextView ownerView = view.findViewById(R.id.result_owner);
        TextView statusView = view.findViewById(R.id.result_status);

        String title = book.getTitle();
        titleView.setText(title);

        String desc = book.getDescription();
        descView.setText(desc);

        String owner = book.getOwnerName();
        ownerView.setText(owner);

        String status = book.getStatus();
        statusView.setText(status);

        if (book.getUri() != null) {
            Glide.with(view).load(book.getUri()).into(imageView);
        } else {
            Glide.with(view).load(R.drawable.ic_stock_book_photo_foreground).into(imageView);
        }

        return view;
    }

}
