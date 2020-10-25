package com.example.booktracker.boundary;

import com.example.booktracker.entities.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class addBookQuery {
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

            for (int i = 0;  i < arr.length(); i++){

                JSONObject curObj = (JSONObject) arr.get(i);
                JSONObject obj2 = curObj.getJSONObject("volumeInfo");
                JSONArray authors = obj2.getJSONArray("authors");
                ArrayList<String> authorList = new ArrayList<String>();
                for (int j = 0; j < authors.length();j++){
                    authorList.add((String) authors.get(j));
                }
                output.add(new Book(authorList,obj2.getString("title")));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }
}