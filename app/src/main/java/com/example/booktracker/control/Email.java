package com.example.booktracker.control;

import android.app.Application;

public class Email extends Application {
    private String email;
    public String getEmail(){
        return email;
    }
    public void setEmail(String newEmail){
        this.email = newEmail;
    }
}
