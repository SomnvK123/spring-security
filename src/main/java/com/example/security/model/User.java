package com.example.security.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private String username;
    private String password;
    private Integer phone;
    private String roles;


    public User() {}

    public User(String username, String password, String roles, Integer phone) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.phone = phone;
    }

    public Integer getPhone() {
        return phone;
    }

    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
