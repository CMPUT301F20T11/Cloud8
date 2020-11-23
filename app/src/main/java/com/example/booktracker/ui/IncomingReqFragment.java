package com.example.booktracker.ui;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.BookCollection;
import com.example.booktracker.boundary.RequestCollection;
import com.example.booktracker.boundary.RequestQuery;
import com.example.booktracker.boundary.getBookQuery;
import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static androidx.fragment.app.DialogFragment.STYLE_NO_TITLE;

public class IncomingReqFragment extends Fragment  {
    private String userEmail, userSelected, lastStatus;
    private ListView listView;
    private RequestQuery requestQuery;
    private RequestCollection requestCollection;
    private View view;
    private Request selected_request = null;
    private DocumentSnapshot userDoc;

    //implements View.OnClickListener
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_incoming_req, container, false);

        listView = view.findViewById(R.id.incoming_requests_list);
        HomeActivity activity = (HomeActivity) getActivity();
        userEmail = activity.getUserEmail();
        requestCollection = new RequestCollection(new ArrayList<Request>(), listView, userEmail, view.getContext());
        requestQuery = new RequestQuery(userEmail, requestCollection, view.getContext());
        requestQuery.getRequests();
        lastStatus = "";

        setSelectListener();

        /**
         * Accepting a book request prompts the option to attach a geo location where book can be picked up
         */
        Button acceptReqBtn = (Button) view.findViewById(R.id.accept_req_button);
        acceptReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder geoPrompt = new AlertDialog.Builder(view.getContext());
                geoPrompt.setMessage("Set location for book pickup?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


        Button declineReqBtn = (Button) view.findViewById(R.id.decline_req_button);
        declineReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete from request list
                // update requester's requested books
            }
        });

        return view;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    startActivity(new Intent(getActivity(), SetGeoActivity.class));
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
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

}
