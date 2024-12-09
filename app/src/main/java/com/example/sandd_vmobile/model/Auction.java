package com.example.sandd_vmobile.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Auction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private User seller;
    private String title;
    private String description;
    private float participationPrice;
    private float startPrice;
    private float currentPrice;
    private float weight;
    private Date startTime;
    private Date endTime;
    private Status status;
    private List<String> imageUrls;

    public enum Status {
        OPEN,
        CLOSED,
        CANCELED
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getParticipationPrice() { return participationPrice; }
    public void setParticipationPrice(float participationPrice) { this.participationPrice = participationPrice; }

    public float getStartPrice() { return startPrice; }
    public void setStartPrice(float startPrice) { this.startPrice = startPrice; }

    public float getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(float currentPrice) { this.currentPrice = currentPrice; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}

