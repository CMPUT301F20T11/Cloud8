package com.example.booktracker.entities;

/**
 * Class for Users within the application
 *
 * @author Edlee Ducay
 * 10/28/2020
 */
public class User {
    public String name;
    public String email;
    public String phoneNumber;
    public String token;

    public User() {}

    /**
     * Constructor for User class without token
     * @param name User's name
     * @param email User's email
     * @param phoneNumber User's phone number
     */
    public User(String name, String email, String phoneNumber){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor for User class with token
     * @param name User's name
     * @param email User's email
     * @param phoneNumber User's phone number
     * @param argToken
     */
    public User(String name, String email, String phoneNumber, String argToken){
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.token = argToken;
    }
    //=========Ivan===============

    /**
     * Gets the email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the username of the user
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get's the user's phone number
     * @return
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Get's the user's token
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * Set's the user's token
     * @param token
     */
    public void setToken(String token) {
        this.token = token;
    }

    //============================
}
