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
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     *
     */
    public void sendRequest() {
        addToRequestedBooks();
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

    /**
     * Add's the request to 'requested' collection in the user document
     * Add's the request into the 'incomingRequests' collection for toUser,
     * and sends the push notification
     */
    public void addToRequestedBooks() {
        DocumentReference bookReference = db.collection("books").document(book.getIsbn());
        HashMap<String, Object> userBook = new HashMap<>();
        userBook.put("bookReference", bookReference);
        Task accepted = userDoc.collection("accepted").document(book.getIsbn()).get();
        Task borrowed = userDoc.collection("borrowed").document(book.getIsbn()).get();
        Task requested = userDoc.collection("requested").document(book.getIsbn()).get();
        Tasks.whenAllComplete(accepted,borrowed,requested)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> task) {
                        ArrayList<Task<?>> res = (ArrayList<Task<?>>) task.getResult();
                        DocumentSnapshot res1 = (DocumentSnapshot) res.get(0).getResult(); //this is accepted
                        DocumentSnapshot res2 = (DocumentSnapshot) res.get(1).getResult(); //this is borrowed
                        DocumentSnapshot res3 = (DocumentSnapshot) res.get(2).getResult(); //this is requested
                        if (res1.exists() || res2.exists() || res3.exists()){
                            Toast.makeText(context, "Book has already been requested", Toast.LENGTH_SHORT).show();
                        }else {
                            userDoc.collection("requested")
                                    .document(book.getIsbn())
                                    .set(userBook);
                            addToIncomingRequests();
                            sendPushNotification();
                        }
                    }
                });

    }

    /**
     * Add's a request to the book in the original owner's 'myBooks' collection
     */
    public void addToIncomingRequests() {
        CollectionReference bookCollection = db.collection("books");
        DocumentReference bookReference = bookCollection.document(book.getIsbn());
        HashMap<String,Object> data = new HashMap<>();
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

    /**
     * Gets the current user object from the DB
     */
    public void getSender() {
        UserQuery userQuery = new UserQuery(fromEmail, context);
        from = userQuery.getUserObject();
    }

    /**
     * Gets the book that has been requested by the user
     * @return book object
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book that has been requested by the user
     * @param book
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the username of the user who requested the book
     * @return fromUsername object
     */
    public String getFromUsername() {
        return fromUsername;
    }

    /**
     * Sets the username of the user who requested the book
     * @param fromUsername
     */
    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }




}
