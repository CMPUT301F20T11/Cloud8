package com.example.booktracker.boundary;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.BookCollection;
import com.example.booktracker.entities.User;
import com.example.booktracker.ui.AddBookActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBookQuery extends BookQuery{
    private BookCollection bookList;
    private String queryOutput = "";
    /**
     * This will call its parent constructore from BookQuery
     * @param userEmail
     */

    public AddBookQuery(String userEmail){
        super(userEmail);
    }
    /**
     * getJson will perform HTTP GET request to Google Books api
     * @author Ivan Penales <ipenales@ualberta.ca>
     * @param isbn will contain the isbn number of a book
     * @return json in String format
     */
    private String getJson(String isbn) throws RuntimeException{
        String formatString = "https://www.googleapis.com/books/v1/volumes?q=isbn:%s";
        StringBuffer output = null;
        try{
            //make http request to googles books api
            URL url = new URL(String.format(formatString,isbn));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type","application/json");
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            output = new StringBuffer();
            while ((inputLine = input.readLine()) != null) {
                output.append(inputLine);
            }
            input.close();
            connection.disconnect();
            return (String) output.toString();
        }catch(Exception e){
            throw new RuntimeException(String.format("http request to get isbn %s failed",isbn));
        }
    }
    /**
     * isbnQuery will return all Books that match the given isbn
     * https://www.baeldung.com/java-http-request
     * @author Ivan Penales <ipenales@ualberta.ca>
     * @param isbn will contain the isbn number of a book
     * @return ArrayList<Book>
     */
    public ArrayList<Book> isbnQuery(String isbn) throws RuntimeException {
        ArrayList<Book> output = new ArrayList<Book>();
        try{
            //parse json and extract author and book title
            JSONObject json = new JSONObject(getJson(isbn));
            JSONArray arr = json.getJSONArray("items");
            User user;
            for (int i = 0;  i < arr.length(); i++){

                JSONObject curObj = (JSONObject) arr.get(i);
                JSONObject obj2 = curObj.getJSONObject("volumeInfo");
                JSONArray authors = obj2.getJSONArray("authors");
                ArrayList<String> authorList = new ArrayList<String>();
                for (int j = 0; j < authors.length();j++){
                    authorList.add((String) authors.get(j));
                }
                output.add(new Book(authorList,obj2.getString("title"),isbn));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     * This will add the book to the adapter and the database if its not already there
     * @author Ivan Penales
     * @param newBook book to be added
     */
    public String addBook(Book newBook){

        addToDb(newBook);// add book to database
        return queryOutput;
    }

    /**
     * @author Ivan Penales
     * @param newBook book to extract data from
     * @return A hashmap matching the key value pairs of a book in firestore
     */
    private HashMap<String,Object> getData(Book newBook){
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("title",newBook.getTitle());
        data.put("owner",newBook.getOwner());
        data.put("borrower",newBook.getBorrower());
        data.put("description",newBook.getDescription());
        data.put("author",newBook.getAuthor());
        if (newBook.getUri() != null){
            data.put("image_uri", newBook.getUri().toString());
        }
        return data;
    }

    /**
     * This will add a book to firestore
     * @author Ivan Penales
     * @param newBook book to add to firestore
     */
    private void addToDb(Book newBook){

        HashMap<String,Object> data = getData(newBook);
        if (newBook.getStatus() != ""){
            userDoc.collection(newBook.getStatus())
                    .document(newBook.getIsbn())
                    .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   queryOutput =  "Added book succesfully";
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    queryOutput = "couldn't add book";
                }
            });
        }
        //book is always added to myBook list regardless of its status
        userDoc.collection("myBooks")
                .document(newBook.getIsbn())
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                queryOutput =  "Added book succesfully";
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                queryOutput = "couldnt add book";
            }
        });
    }

}