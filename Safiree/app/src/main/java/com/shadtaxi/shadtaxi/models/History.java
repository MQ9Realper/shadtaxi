package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 11/25/17.
 */

public class History {
    private String driver_name;
    private int driver_image;
    private float driver_rating;
    private String ride_date;
    private String ride_cost;
    private String origin;
    private String destination;

    public History(String driver_name, int driver_image, float driver_rating, String ride_date, String ride_cost, String origin, String destination) {
        this.driver_name = driver_name;
        this.driver_image = driver_image;
        this.driver_rating = driver_rating;
        this.ride_date = ride_date;
        this.ride_cost = ride_cost;
        this.origin = origin;
        this.destination = destination;
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

    public String getRide_date() {
        return ride_date;
    }

    public String getRide_cost() {
        return ride_cost;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }
}
