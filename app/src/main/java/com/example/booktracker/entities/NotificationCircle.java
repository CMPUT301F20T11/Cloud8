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
    private TextView incoming;
    private TextView accepted;

    public NotificationCircle(String argEmail, TextView argView,TextView incoming,TextView accepted){
        getBookQuery = new GetBookQuery();
        count = new NotifCount();
        email = argEmail;
        view = argView;
        updateQuery = new UpdateQuery();
        this.incoming = incoming;
        this.accepted = accepted;
    }

    public void checkNotification() {
        getBookQuery.getNotif(this,count,(email));
    }
    private void raiseIncoming(){
        incoming.setVisibility(View.VISIBLE);
        incoming.setText(Long.toString(count.getIncoming()));
    }
    private void raiseAccepted(){
        accepted.setVisibility(View.VISIBLE);
        accepted.setText(Long.toString(count.getAccepted()));
    }
    private void raiseTotal(){
        view.setVisibility(View.VISIBLE);
        view.setText(Long.toString(count.getTotal()));
    }

    private void clearNotif(){
        view.setVisibility(View.GONE);
        incoming.setVisibility(View.GONE);
        accepted.setVisibility(View.GONE);
    }

    @Override
    public void executeCallback() {
        if (count.getTotal() == 0) {
            clearNotif();
        } else {
            raiseTotal();
        }

        if (count.getAccepted() == 0) {
            //if count accepted is zero then counter could have never been initialized in db
            updateQuery.emptyNotif(email,"acceptedCount");
            accepted.setVisibility(View.GONE);
        } else {
            raiseAccepted();
        }

        if (count.getIncoming() == 0) {
            //if count incoming is zero the counter could have never been initialized in db
            updateQuery.emptyNotif(email,"incomingCount");
            incoming.setVisibility(View.GONE);
        } else {
            raiseIncoming();
        }
    }
}
