package com.example.booktracker.boundary;

import android.content.Context;
import android.widget.ListView;

import com.example.booktracker.entities.Request;

import java.util.ArrayList;


public class RequestCollection {
    private ArrayList<Request> requestList;
    private ListView listView;
    private RequestAdapter adapter;
    private Context context;
    private String status;
    private String email;

    /**
     * Main constructor for the RequestCollection class
     * @param argRequestList
     * @param parent
     * @param userEmail
     * @param argContext
     */
    public RequestCollection(ArrayList<Request> argRequestList, ListView parent,
                             String userEmail, Context argContext) {
        context = argContext;
        requestList = argRequestList;
        email = userEmail;
        adapter = new RequestAdapter(context, argRequestList);
        status = "";
        listView = parent;
    }

    /**
     * Gets the request from the adapter at position
     * @param position
     * @return Request
     */
    public Request getRequest(int position){
        return adapter.getItem(position);
    }

    /**
     * Sets the adapter using the inputted request array
     * @param argRequestList
     */
    public void setRequestList(ArrayList<Request> argRequestList) {
        adapter = new RequestAdapter(context, argRequestList);
    }

    public void deleteRequest(Request toRemove){
        adapter.remove(toRemove);
        adapter.notifyDataSetChanged();
    }

    /**
     * Binds the ui to the adapter
     */
    public void displayRequests() {
        listView.setAdapter(adapter);
    }

    /**
     * Clears all the requests in the adapter and the listview
     */
    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
