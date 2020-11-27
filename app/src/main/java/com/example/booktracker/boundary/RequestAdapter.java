package com.example.booktracker.boundary;

import android.content.Context;
import android.text.TextUtils;
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
import com.example.booktracker.entities.Request;

import java.util.ArrayList;

/**
 * ArrayAdapter class for the Request entity
 *
 * @author Edlee Ducay
 */
public class RequestAdapter extends ArrayAdapter<Request> {
    private ArrayList<Request> requestList;
    private Context context;

    /**
     * Constructor for RequestAdapter class
     *
     * @param argContext
     * @param argRequestList
     */
    public RequestAdapter(Context argContext, ArrayList<Request> argRequestList) {
        super(argContext, 0, argRequestList);
        this.requestList = argRequestList;
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
            view = LayoutInflater.from(context).inflate(R.layout.request_adapter_content, parent, false);
        }

        Request request = requestList.get(position);

        TextView mainView = view.findViewById(R.id.request_all_text);
        ImageView imageView = view.findViewById(R.id.request_imageView);

        Book book = request.getBook();
        String title = book.getTitle();
        String authors = TextUtils.join(",", book.getAuthor());
        String fromUsername = request.getFromUsername();
        String result = title + "\n" + authors + "\n" + fromUsername;
        mainView.setText(result);
        if (book.getUri() != null) {
            Glide.with(view).load(book.getUri()).into(imageView);
        } else {
            Glide.with(view).load(R.drawable.ic_stock_book_photo_foreground).into(imageView);
        }

        return view;
    }
}
