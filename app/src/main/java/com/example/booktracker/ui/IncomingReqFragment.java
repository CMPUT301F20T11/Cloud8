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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.DeleteBookQuery;
import com.example.booktracker.boundary.RequestCollection;
import com.example.booktracker.boundary.RequestQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.QueryOutputCallback;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.QueryOutput;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class IncomingReqFragment extends Fragment implements View.OnClickListener, QueryOutputCallback {
    Book selected_book = null;
    int LAUNCH_GEO = 523;

    private boolean userGPS = false;
    private String userEmail, userSelected, lastStatus;
    private ListView listView;
    private RequestQuery requestQuery;
    private RequestCollection requestCollection;
    private View view;
    private Request selected_request = null;
    private DocumentSnapshot userDoc;
    private DeleteBookQuery delQuery;
    private IncomingReqFragment instance = this;
    private QueryOutput queryOutput = new QueryOutput();
    private UpdateQuery query = new UpdateQuery();

    // implements View.OnClickListener
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_incoming_req, container, false);

        listView = view.findViewById(R.id.incoming_requests_list);
        setHasOptionsMenu(true);
        HomeActivity activity = (HomeActivity) getActivity();
        userEmail = activity.getUserEmail();
        requestCollection = new RequestCollection(new ArrayList<>(), listView, userEmail, view.getContext());
        requestQuery = new RequestQuery(userEmail, requestCollection, view.getContext());
        lastStatus = "";
        delQuery = new DeleteBookQuery();
        setSelectListener();
        query.emptyNotif(activity.getUserEmail(),"incomingCount");
        activity.notifRefresh();

        /*
          Accepting a book request prompts the option to attach a geo
          location where book can be picked up
         */
        Button acceptReqBtn = (Button) view.findViewById(R.id.accept_req_button);
        acceptReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_request != null){
                    AlertDialog.Builder geoPrompt = new AlertDialog.Builder(view.getContext());
                    geoPrompt.setMessage("Set location for book pickup?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else {
                    Toast.makeText(getContext(), "No request selected", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button declineReqBtn = (Button) view.findViewById(R.id.decline_req_button);
        declineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_request != null) {
                    String isbn = selected_request.getBook().getIsbn();
                    delQuery.deleteBookRequested(isbn,selected_request.getFromEmail());
                    delQuery.deleteBookIncoming(isbn,selected_request.getFromEmail(),selected_request.getToEmail());
                    requestQuery.getRequests("incomingRequests");
                }
            }
        });

        return view;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    startActivityForResult(new Intent(getActivity(), SetGeoActivity.class), LAUNCH_GEO);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    // .. request accepted .. don't attach location
                    HashMap<String, Object> dataRes = new HashMap<>();
                    dataRes.put("status","unavailable");
                    selected_book = selected_request.getBook();
                    query.updateBook(selected_book,instance,dataRes,queryOutput);
                    query.changeBookStatus(selected_book.getIsbn() + "-" + selected_request.getFromEmail(), selected_book.getIsbn(),"accepted", selected_request.getToEmail(),"incomingRequests");
                    query.changeBookStatus(selected_book.getIsbn(), selected_book.getIsbn(),"accepted", selected_request.getFromEmail(),"requested");
                    requestCollection.deleteRequest(selected_request);
                    query.incrementNotif(selected_request.getFromEmail(),"acceptedCount");
                    break;
            }
        }
    };

    /**
     * Set the callback function to keep track of the selected books
     */
    private void setSelectListener() {
        listView.setOnItemClickListener((adapter, v, position, id) -> {
            selected_request = requestCollection.getRequest(position);
            userSelected = selected_request.getFromUsername();
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

    /**
     * Refresh the listView when the user return the the HomeActivity in case an update to
     * the BookCollection was made
     */
    @Override
    public void onResume() {
        //this is needed to refresh the list of books displayed when the user goes back to the
        //home activity
        super.onResume();
        requestQuery.getRequests("incomingRequests");

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

    @Override
    public void displayQueryResult(String result){
        Toast.makeText(instance.getContext(),queryOutput.getOutput(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCH_GEO) {
            if (resultCode == Activity.RESULT_OK) {
                Double lat = data.getDoubleExtra("pickupLat", -1);
                Double lon = data.getDoubleExtra("pickupLng", -1);
                //need to change the status of the book to unavailable

                HashMap<String, Object> dataRes = new HashMap<>();
                dataRes.put("lat", lat);
                dataRes.put("lon", lon);
                selected_book = selected_request.getBook();
                query.updateBook(selected_book, instance, dataRes, queryOutput);
                query.changeBookStatus(selected_book.getIsbn() + "-" + selected_request.getFromEmail(), selected_book.getIsbn(),"accepted", selected_request.getToEmail(),"incomingRequests");
                query.changeBookStatus(selected_book.getIsbn(), selected_book.getIsbn(),"accepted", selected_request.getFromEmail(),"requested");
                requestCollection.deleteRequest(selected_request);
                query.incrementNotif(selected_request.getFromEmail(),"acceptedCount");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //set location cancelled .. do nothing
            }
        }
    }

    @Override
    public void onClick(View view) {}
}
