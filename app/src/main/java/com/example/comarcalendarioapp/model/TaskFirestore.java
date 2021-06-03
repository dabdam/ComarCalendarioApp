package com.example.comarcalendarioapp.model;

public class TaskFirestore {
    private String description;
    private String date;
    private String place;
    private String author;

    public TaskFirestore() {
    }

    public TaskFirestore(String description, String date, String place, String author) {
        this.description = description;
        this.date = date;
        this.place = place;
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
