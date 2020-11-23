package com.example.booktracker.boundary;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Serves as a request queue for the notification requests
 * Retrieved from: https://blog.usejournal.com/send-device-to-device-push-notifications-without-server-side-code-238611c143
 * Mendhie Emmanuel
 * 11/18/2020
 */
public class MySingleton {
    private static MySingleton instance;
    private RequestQueue requestQueue;
    private Context context;

    private MySingleton(Context argContext) {
        context = argContext;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingleton getInstance(Context argContext) {
        if (instance == null) {
            instance = new MySingleton(argContext);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

}
