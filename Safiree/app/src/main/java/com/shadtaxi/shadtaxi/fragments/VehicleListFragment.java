package com.shadtaxi.shadtaxi.fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.VehicleListAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.models.Vehicle;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.views.Btn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleListFragment extends Fragment implements View.OnClickListener {
    private ProgressDialog progressDialog;
    private PreferenceHelper preferenceHelper;
    private List<Vehicle> vehicleList;
    private RecyclerView listVehicles;
    private LinearLayout layoutEmptyVehicleList;


    public VehicleListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferenceHelper = new PreferenceHelper(getActivity());
        vehicleList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_vehicle_list2, container, false);
        initViews(view);
        getVehicleList();
        return view;
    }

    private void initViews(View view) {
        listVehicles = (RecyclerView) view.findViewById(R.id.listViewVehicles);
        layoutEmptyVehicleList = (LinearLayout) view.findViewById(R.id.layoutEmptyList);
        Btn btnVehicleListRefresh = (Btn) layoutEmptyVehicleList.findViewById(R.id.btnRefreshVehicles);

        btnVehicleListRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRefreshVehicles:
                getVehicleList();
                break;
            default:
                break;
        }
    }

    private void getVehicleList() {
        showProgressDialog("Please wait...");
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
                        dismissProgressDialog();

                    }

                    @Override
                    public void onError(ANError error) {
                        dismissProgressDialog();
                        layoutEmptyVehicleList.setVisibility(View.VISIBLE);
                        String response_string = error.getErrorBody();
                        Log.e("error:::", response_string);
                        if (response_string != null) {
                            if (response_string.contains("data")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    showErrorToast(jsonObject1.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    showErrorToast(jsonObject.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else {
                            showErrorToast("Internet is not available, please try again!");
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
            VehicleListAdapter vehicleListAdapter = new VehicleListAdapter(getActivity(), vehicleList);
            listVehicles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            listVehicles.setHasFixedSize(true);
            listVehicles.setAdapter(vehicleListAdapter);
        }

    }

    private void showErrorToast(String message) {
        StyleableToast styleableToast = new StyleableToast
                .Builder(getActivity())
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.RED)
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
