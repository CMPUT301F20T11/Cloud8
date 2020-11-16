package com.example.booktracker.control;

import com.example.booktracker.entities.Book;

import java.util.ArrayList;

public interface Callback {
    /**
     * updateUi will udpate the ui of the the activity that requested a query
     */
    void executeCallback();
}
