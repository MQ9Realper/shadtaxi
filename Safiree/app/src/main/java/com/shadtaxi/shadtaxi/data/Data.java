package com.shadtaxi.shadtaxi.data;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.NearestDriver;
import com.shadtaxi.shadtaxi.models.Country;
import com.shadtaxi.shadtaxi.models.History;
import com.shadtaxi.shadtaxi.models.VehicleType;

import java.util.ArrayList;

/**
 * Created by dennis on 10/12/17.
 */

public class Data {

    public Data() {

    }

    public ArrayList<Country> countryList() {
        ArrayList<Country> countryArrayList = new ArrayList<>();
        countryArrayList.add(new Country("Kenya", "+254", R.drawable.kenya));
        countryArrayList.add(new Country("Uganda", "+256", R.drawable.uganda));
        countryArrayList.add(new Country("Tanzania", "+255", R.drawable.tanzania));

        return countryArrayList;
    }

}
