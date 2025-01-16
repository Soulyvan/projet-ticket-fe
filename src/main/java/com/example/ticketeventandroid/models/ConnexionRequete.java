package com.example.ticketeventandroid.models;

public class ConnexionRequete {
    private String email;
    private String password;

    public ConnexionRequete(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
