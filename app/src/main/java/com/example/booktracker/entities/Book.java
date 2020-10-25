package com.example.booktracker.entities;

import java.util.ArrayList;
import java.util.List;
/**
 * Book entity is responsible for encapsulating book related data.
 * @author Ivan Penales <ipenales@ualberta.ca>
 * @return Book
 */
public class Book {
    private List<String> author;
    private String title;
    private int isbn;
    public Book(List<String> argAuthor,String argTitle,int argIsbn){
        author = argAuthor;
        title = argTitle;
        isbn = argIsbn;
    }
    public Book(List<String> argAuthor,String argTitle){
        author = argAuthor;
        title = argTitle;
    }
    public String getTitle(){
        return title;
    }
    public List<String> getAuthors(){
        return author;
    }
}
