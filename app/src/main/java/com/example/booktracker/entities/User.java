package com.example.booktracker.entities;

public class User {
    public String name;
    public String email;
    public String phoneNumber;
    public String token;

    public User() {

    }
    public User(String name, String email, String phoneNumber){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public User(String name, String email, String phoneNumber, String argToken){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.token = argToken;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //============================
}
