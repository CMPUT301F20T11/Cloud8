package com.example.booktracker.entities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.telecom.Call;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.control.Callback;
import com.example.booktracker.control.Email;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class NotificationCircle implements Callback {
    private GetBookQuery query;
    private int notifs = 0;
    private NotifCount count;
    private String email;
    private TextView view;
    public NotificationCircle(String argEmail,TextView argView){
        query = new GetBookQuery();
        count = new NotifCount();
        email = argEmail;
        view = argView;
    }
    public void checkNotification(){
        query.getNotif(this,count,(email));
    }
    private void raiseNotif(){
        view.setText(Long.toString(count.getTotal()));
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }
    private void clearNotif(){
        view.setVisibility(View.GONE);
    }
    @Override
    public void executeCallback(){
        if (count.getAccepted() > 0 || count.getIncoming() > 0){
            raiseNotif();
        }
        if (count.getTotal() == 0){
            clearNotif();
        }
    }
}
