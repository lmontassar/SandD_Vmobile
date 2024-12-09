package com.example.sandd_vmobile.model;

public class LoginResponse {
    private String jwt;
    private User user;

    public LoginResponse(User user, String jwt) {
        this.user = user;
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public User getUser() {
        return user;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
