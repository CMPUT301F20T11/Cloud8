package com.example.booktracker.entities;


import android.net.Uri;

import java.io.Serializable;
import java.util.List;

/**
 * Base class for the book entity
 * @author Edlee Ducay
 */
public class Book implements Serializable {
    private List<String> author;
    private String title;
    private String isbn;
    private String description;
    private String uri;
    private String localUri;
    //=======These will be emails=========
    private String owner;
    private String borrower;
    //======================
    private String status;

    /**
     * contructor for initializing an empty book
     */
    public Book(){
        this.title = "";
        this.isbn = "";
        this.description = "";
        this.owner = "";
        this.status = "";
        this.borrower = null;
    }
    /**
     * Constructor for books with a description
     * @param argOwner
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     * @param argDesc
     */
    public Book(String argOwner, List<String>argAuthor, String argTitle, String argIsbn, String argDesc) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.description = argDesc;
        this.owner = argOwner;
        this.status = "available";
        this.borrower = null;

    }

    /**
     * Constructor for books without a description
     * @param argOwner
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     */
    public Book(String argOwner, List<String>argAuthor, String argTitle, String argIsbn) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.owner = argOwner;
        this.status = "";
        this.borrower = null;
    }
    /**
     * Constructor for books without a description without owner
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     */
    public Book( List<String>argAuthor, String argTitle, String argIsbn) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.status = "";
        this.borrower = null;
    }
    /**
     * Gets the book's author(s)
     * @return List of author(s)
     */
    public List<String> getAuthor() {
        return author;
    }

    /**
     * Sets the book's author(s)
     * @param author
     */
    public void setAuthor(List<String> author) {
        this.author = author;
    }

    /**
     * Gets the book's title
     * @return string of the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the book's title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the book's ISBN
     * @return isbn as integer
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Set the book's ISBN
     * @param isbn
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Get the book's description
     * @return String of the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the book's description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the book's current status
     * @return status ('available' / 'unavailable')
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the book's current status
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the book's owner
     * @return owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Set the book's owner
     * @param owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Get the book's current borrower
     * @return borrower (returns 'none' if no borrower)
     */
    public String getBorrower() {
        return borrower;
    }

    /**
     * Set the book's current borrower
     * @param borrower
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * Return the book's URI
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the book's URI
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     *  Get the book's local URI
     * @return
     */
    public String getLocalUri() {
        return localUri;
    }

    /**
     * Set the books local URI
     * @param localUri
     */
    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

}
