package com.example.ticketeventandroid.models;

public class ConnexionReponse {
    private String token;
    private CustomUser user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }
}
