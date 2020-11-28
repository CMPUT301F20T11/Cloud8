package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.BundleCompat;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class AcceptedReqFragment extends Fragment implements Callback {
    private UpdateQuery updateQuery;
    private ListView listView;
    private ArrayList<Book> bookList;
    private GetBookQuery getBookQuery;
    private BookCollection bookCollection;
    private View view;
    private Book selected_book = null;
    private String email;
    private HomeActivity activity;
    private AcceptedReqFragment instance = this;
    private Button viewButton;
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
        viewButton = view.findViewById(R.id.view_button_accepted);
        setViewListener();
        updateQuery = new UpdateQuery();
        updateQuery.emptyNotif(activity.getUserEmail(),"acceptedCount");
        activity.notifRefresh();
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
    private void setButtonListener(Button geoButton){
        geoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_book != null){
                    getBookQuery.getLatLong(instance,selected_book);
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        getBookQuery.getBooksCategory("accepted");
        activity.notifRefresh();
    }
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_book = bookCollection.getBook(position);
        });
    }
    @Override
    public void executeCallback(){
        Double lat = selected_book.getLat();
        Double lon = selected_book.getLon();
        if(lat == null || lon == null){
            Toast.makeText(getContext(), "No location attached to location", Toast.LENGTH_SHORT).show();
        }
        else{
            Bundle pickupLoc = new Bundle();
            pickupLoc.putDouble("pickupLat", lat);
            pickupLoc.putDouble("pickupLng", lon);
            Log.d(TAG, "acceptedReq lat: " + lat);
            Log.d(TAG, "acceptedReq lon: " + lon);
            Intent viewGeo = new Intent(getContext(), ViewGeoActivity.class);
            viewGeo.putExtras(pickupLoc);
            startActivity(viewGeo);
        }
    }
}
