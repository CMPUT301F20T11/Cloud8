package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.entities.Book;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class AcceptedReqFragment extends Fragment implements Callback {
    private UpdateQuery updateQuery;
    private ListView listView;
    private ArrayList<Book> bookList;
    private GetBookQuery getBookQuery;
    private BookCollection bookCollection;
    private DeleteBookQuery deleteBookQuery;
    private View view;
    private Book selected_book = null;
    private String email, userSelected;
    private HomeActivity activity;
    private AcceptedReqFragment instance = this;
    private Button viewButton;
    private DocumentSnapshot userDoc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_accepted_req, container, false);
        setHasOptionsMenu(true);
        activity = (HomeActivity) getActivity();
        email = activity.getUserEmail();
        listView = view.findViewById(R.id.my_book_list);
        bookList = new ArrayList<>();
        bookCollection = new BookCollection(bookList, listView, email, view.getContext());
        getBookQuery = new GetBookQuery(activity.getUserEmail(), bookCollection,view.getContext());
        deleteBookQuery = new DeleteBookQuery();
        setButtonListener(view.findViewById(R.id.view_geo_button));
        setSelectListener();
        setCancelListener();
        viewButton = view.findViewById(R.id.view_button_accepted);
        setViewListener();
        updateQuery = new UpdateQuery();
        updateQuery.emptyNotif(activity.getUserEmail(),"acceptedCount");
        activity.notifRefresh();

        return view;
    }
    private void setCancelListener(){
        Button cancelBtn = view.findViewById(R.id.cancel_accepted);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteBookQuery.deleteBookAccepted(selected_book.getIsbn(),selected_book.getOwnerEmail());
                deleteBookQuery.deletePotentialBorrower(selected_book);
                bookCollection.deleteBook(selected_book);
            }
        });
    }
    private void setViewListener() {
        viewButton.setOnClickListener(view -> {
            if (selected_book != null) {
                Intent intent = new Intent(view.getContext(),ViewBookActivity.class);
                intent.putExtra(EXTRA_MESSAGE, selected_book.getIsbn());
                startActivity(intent);
            }
        });
    }

    private void setButtonListener(Button geoButton){
        geoButton.setOnClickListener(v -> {
            if (selected_book != null) {
                getBookQuery.getLatLong(instance, selected_book);
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
            userSelected = selected_book.getOwnerEmail();
            if (userSelected != null) {
                getUserDoc(userSelected);
            }
        });
    }

    private Boolean getUserDoc(String owner) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (owner == null) {
            return false;
        } else {
            DocumentReference docRef = db.collection("users").document(owner);
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        userDoc = doc;
                    }
                }
            });
        }
        return true;
    }

    @Override
    public void executeCallback(){
        Double lat = selected_book.getLat();
        Double lon = selected_book.getLon();
        if (lat == null || lon == null) {
            Toast.makeText(getContext(), "No location attached to location", Toast.LENGTH_SHORT).show();
        } else {
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu,
                                    @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_user) {
            if (getUserDoc(userSelected)) {
                showUserDialog(userDoc);
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUserDialog(DocumentSnapshot userDoc) {
        String username = userDoc.getString("username");
        String email = userDoc.getString("email");
        String phone = userDoc.getString("phone");
        ViewUserDialog userDialog = ViewUserDialog.newInstance(username, email, phone);
        userDialog.setStyle(STYLE_NO_TITLE, 0);
        userDialog.show(getParentFragmentManager(), "VIEW USER");
    }
}
