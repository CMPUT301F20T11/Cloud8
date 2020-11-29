package com.example.booktracker;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.booktracker.entities.Book;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


/**
 * Test getter/setters for Book
 */
public class BookTest {
    private Book book;
    

    @Before
    public void setUp() throws Exception {
        book = new Book();
    }

    @Test
    public void BookAuthors() {
        List<String> author = new ArrayList<>();
        author.add("author1");
        author.add("author2");
        book.setAuthor(author);
        List<String> getAuthors = book.getAuthor();
        assertTrue("Incorrect Authors", getAuthors.get(0).matches("author1") && getAuthors.get(1).matches("author2"));
    }

    @Test
    public void BookTitle() {
        String title = "title";
        book.setTitle(title);
        assertTrue("Incorrect title", book.getTitle().matches(title));
    }

    @Test
    public void BookISBN() {
        String isbn = "1234123412341";
        book.setIsbn(isbn);
        assertTrue("Incorrect ISBN", book.getIsbn().matches(isbn));
    }

    @Test
    public void BookDescr() {
        String descr = "bookdescr";
        book.setDescription(descr);
        assertTrue("Incorrect Descr", book.getDescription().matches(descr));
    }

    @Test
    public void BookStatus() {
        String status = "borrowed";
        book.setStatus(status);
        assertTrue("Incorrect status", book.getStatus().matches(status));
    }

    @Test
    public void BookOwner() {
        String ownerName = "owner";
        String ownerEmail = "owner@gmail.com";
        Map<String, String> owner = new HashMap<>();
        owner.put(ownerEmail, ownerName);
        book.setOwner(owner);
        assertSame("Incorrect owner", book.getOwner(), owner);
        assertSame("Incorrect owner", book.getOwnerName(), ownerName);
        assertSame("Incorrect owner", book.getOwnerEmail(), ownerEmail);
    }

    @Test
    public void BookBorrower() {
        String borrower = "borrower";
        book.setBorrower(borrower);
        assertTrue("Incorrect Borrower", book.getBorrower().matches(borrower));
    }

    @Test
    public void BookURI() {
        String localUri = "uri";
        book.setUri(localUri);
        assertTrue("Incorrect URI", book.getUri().matches(localUri));
    }

    @Test
    public void BookLocation() {
        Double lat = 1.23;
        Double lon = 1.24;
        book.setLat(lat);
        book.setLon(lon);
        assertSame(book.getLat(), lat);
        assertSame(book.getLon(), lon);
    }
}
