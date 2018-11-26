package com.example.andrewtran.superapp.models;

public class User {
    private String Name;
    private String Email;

    private User() {
    }

    public User(String name, String email){
        Name = name;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }
}
