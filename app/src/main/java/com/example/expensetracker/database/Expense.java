package com.example.expensetracker.database;

public class Expense {

    private int id;
    private String type;
    private int amount;
    private String date;
    private String comments;
    private int tripId = 0;

    public Expense(String type, int amount, String date, String comments, int tripId) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.comments = comments;
        this.tripId = tripId;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getTripId() {
        return tripId;
    }
}
