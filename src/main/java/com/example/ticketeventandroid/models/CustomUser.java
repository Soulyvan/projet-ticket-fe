package com.example.ticketeventandroid.models;

public class CustomUser {
    private int id;
    private String username;
    private String email;
    private boolean organisateur;

    public CustomUser() {
    }

    public CustomUser(int id, String username, String email, boolean organisateur) {
        this.id = id;
        this.username = username;
        this.email = email;
        // this.photo = photo;
        this.organisateur = organisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOrganisateur() {
        return organisateur;
    }

    public void setOrganisateur(boolean organisateur) {
        this.organisateur = organisateur;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
