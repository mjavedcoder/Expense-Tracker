package com.example.expensetracker.database;

import java.io.Serializable;

public class Trip implements Serializable {

    private int id;
    private String name;
    private String destination;
    private String date;
    private int requiresAssessment;
    private String description = " ";
    private int daysSpent = 1;

    public Trip() {
    }

    public Trip(String name, String destination, String date, int requiresAssessment, String description, int daysSpent) {
        this.name = name;
        this.destination = destination;
        this.date = date;
        this.requiresAssessment = requiresAssessment;
        this.description = description;
        this.daysSpent = daysSpent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRequiresAssessment() {
        return requiresAssessment;
    }

    public void setRequiresAssessment(int requiresAssessment) {
        this.requiresAssessment = requiresAssessment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDaysSpent() {
        return daysSpent;
    }

    public void setDaysSpent(int daysSpent) {
        this.daysSpent = daysSpent;
    }
}
