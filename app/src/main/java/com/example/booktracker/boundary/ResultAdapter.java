package com.example.booktracker.boundary;


import android.annotation.SuppressLint;
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
    private final ArrayList<Book> bookList;
    private final Context context;

    public ResultAdapter(Context argContext, ArrayList<Book> argBookList) {
        super(argContext, 0, argBookList);
        this.bookList = argBookList;
        this.context = argContext;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.result_adapter_content, parent, false);
        }
        Book book = bookList.get(position);

        TextView mainView = view.findViewById(R.id.result_text);
        ImageView imageView = view.findViewById(R.id.book_image);

        String title = book.getTitle();
        String desc = book.getDescription();
        String status = book.getStatus();
        String owner = book.getOwnerName();
        String result = title + "\n" + desc + "\n" + owner + "\n" + status;
        mainView.setText(result);
        if (book.getUri() != null) {
            Glide.with(view).load(book.getUri()).into(imageView);
        } else {
            Glide.with(view).load(R.drawable.ic_stock_book_photo_foreground).into(imageView);
        }

        return view;
    }

}
