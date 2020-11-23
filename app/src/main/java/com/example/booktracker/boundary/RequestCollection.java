package com.example.booktracker.boundary;

import android.content.Context;
import android.widget.ListView;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.Request;

import java.util.ArrayList;

public class RequestCollection {
    private ArrayList<Request> requestList;
    private ListView listView;
    private RequestAdapter adapter;
    private Context context;
    private String status;
    private String email;

    public RequestCollection(ArrayList<Request> argRequestList, ListView parent,
                             String userEmail, Context argContext) {
        context = argContext;
        requestList = argRequestList;
        email = userEmail;
        adapter = new RequestAdapter(context, argRequestList);
        status = "";
        listView = parent;
    }

    public Request getRequest(int position){
        return adapter.getItem(position);
    }

    public void setRequestList(ArrayList<Request> argRequestList) {
        adapter = new RequestAdapter(context, argRequestList);
    }

    public void displayRequests() {
        listView.setAdapter(adapter); //bind ui to adapter,if list View ui
    }

    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
