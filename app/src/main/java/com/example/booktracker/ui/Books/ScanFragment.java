package com.example.booktracker.ui.Books;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booktracker.R;
import com.example.booktracker.ui.Activities.MainActivity;
import com.example.booktracker.ui.Activities.ScanActivity;

/**
 * Placeholder fragment to navigate to Scan activity from Nav drawer
 */
public class ScanFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        startActivity(new Intent(view.getContext(), ScanActivity.class));
        return view;
    }
}
