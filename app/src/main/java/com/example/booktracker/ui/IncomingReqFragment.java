package com.example.booktracker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public class IncomingReqFragment extends Fragment implements View.OnClickListener {
    ListView bookList;
    ArrayAdapter<Book> bookAdapter;
    ArrayList<Book> bookDataList;
    Book selected_book = null;
    int LAUNCH_GEO = 523;
    int LAUNCH_PERMISSIONS = 69;
    private boolean userGPS = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incoming_req, container, false);


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


    // know what book is referenced when view profile option selected
    @Override
    public void onClick(View v) {
        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                selected_book = bookDataList.get(position);
            }
        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
//                    startActivityForResult(new Intent(getActivity(), Permissions.class), LAUNCH_PERMISSIONS);
//                    Intent setGeo = new Intent(getActivity(), SetGeoActivity.class);
//                    setGeo.putExtra("userGPS", userGPS);
                    startActivityForResult(new Intent(getActivity(), SetGeoActivity.class), LAUNCH_GEO);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LAUNCH_GEO) {
            if(resultCode == Activity.RESULT_OK){
                Double lat = data.getDoubleExtra("pickupLat", -1);
                Double lon = data.getDoubleExtra("pickupLng", -1);
                // attach to accepted request

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //no location attached to request
            }
        }
        if(requestCode == LAUNCH_PERMISSIONS){
            userGPS = data.getBooleanExtra("userGPS", false);
        }
    }



}
