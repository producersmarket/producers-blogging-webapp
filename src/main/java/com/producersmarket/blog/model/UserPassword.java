package com.producersmarket.blog.model;

public class UserPassword {

    private int id = -1;
    private String passwordHash = null;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

}
