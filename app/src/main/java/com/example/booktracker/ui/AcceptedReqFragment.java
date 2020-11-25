package com.example.booktracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.boundary.UpdateQuery;

public class AcceptedReqFragment extends Fragment {
    private UpdateQuery updateQuery;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accepted_req, container, false);

        HomeActivity activity = (HomeActivity) getActivity();
        updateQuery = new UpdateQuery();
        updateQuery.emptyNotif(activity.getUserEmail(),"acceptedCount");
        return view;
    }
}
