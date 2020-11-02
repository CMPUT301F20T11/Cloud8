package com.example.booktracker.boundary;

import com.example.booktracker.entities.Book;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
public class addBookQueryTest {
    @Test
    public void testIsbnQuery(){
        AddBookQuery test = new AddBookQuery();
        ArrayList<Book> result = test.isbnQuery("9780871295422");
        ArrayList<String> authors = (ArrayList) Arrays.
                asList("Robert Owens","George Orwell","Wilton E. Hall","William A. Miles");
        assertEquals(1,result.size());
        assertEquals("George Orwell's 1984",result.get(0).getTitle());
        assertEquals(0,result.get(0).getAuthors().containsAll(authors));
    }
}
