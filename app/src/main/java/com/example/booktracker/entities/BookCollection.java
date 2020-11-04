package com.example.booktracker.entities;

import android.content.Context;
import android.widget.ListView;

import com.example.booktracker.boundary.AddBookQuery;
import com.example.booktracker.boundary.BookAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
    //the goal of this class is to add books to adapter and notify changes and just overall manage
    //the adapter
    //this needs to be loaded when the main screen is loaded
    private ArrayList<Book> bookList;
    private ListView listView;
    private BookAdapter adapter;
    private String status;
    private String email;

    /**
     * When instatiated it will change the displayed books
     * @author Ivan Penales
     * @param argBookList array adapter responsible for the view of a single book in a list
     * @param parent xml to bind the book data to
     * @param context
     * @param userEmail
     */
    public BookCollection(ArrayList<Book> argBookList, ListView parent, String userEmail, Context context){
        bookList = argBookList;
        email = userEmail;
        adapter = new BookAdapter(context,argBookList);
        status = "";
        listView = parent;
        parent.setAdapter(adapter);//bind ui to adapter,if list View ui
        System.out.println("adapter has been set");
        //already has a list then it will be overwritten by this
    }
    /**
     * This will add the book to the database and the adapter
     * @author Ivan Penales
     * @param newBook
     */
    public void addBook(Book newBook){
        AddBookQuery query = new AddBookQuery(email);
        query.addBook(newBook);//this might modify the adapter
        adapter.notifyDataSetChanged();
    }
    public String getStatus() {
        return status;
    }
}
