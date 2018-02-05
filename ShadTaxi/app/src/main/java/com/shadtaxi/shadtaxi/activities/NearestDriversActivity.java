package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.ListView;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.NearestDriversAdapter;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.models.NearestDriver;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Edt;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NearestDriversActivity extends AppCompatActivity {
    private ArrayList<NearestDriver> nearestDrivers;
    private Utils utils;
    private PreferenceHelper preferenceHelper;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_drivers);
        nearestDrivers = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.toolbarAvailableDrivers);
        utils = new Utils(this, this);
        preferenceHelper = new PreferenceHelper(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getExtraData();

    }

    private void getExtraData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey("vehicle_type")) {
                String vehicle_type = bundle.getString("vehicle_type");
                String response = bundle.getString("response");
                if (vehicle_type.contains("BodaBoda")) {
                    utils.initToolbar(toolbar, "Nearby " + WordUtils.capitalizeFully(vehicle_type) + " Riders", DashboardActivity.class);
                } else {
                    utils.initToolbar(toolbar, "Nearby " + WordUtils.capitalizeFully(vehicle_type) + " Drivers", DashboardActivity.class);
                }

                initDriverList(response);
            }
        } else {
            if (preferenceHelper.getSelectedVehicleType().contains("BodaBoda")) {
                utils.initToolbar(toolbar, "Nearby " + WordUtils.capitalizeFully(preferenceHelper.getSelectedVehicleType()) + " Riders", DashboardActivity.class);
            } else {
                utils.initToolbar(toolbar, "Nearby " + WordUtils.capitalizeFully(preferenceHelper.getSelectedVehicleType()) + " Drivers", DashboardActivity.class);
            }
        }
    }

    private void initDriverList(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                final int id = jsonObject1.getInt("id");
                final String name = jsonObject1.getString("name");
                final String email = jsonObject1.getString("email");
                final String image_url = jsonObject1.getString("image_url");
                final String latlong = jsonObject1.getString("latlong");
                final String address = jsonObject1.getString("address");
                final String distance = jsonObject1.getString("distance");
                final String time = jsonObject1.getString("time");

                NearestDriver nearestDriver = new NearestDriver();
                nearestDriver.setId(id);
                nearestDriver.setName(name);
                nearestDriver.setEmail(email);
                nearestDriver.setImage_url(image_url);
                nearestDriver.setLatlong(latlong);
                nearestDriver.setAddress(address);
                nearestDriver.setDistance(distance);
                nearestDriver.setTime(time);

                nearestDrivers.add(nearestDriver);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final NearestDriversAdapter ridesAdapter = new NearestDriversAdapter(this, this, nearestDrivers);
        RecyclerView listView = (RecyclerView) findViewById(R.id.listViewDrivers);
        listView.setLayoutManager(new LinearLayoutManager(NearestDriversActivity.this));
        listView.setHasFixedSize(true);
        listView.setAdapter(ridesAdapter);
    }

}
