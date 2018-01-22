package com.shadtaxi.shadtaxi.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.VehicleListAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.models.Vehicle;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Btn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VehicleListActivity extends AppCompatActivity {
    private PreferenceHelper preferenceHelper;
    private List<Vehicle> vehicleList;
    private RecyclerView listVehicles;
    private LinearLayout layoutEmptyVehicleList;
    private Btn btnVehicleListRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVehicleList);
        final Utils utils = new Utils(this, this);
        utils.initToolbar(toolbar, "Vehicle List", SettingsActivity.class);
        preferenceHelper = new PreferenceHelper(this);
        vehicleList = new ArrayList<>();
        listVehicles = (RecyclerView) findViewById(R.id.listViewVehicles);
        layoutEmptyVehicleList = (LinearLayout) findViewById(R.id.layoutEmptyList);
        btnVehicleListRefresh = (Btn) layoutEmptyVehicleList.findViewById(R.id.btnRefreshVehicles);

        getVehicleList(utils);

        btnVehicleListRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVehicleList(utils);
            }
        });
    }

    private void getVehicleList(final Utils utils) {
        utils.showProgressDialog("Please wait...");
        String token = preferenceHelper.getAccessToken();
        AndroidNetworking.get(Constants.GET_VEHICLES)
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Accept", "application/json")
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .setTag("vehicles")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        //utils.showErrorToast(response);
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            JSONArray jsonArray1 = jsonArray.getJSONArray("data");

                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                final int id = jsonObject1.getInt("id");
                                final String number = jsonObject1.getString("number");
                                final int year = jsonObject1.getInt("year");
                                final int capacity = jsonObject1.getInt("capacity");
                                final String model = jsonObject1.getString("model");
                                final boolean verified = jsonObject1.getBoolean("verified");

                                Vehicle vehicle = new Vehicle();
                                vehicle.setId(id);
                                vehicle.setNumber(number);
                                vehicle.setYear(year);
                                vehicle.setCapacity(capacity);
                                vehicle.setModel(model);
                                vehicle.setVerified(verified);

                                vehicleList.add(vehicle);

                            }


                        } catch (JSONException e) {
                            Log.e("vehicles::", e.getMessage());
                        }

                        initVehicleList(vehicleList);
                        utils.dismissProgressDialog();

                    }

                    @Override
                    public void onError(ANError error) {
                        utils.dismissProgressDialog();
                        layoutEmptyVehicleList.setVisibility(View.VISIBLE);
                        String response_string = error.getErrorBody();
                        Log.e("error:::", response_string);
                        if (response_string != null) {
                            if (response_string.contains("data")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    utils.showErrorToast(jsonObject1.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    utils.showErrorToast(jsonObject.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else {
                            utils.showErrorToast("Internet is not available, please try again!");
                        }
                    }
                });

    }

    private void initVehicleList(List<Vehicle> vehicleList) {
        if (vehicleList.size() == 0) {
            layoutEmptyVehicleList.setVisibility(View.VISIBLE);
            listVehicles.setVisibility(View.GONE);
        } else {
            listVehicles.setVisibility(View.VISIBLE);
            layoutEmptyVehicleList.setVisibility(View.GONE);
            VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(this, vehicleList);
            listVehicles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            listVehicles.setHasFixedSize(true);
            listVehicles.setAdapter(vehicleListAdapter);
        }

    }
}
