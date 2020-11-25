package com.example.booktracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public class AcceptedReqFragment extends Fragment {
    private UpdateQuery updateQuery;
    private ListView listView;
    private ArrayList<Book> bookList;
    private GetBookQuery getBookQuery;
    private BookCollection bookCollection;
    private View view;
    private Book selected_book = null;
    private String email;
    private HomeActivity activity;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_req, container, false);

        activity = (HomeActivity) getActivity();
        email = activity.getUserEmail();
        listView = view.findViewById(R.id.my_book_list);
        bookList = new ArrayList<Book>();
        bookCollection = new BookCollection(bookList,listView,email,view.getContext());
        getBookQuery = new GetBookQuery(activity.getUserEmail(), bookCollection,view.getContext());
        setButtonListener((Button) view.findViewById(R.id.view_geo_button));
        setSelectListener();
        updateQuery = new UpdateQuery();
        updateQuery.emptyNotif(activity.getUserEmail(),"acceptedCount");
        activity.notifRefresh();
        return view;
    }
    private void setButtonListener(Button geoButton){
        geoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to view the geo location here
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getBookQuery.getMyBooks("accepted");
        activity.notifRefresh();
    }
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = bookCollection.getBook(position);
        });
    }
}
