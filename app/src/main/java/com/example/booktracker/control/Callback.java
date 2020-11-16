package com.example.booktracker.control;

import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public interface Callback {
    /**
     * updateUi will update the ui of the the activity that requested a query
     */
    void executeCallback();
}
