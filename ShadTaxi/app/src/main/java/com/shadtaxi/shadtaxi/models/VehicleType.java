package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 10/12/17.
 */

public class VehicleType {
    private String vehicle_type;
    private int vehicle_icon;

    public VehicleType(String vehicle_type, int vehicle_icon){
        this.vehicle_type = vehicle_type;
        this.vehicle_icon = vehicle_icon;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public int getVehicle_icon() {
        return vehicle_icon;
    }
}
