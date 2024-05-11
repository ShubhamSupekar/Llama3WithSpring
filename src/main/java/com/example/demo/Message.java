package com.example.demo;

import java.util.List;

public class Message {
    private String role;
    private String content;

    // Constructors
    public Message() {
    }

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


