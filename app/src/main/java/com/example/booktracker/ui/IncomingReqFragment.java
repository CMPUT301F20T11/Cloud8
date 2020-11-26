package com.example.booktracker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.RequestCollection;
import com.example.booktracker.boundary.RequestQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class IncomingReqFragment extends Fragment implements View.OnClickListener {
    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    int LAUNCH_GEO = 523;
    int LAUNCH_PERMISSIONS = 69;

    private boolean userGPS = false;
    private String userEmail, userSelected, lastStatus;
    private ListView listView;
    private RequestQuery requestQuery;
    private RequestCollection requestCollection;
    private View view;
    private Request selected_request = null;
    private DocumentSnapshot userDoc;
    private DeleteBookQuery delQuery;

    // implements View.OnClickListener
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_incoming_req, container, false);

        listView = view.findViewById(R.id.incoming_requests_list);
        setHasOptionsMenu(true);
        HomeActivity activity = (HomeActivity) getActivity();
        userEmail = Objects.requireNonNull(activity).getUserEmail();
        requestCollection = new RequestCollection(new ArrayList<>(), listView, userEmail, view.getContext());
        requestQuery = new RequestQuery(userEmail, requestCollection, view.getContext());
        lastStatus = "";
        delQuery = new DeleteBookQuery();
        setSelectListener();

        /*
          Accepting a book request prompts the option to attach a geo
          location where book can be picked up
         */
        Button acceptReqBtn = view.findViewById(R.id.accept_req_button);
        acceptReqBtn.setOnClickListener(view -> {
            AlertDialog.Builder geoPrompt =
                    new AlertDialog.Builder(view.getContext());
            geoPrompt.setMessage("Set location for book pickup?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });


        Button declineReqBtn = view.findViewById(R.id.decline_req_button);
        declineReqBtn.setOnClickListener(view -> {
            String isbn = selected_request.getBook().getIsbn();
            delQuery.deleteBookRequested(isbn,
                    selected_request.getFromEmail());
            delQuery.deleteBookIncoming(isbn,
                    selected_request.getFromEmail(),
                    selected_request.getToEmail());
            requestQuery.getRequests();
        });

        return view;
    }

    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                IncomingReqFragment.this.startActivityForResult(new Intent(IncomingReqFragment.this.getActivity(), SetGeoActivity.class), LAUNCH_GEO);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                // .. request accepted .. don't attach location
                break;
        }
    };

    /**
     * Set the callback function to keep track of the selected books
     */
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_request = requestCollection.getRequest(position);
            userSelected = selected_request.getFromEmail();
            if (userSelected != null) {
                IncomingReqFragment.this.getUserDoc(userSelected);
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

    /**
     * Refresh the listView when the user return the the HomeActivity in case an update to
     * the BookCollection was made
     */
    @Override
    public void onResume() {
        //this is needed to refresh the list of books displayed when the user goes back to the
        //home activity
        super.onResume();
        requestQuery.getRequests();

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
                return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCH_GEO) {
            if(resultCode == Activity.RESULT_OK){
                Double lat = data.getDoubleExtra("pickupLat", -1);
                Double lon = data.getDoubleExtra("pickupLng", -1);
                // attach to accepted request

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //set location cancelled
                // .. request accepted .. don't attach location
            }
        }
        if(requestCode == LAUNCH_PERMISSIONS){
            userGPS = data.getBooleanExtra("userGPS", false);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
