package com.example.booktracker.boundary;

import com.example.booktracker.entities.Book;

public class DeleteBookQuery extends BookQuery{
    public DeleteBookQuery(String email){
        super(email);
    }

    /**
     * This will query the database and delete a book
     * @param book book to be deleted
     */
    public void deleteBook(Book book){
        if (book.getStatus() != ""&& book.getStatus() != null){
            userDoc.collection(book.getStatus())
                    .document(book.getIsbn())
                    .delete();
        }
        userDoc.collection("myBooks")
                .document(book.getIsbn())
                .delete();
    }

}
