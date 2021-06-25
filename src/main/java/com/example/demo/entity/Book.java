package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Book {


    private int userId;
    private int id;

    private String body;

    private String title;

    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public Book(String title) {
        this.title = title;
    }

    public Book() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Book(int userId, int id, String body, String title) {
        this.userId = userId;
        this.id = id;
        this.body = body;
        this.title = title;
    }
}
