package com.example.booktracker.ui;

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
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.RequestCollection;
import com.example.booktracker.boundary.RequestQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class AvailableFragment extends Fragment{
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    private UpdateQuery updateQuery;
    private ListView listView;
    private ArrayList<Book> bookList;
    private GetBookQuery getBookQuery;
    private BookCollection book;
    private View view;
    private Request selected_request = null;
    private String email;
    private HomeActivity activity;
    private Button viewButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_available_books, container, false);
        activity = (HomeActivity) getActivity();
        email = activity.getUserEmail();
        bookList = new ArrayList<Book>();
        listView = view.findViewById(R.id.my_book_list);
        viewButton = view.findViewById(R.id.view_button_accepted);
        setViewListener();
        book = new BookCollection(bookList,listView,email,view.getContext());
        getBookQuery = new GetBookQuery(activity.getUserEmail(), book,view.getContext());
        setSelectListener();
        return view;
    }
    private void setViewListener(){
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_book != null){
                    Intent intent = new Intent(view.getContext(),ViewBookActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, selected_book.getIsbn());
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getBookQuery.getAvailable(email);
        activity.notifRefresh();
    }
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = book.getBook(position);
        });
    }
}