package com.example.booktracker.entities;


import java.io.Serializable;
import java.util.List;

/**
 * Base class for the book entity
 * @author Edlee Ducay
 */
public class Book implements Serializable {
    private List<String> author;
    private String title;
    private int isbn;
    private String description;
    private String owner;
    private String status;
    private String borrower;

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
        //this.owner =    <== ADD
        this.status = "available";
        this.borrower = "none";
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
        //this.owner =    <== ADD
        this.status = "available";
        this.borrower = "none";
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
    public int getIsbn() {
        return isbn;
    }

    /**
     * Set the book's ISBN
     * @param isbn
     */
    public void setIsbn(int isbn) {
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


}
