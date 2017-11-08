package com.shadtaxi.shadtaxi.data;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.AvailableDriver;
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

    public ArrayList<AvailableDriver> driverArrayList(){
        ArrayList<AvailableDriver> driverArrayList = new ArrayList<>();
        driverArrayList.add(new AvailableDriver("Hellen  Soyinka", R.drawable.hellen_soyinka, 4.5f, "1km away"));
        driverArrayList.add(new AvailableDriver("Alex  Wanyoike", R.drawable.alex_wanyoike, 3.5f, "700m away"));
        driverArrayList.add(new AvailableDriver("Chris  Mutembei", R.drawable.chris_mutembei, 4f, "1.5km away"));
        driverArrayList.add(new AvailableDriver("Eric  Omari", R.drawable.eric_omari, 5f, "500m away"));
        driverArrayList.add(new AvailableDriver("Phylis  Wangari", R.drawable.phyllis_wangari, 2.5f, "1.2km away"));
        driverArrayList.add(new AvailableDriver("Mueni  Munyao", R.drawable.mueni_munyao, 3.5f, "60m away"));

        return driverArrayList;

    }
}
