package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 11/8/17.
 */

public class AvailableDriver {
    private String driver_name;
    private int driver_image;
    private float driver_rating;
    private String driver_distance;

    public AvailableDriver(String driver_name, int driver_image, float driver_rating, String driver_distance) {
        this.driver_name = driver_name;
        this.driver_image = driver_image;
        this.driver_rating = driver_rating;
        this.driver_distance = driver_distance;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public int getDriver_image() {
        return driver_image;
    }

    public float getDriver_rating() {
        return driver_rating;
    }

    public String getDriver_distance() {
        return driver_distance;
    }
}
