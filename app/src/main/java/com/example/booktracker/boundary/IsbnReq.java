package com.example.booktracker.boundary;

import android.os.AsyncTask;
import android.util.Log;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.User;
import com.example.booktracker.ui.AddBookActivity;
import com.example.booktracker.ui.EditBookActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class IsbnReq extends AsyncTask<String,String,String> {
    private boolean done = false;
    private String isbn;
    private ArrayList<Book> output;
    private AddBookActivity instance;
    public IsbnReq(String argIsbn, ArrayList<Book> argOutput, AddBookActivity argInstance){
        super();
        isbn = argIsbn;
        output = argOutput;
        instance = argInstance;
    }

    /**
     * This method will use getJson to make a HTTP request to GoogleBooks api and parse the result
     * @param strings
     * @return A string to indicate the success of the request
     */
    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject json = new JSONObject(getJson(isbn));
            JSONArray arr = json.getJSONArray("items");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject curObj = (JSONObject) arr.get(i);
                JSONObject obj2 = curObj.getJSONObject("volumeInfo");
                JSONArray authors = obj2.getJSONArray("authors");
                ArrayList<String> authorList = new ArrayList<String>();
                for (int j = 0; j < authors.length(); j++) {
                    authorList.add((String) authors.get(j));
                }
                output.add(new Book(authorList, obj2.getString("title"), isbn));
            }
            return "COMPLETED";
        }catch (Exception e){
            System.out.println(e);
        }
        return "NOT COMPLETED";
    }

    /**
     * If the request was successful then UI will be udated
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("COMPLETED")){
            instance.updateUI(output);
        }

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
            //make http request to google books api
            URL url = new URL(String.format(formatString,isbn.trim()));

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
            throw new RuntimeException(String.format("http request to get isbn %s failed e:%s ",isbn,e));
        }
    }
}
