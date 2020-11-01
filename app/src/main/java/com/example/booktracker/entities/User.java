package com.example.booktracker.entities;

public class User {
    public String name, email, phoneNumber;

    public User() {

    }
    public User(String name, String email, String phoneNumber){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    //=========Ivan===============

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    //============================
}
