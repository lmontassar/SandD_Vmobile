package com.example.sandd_vmobile.model;

public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    //GETTERs AND SETTERs


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}