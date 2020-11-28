package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;

/**
 * Placeholder fragment to navigate to Scan activity from Nav drawer
 */
public class ScanFragment extends Fragment implements View.OnClickListener {
    private Button scanButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        setHasOptionsMenu(true);
        scanButton = view.findViewById(R.id.scanButton);
        scanButton.setOnClickListener(this);

        return view;
    }
    public void displayDescription() {
        //description should be displayed when the view book displays
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getContext(), ScanActivity.class));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.action_view_user);
        if (item != null) {
            item.setVisible(false);
        }
    }
}
