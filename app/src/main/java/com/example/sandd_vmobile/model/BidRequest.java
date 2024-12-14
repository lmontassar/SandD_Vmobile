package com.example.sandd_vmobile.model;

public class BidRequest {
    private class bid{
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public bid(Long id){
            this.id = id;
        }
    }
    private bid auctionId;
    private bid userId;
    private double amount;

    public BidRequest(Long aId, Long uId, double amount) {
        bid auctionId = new bid(aId);
        this.auctionId = auctionId;
        bid userId = new bid(uId);
        this.userId = userId;
        this.amount = amount;
    }

    public bid getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(bid auctionId) {
        this.auctionId = auctionId;
    }

    public bid getUserId() {
        return userId;
    }

    public void setUserId(bid userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
