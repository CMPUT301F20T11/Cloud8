package com.example.booktracker.entities;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for the book entity
 *
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
    private String stringOwner;
    private Map<String, String> owner;
    private String borrower;
    //======================
    private String status;
    private Double lat = null;
    private Double lon = null;

    /**
     * constructor for initializing an empty book
     */
    public Book() {
        this.title = "";
        this.isbn = "";
        this.description = "";
        Map<String, String> nestedData = new HashMap<>();
        nestedData.put("", "");
        this.owner = nestedData;
        owner.put("", "");
        this.status = "";
        this.borrower = "";
    }

    /**
     * Constructor for books with a description
     *
     * @param argOwner
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     * @param argDesc
     */
    public Book(HashMap<String, String> argOwner, List<String> argAuthor,
                String argTitle, String argIsbn, String argDesc) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.description = argDesc;
        this.owner = argOwner;
        this.status = "available";
        this.borrower = null;
    }

    public Book(String argOwner, List<String> argAuthor, String argTitle,
                String argIsbn, String argDesc) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.description = argDesc;
        this.stringOwner = argOwner;
        this.status = "available";
        this.borrower = null;
    }

    /**
     * Constructor for books without a description
     *
     * @param argOwner
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     */
    public Book(HashMap<String, String> argOwner, List<String> argAuthor,
                String argTitle, String argIsbn) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.owner = argOwner;
        this.status = "";
        this.borrower = null;
    }

    /**
     * Constructor for books without a description without owner
     *
     * @param argAuthor
     * @param argTitle
     * @param argIsbn
     */
    public Book(List<String> argAuthor, String argTitle, String argIsbn) {
        this.author = argAuthor;
        this.title = argTitle;
        this.isbn = argIsbn;
        this.status = "";
        this.borrower = null;
    }

    /**
     * Gets the book's author(s)
     *
     * @return List of author(s)
     */
    public List<String> getAuthor() {
        return author;
    }

    /**
     * Sets the book's author(s)
     *
     * @param author
     */
    public void setAuthor(List<String> author) {
        this.author = author;
    }

    /**
     * Gets the book's title
     *
     * @return string of the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the book's title
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the book's ISBN
     *
     * @return isbn as integer
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Set the book's ISBN
     *
     * @param isbn
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Get the book's description
     *
     * @return String of the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the book's description
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the book's current status
     *
     * @return status ('available' / 'unavailable')
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the book's current status
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the book's owner
     *
     * @return owner
     */
    public HashMap<String, String> getOwner() {
        return (HashMap<String, String>) owner;
    }

    public String getStringOwner() {
        return stringOwner;
    }

    public String getOwnerEmail() {
        Map.Entry<String, String> entry = owner.entrySet().iterator().next();
        return entry.getKey();
    }

    public String getOwnerName() {
        Map.Entry<String, String> entry = owner.entrySet().iterator().next();
        return entry.getValue();
    }

    /**
     * Set the book's owner
     *
     * @param owner
     */
    public void setOwner(Map<String, String> owner) {
        this.owner = owner;
    }

    public void setStringOwner(String owner) {
        this.stringOwner = owner;
    }

    /**
     * Get the book's current borrower
     *
     * @return borrower (returns 'none' if no borrower)
     */
    public String getBorrower() {
        return borrower;
    }

    /**
     * Set the book's current borrower
     *
     * @param borrower
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * Return the book's URI
     *
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the book's URI
     *
     * @param uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Get the book's local URI
     *
     * @return
     */
    public String getLocalUri() {
        return localUri;
    }

    /**
     * Set the books local URI
     *
     * @param localUri
     */
    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
    public void setLatLon(Double lon,Double lat){
        this.lon = lon;
        this.lat = lat;
    }
}
