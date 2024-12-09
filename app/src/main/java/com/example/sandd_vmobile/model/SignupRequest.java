package com.example.sandd_vmobile.model;

import okhttp3.MultipartBody;

public class SignupRequest {
    private String username;
    private String firstname;
    private String lastname;
    private int phoneNumber;
    private String address;
    private String email;
    private String password;



    private MultipartBody.Part image;

    public SignupRequest(String username, String firstname, String lastname, int phoneNumber, String address, String email, String password,MultipartBody.Part image ) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public MultipartBody.Part getImage() {
        return image;
    }

    public void setImage(MultipartBody.Part image) {
        this.image = image;
    }
}