package com.example.booktracker;

import com.example.booktracker.entities.Book;
import com.example.booktracker.entities.NotifCount;
import com.example.booktracker.entities.User;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NotifCountTest {
    NotifCount notifCount;
    long accepted;
    long incoming;
    
    @Before
    public void setUp() throws Exception {
        notifCount = new NotifCount();
    }

    @Test
    public void Accepted() {
        accepted = 1L;
        notifCount.setAccepted(accepted);
        assertEquals(notifCount.getAccepted(), accepted);
    }

    @Test
    public void Incoming() {
        incoming = 1L;
        notifCount.setIncoming(incoming);
        assertEquals(notifCount.getIncoming(), incoming);
    }

    @Test
    public void Total() {
        assertEquals(notifCount.getTotal(), accepted+incoming);
    }

}
