package com.shadtaxi.shadtaxi.data;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.VehicleType;

import java.util.ArrayList;

/**
 * Created by dennis on 10/12/17.
 */

public class Data {

    public Data() {

    }

    public ArrayList<VehicleType> vehicleTypeArrayList() {

        ArrayList<VehicleType> vehicleTypes = new ArrayList<>();
        vehicleTypes.add(new VehicleType("Matatu", R.drawable.icon_matatu_not_selected));
        vehicleTypes.add(new VehicleType("Salon", R.drawable.icon_salon_not_selected));
        vehicleTypes.add(new VehicleType("TukTuk", R.drawable.icon_tuktuk_not_selected));
        vehicleTypes.add(new VehicleType("BodaBoda", R.drawable.icon_motorbike_not_selected));

        return vehicleTypes;
    }
}
