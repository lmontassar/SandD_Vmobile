package com.example.sandd_vmobile.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String firstname;
    private String lastname;
    private float amount;
    private Integer phoneNumber;
    private String address;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    private String createdAt;
    private String status;
    private String imageUrl;


    // Getters and setters


    public User() {

    }

    public User(Long id, String username, String lastname, float amount, String imageUrl, String status, String createdAt, String address, Integer phoneNumber, String firstname, String role, String email, String password) {
        this.id = id;
        this.username = username;
        this.lastname = lastname;
        this.amount = amount;
        this.imageUrl = imageUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.firstname = firstname;
        this.role = role;
        this.email = email;
        this.password = password;
    }

    public User(Long id, String username, String email, String firstname, String lastname, float amount, Integer phoneNumber, String address, String imageUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public float getAmount() {
        return amount;
    }

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}