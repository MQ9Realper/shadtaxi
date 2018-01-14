package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 11/2/17.
 */

public class User {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String image;
    private String isRider;
    private String isDriver;
    private String profile;
    private String password;
    private String password_confirmation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_confirmation() {
        return password_confirmation;
    }

    public void setPassword_confirmation(String password_confirmation) {
        this.password_confirmation = password_confirmation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String isRider() {
        return isRider;
    }

    public void setRider(String rider) {
        isRider = rider;
    }

    public String isDriver() {
        return isDriver;
    }

    public void setDriver(String driver) {
        isDriver = driver;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
