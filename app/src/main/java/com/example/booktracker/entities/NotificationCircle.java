package com.example.booktracker.entities;

import android.view.View;
import android.widget.TextView;

import com.example.booktracker.boundary.GetBookQuery;
import com.example.booktracker.boundary.UpdateQuery;
import com.example.booktracker.control.Callback;

public class NotificationCircle implements Callback {
    private GetBookQuery getBookQuery;
    private UpdateQuery updateQuery;
    private NotifCount count;
    private String email;
    private TextView view;
    public NotificationCircle(String argEmail,TextView argView){
        getBookQuery = new GetBookQuery();
        count = new NotifCount();
        email = argEmail;
        view = argView;
        updateQuery = new UpdateQuery();
    }
    public void checkNotification(){
        getBookQuery.getNotif(this,count,(email));
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
        if (count.getAccepted() == 0){
            //if count accepted is zero then counter could have never been initalized in db
            updateQuery.emptyNotif(email,"acceptedCount");
        }
        if (count.getIncoming() == 0){
            //if count incoming is zero the counter could have never been initailized in db
            updateQuery.emptyNotif(email,"incomingCount");
        }
    }
}
