package com.example.booktracker.entities;


import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private List<String> author;
    private String title;
    private int isbn;
    private String description;
    private String owner;
    private String status;

    /**
     * Constructor for books with a description
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     * @param argDesc
     */
    public Book(List<String>argAuthor, String argTitle, int argIsbn, String argDesc) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.description = argDesc;
    }

    /**
     * Constructor for books without a description
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     */
    public Book(List<String>argAuthor, String argTitle, int argIsbn) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor(List<String> author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
