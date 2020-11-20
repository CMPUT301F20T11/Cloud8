package com.example.booktracker.boundary;

import android.content.Context;
import android.widget.ListView;

import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public class BookCollection {
    //the goal of this class is to add books to adapter and notify changes
    // and just overall manage
    //the adapter
    //this needs to be loaded when the main screen is loaded
    private ArrayList<Book> bookList;
    private ListView listView;
    private BookAdapter adapter;
    private Context context;
    private String status;
    private String email;

    /**
     * When instantiated it will change the displayed books
     *
     * @param argBookList array adapter responsible for the view of a single
     *                    book in a list
     * @param parent      xml to bind the book data to
     * @param argContext  this context should always have list view
     * @param userEmail   email of the user currently logged in
     */
    public BookCollection(ArrayList<Book> argBookList, ListView parent,
                          String userEmail, Context argContext) {
        context = argContext;
        bookList = argBookList;
        email = userEmail;
        adapter = new BookAdapter(context, argBookList);
        status = "";
        listView = parent;
        //already has a list then it will be overwritten by this
    }

    /**
     * This will add the book to the database and the adapter
     *
     * @param newBook
     */
    public void addBook(Book newBook) {
        AddBookQuery query = new AddBookQuery(email);
        query.addBook(newBook);//this might modify the adapter
        adapter.notifyDataSetChanged();
    }

    /**
     * Get a book in the book adapter
     *
     * @param position
     * @return
     */
    public Book getBook(int position) {
        return adapter.getItem(position);
    }

    public String getStatus() {
        return status;
    }

    public void displayBooks() {
        listView.setAdapter(adapter);//bind ui to adapter,if list View ui
    }

    public void setBookList(ArrayList<Book> argBookList) {
        adapter = new BookAdapter(context, argBookList);
    }

    /**
     * delete a specific book from the adapter and the listView
     *
     * @param book
     */
    public void deleteBook(Book book) {
        adapter.remove(book);
        adapter.notifyDataSetChanged();
    }

    /**
     * delete all books in the adapter and all books in the listview
     */
    public void clearList() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }
}
