package com.example.booktracker.entities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.booktracker.boundary.UserQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Request class for handling notifications and database changes
 * involving other users
 * @author Edlee Ducay
 */
public class Request extends Notification {

    private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private Book book;
    private Context context;
    private RequestQueue mQueue;
    private String fromUsername;
    private User from;

    /**
     * Constructor for the Request class
     * @param from
     * @param to
     * @param argBook
     * @param argContext
     */
    public Request(String from, String to, Book argBook, Context argContext){
        super(from, to);
        book = argBook;
        context = argContext;
        mQueue = Volley.newRequestQueue(argContext);
        getSender();
    }

    /**
     * Main function for sending requests
     * Add's the book into the 'requestedBooks' collection for fromUser,
     * Add's the request into the 'incomingRequests' collection for toUser,
     * and sends the push notification
     *
     */
    public void sendRequest() {
        addToRequestedBooks();
        addToIncomingRequests();
        sendPushNotification();

    }

    /**
     * Sends a push notification upstream to the FCM
     */
    private void sendPushNotification() {
        String title = "Book Request";
        String body = "Your book '" + book.getTitle() + "' has been requested";

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();
        try {
            notificationBody.put("title", title);
            notificationBody.put("message", body);

            notification.put("to", "eE4jguDKTRex-6Fa5MGgIq:APA91bGnrml422Tu_WSe5tIHRc62FX4Jc6Jco1TP57tMSWqFs-BqwBLRT976XebiA5YvcC9VCBX5PxbS-SwMxqX8Nt2BmGRqSEEjsTXAlpEoTmeBwt61ALffVAQW-N_oorw0O7pv7i4L");
            notification.put("data", notificationBody);
        } catch (JSONException e) {
            Log.e("REQUEST TAG", "onCreate: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("REQUEST TAG", "onResponse: " +  response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("REQUEST TAG", "onErrorResponse: Request error");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("authorization", serverKey);
                map.put("Content-Type", contentType);
                return map;
            }
        };

        mQueue.add(jsonObjectRequest);
        //MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void addToRequestedBooks() {
        DocumentReference bookReference = db.collection("books").document(book.getIsbn());
        HashMap<String, Object> userBook = new HashMap<String, Object>();
        userBook.put("bookReference", bookReference);
        userDoc.collection("requested")
                .document(book.getIsbn())
                .set(userBook);
    }

    /**
     * Add's a request to the book in the original owner's 'myBooks' collection
     */
    public void addToIncomingRequests() {
        CollectionReference bookCollection = db.collection("books");
        DocumentReference bookReference = bookCollection.document(book.getIsbn());
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("from", userDoc);
        data.put("bookReference", bookReference);

        DocumentReference toDoc = db.collection("users").document(toEmail);
        toDoc.collection("incomingRequests")
                .document(book.getIsbn() + "-" + fromEmail)
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Add Request to Book", "Request added successfully");
                Toast.makeText(context, "Book requested", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Add Request to Book", "Request did not succeed");
                Toast.makeText(context, "Book request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getSender() {
        UserQuery userQuery = new UserQuery(fromEmail, context);
        from = userQuery.getUserObject();
    }


    /**
     * TODO Set up getData but for users (or make user Query)
     * @param newBook
     * @return
     */
    private HashMap<String,Object> getData(Book newBook){
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("status",newBook.getStatus());
        data.put("isbn",newBook.getIsbn());
        data.put("title",newBook.getTitle());
        data.put("owner",newBook.getOwner());
        data.put("borrower",newBook.getBorrower());
        data.put("description",newBook.getDescription());
        data.put("author",newBook.getAuthor());
        data.put("image_uri", newBook.getUri());
        data.put("local_image_uri", newBook.getLocalUri());
        return data;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }


    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }






}
