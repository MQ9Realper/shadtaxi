package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 10/12/17.
 */

public class VehicleType {
    private String id;
    private String name;
    private String icon;
    private double per_distance;
    private double per_minute;
    private double minimum_price;
    private double base_price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getPer_distance() {
        return per_distance;
    }

    public void setPer_distance(double per_distance) {
        this.per_distance = per_distance;
    }

    public double getPer_minute() {
        return per_minute;
    }

    public void setPer_minute(double per_minute) {
        this.per_minute = per_minute;
    }

    public double getMinimum_price() {
        return minimum_price;
    }

    public void setMinimum_price(double minimum_price) {
        this.minimum_price = minimum_price;
    }

    public double getBase_price() {
        return base_price;
    }

    public void setBase_price(double base_price) {
        this.base_price = base_price;
    }
}
