package com.example.booktracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.ui.ScanActivity;

/**
 * Placeholder fragment to navigate to Scan activity from Nav drawer
 */
public class ScanFragment extends Fragment {
    private static int SCAN_RQ = 69;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        startActivityForResult(new Intent(view.getContext(), ScanActivity.class),SCAN_RQ);
        return view;
    }
    public void displayDescription(){
        //description shoud be displayed when the view book displays
    }
}
