package com.shadtaxi.shadtaxi.fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.VehicleTypeSpinnerAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Edt;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddVehicleFragment extends Fragment implements View.OnClickListener{
    private AppCompatSpinner spinnerVehicleTypes,txtVehicleModels;
    private PreferenceHelper preferenceHelper;
    private ProgressDialog progressDialog;
    private Btn btnSaveVehicle;
    private Edt edtVehicleCapacity, edtVehicleNumber;
    private DatabaseHelper databaseHelper;

    public AddVehicleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        databaseHelper = new DatabaseHelper(getActivity());
        preferenceHelper = new PreferenceHelper(getActivity());

        View view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        initViews(view);
        initVehicleTypeList();
        initVehicleModels();
        initListeners();

        return view;
    }

    private void initViews(View view) {
        spinnerVehicleTypes = (AppCompatSpinner) view.findViewById(R.id.spinnerVehicleType);
        txtVehicleModels = (AppCompatSpinner) view.findViewById(R.id.txtVehicleModel);
        btnSaveVehicle = (Btn) view.findViewById(R.id.btnSaveVehicle);
        edtVehicleCapacity = (Edt) view.findViewById(R.id.edtVehicleCapacity);
        edtVehicleNumber = (Edt) view.findViewById(R.id.edtVehicleNumber);
    }

    private void initVehicleTypeList() {
        VehicleTypeSpinnerAdapter vehicleTypeSpinnerAdapter = new VehicleTypeSpinnerAdapter(getActivity(), R.layout.layout_spinner_item, databaseHelper.getAllVehicleTypes());
        spinnerVehicleTypes.setAdapter(vehicleTypeSpinnerAdapter);
    }

    private void initListeners() {
        btnSaveVehicle.setOnClickListener(this);
    }

    private void initVehicleModels() {
        String[] vehicleModels = {"Toyota Allion", "Toyota Belta", "Toyota Axio", "Toyota Fielder", "Toyota NZE", "Tiger 900", "Tiger 955i", "Tiger 1050", "Tiger 800", "Ape A", "Ape C", "Ape P501"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_item, R.id.txtSpinnerItem, vehicleModels);
        txtVehicleModels.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveVehicle:
                if (TextUtils.isEmpty(edtVehicleNumber.getText().toString())) {
                    showErrorToast("Please enter the vehicle number.");
                } else if (TextUtils.isEmpty(edtVehicleCapacity.getText().toString())) {
                    showErrorToast("Please enter the vehicle capacity.");
                }  else {
                    addVehicle(edtVehicleNumber.getText().toString(), edtVehicleCapacity.getText().toString(), txtVehicleModels.getSelectedItem().toString(), databaseHelper.getAllVehicleTypes().get(spinnerVehicleTypes.getSelectedItemPosition()).getId());
                }
                break;
            default:
                break;
        }
    }

    private void addVehicle(String number, String capacity, String company, String vehicle_type) {
        showProgressDialog("Adding vehicle...");
        String token = preferenceHelper.getAccessToken();
        AndroidNetworking.post(Constants.ADD_VEHICLE)
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("Accept", "application/json")
                .addBodyParameter("number", number)
                .addBodyParameter("capacity", capacity)
                .addBodyParameter("company", company)
                .addBodyParameter("vehicle_type", vehicle_type)
                .setTag("addVehicle")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("vehicle", response);
                        if (response.contains("data")) {
                            dismissProgressDialog();
                            showSuccessToast("You have successfully added a vehicle!");
                            edtVehicleCapacity.setText("");
                            edtVehicleNumber.setText("");
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        dismissProgressDialog();
                        String response_string = error.getErrorBody();
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

    private void showSuccessToast(String message) {
        StyleableToast styleableToast = new StyleableToast
                .Builder(getActivity())
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.parseColor("#2cb742"))
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }

    }
}
