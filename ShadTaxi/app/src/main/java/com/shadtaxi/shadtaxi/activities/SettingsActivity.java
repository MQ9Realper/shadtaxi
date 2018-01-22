package com.shadtaxi.shadtaxi.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Btn btnBecomeDriver, btnStatusPassenger, btnStatusDriver, btnAddVehicle, btnVehicleList;
    private Utils utils;
    private PreferenceHelper preferenceHelper;
    private DatabaseHelper databaseHelper;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private SwitchCompat switchCompatDriver, switchCompatPassenger;
    private TxtSemiBold txtLabelSwitchDriver, txtLabelSwitchPassenger;
    private LinearLayout layoutVehicleDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        preferenceHelper = new PreferenceHelper(this);
        databaseHelper = new DatabaseHelper(this);
        ArrayList<User> users = databaseHelper.getAllUsers();
        utils = new Utils(this, this);
        utils.initToolbar(toolbar, getResources().getString(R.string.action_settings), DashboardActivity.class);

        initViews();

        setListeners();

        indicateUserStatus(users.get(0));

        if (users.get(0).isDriver().equals("true")) {
            btnBecomeDriver.setVisibility(View.GONE);
        } else if (users.get(0).isDriver().equals("false")) {
            btnBecomeDriver.setVisibility(View.VISIBLE);
        }


    }

    private void initViews() {
        btnBecomeDriver = (Btn) findViewById(R.id.btnBecomeDriver);
        btnStatusPassenger = (Btn) findViewById(R.id.btnStatusPassenger);
        btnStatusDriver = (Btn) findViewById(R.id.btnStatusDriver);
        switchCompatDriver = (SwitchCompat) findViewById(R.id.switchDriver);
        switchCompatPassenger = (SwitchCompat) findViewById(R.id.switchRider);
        txtLabelSwitchDriver = (TxtSemiBold) findViewById(R.id.txtLabelSwitchDriver);
        txtLabelSwitchPassenger = (TxtSemiBold) findViewById(R.id.txtLabelSwitchPassenger);
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutVehicles);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        layoutVehicleDetails = (LinearLayout) findViewById(R.id.layoutVehicleDetails);

        btnAddVehicle = (Btn) findViewById(R.id.btnAddVehicle);
        btnVehicleList = (Btn) findViewById(R.id.btnViewVehicles);
    }

    private void setListeners() {
        btnBecomeDriver.setOnClickListener(this);
        btnStatusPassenger.setOnClickListener(this);
        btnStatusDriver.setOnClickListener(this);
        btnVehicleList.setOnClickListener(this);
        btnAddVehicle.setOnClickListener(this);

        switchCompatPassenger.setOnCheckedChangeListener(this);
        switchCompatDriver.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBecomeDriver:
                AlertDialog.Builder alertLogout = new AlertDialog.Builder(this);
                alertLogout.setTitle("Confirm Driver Registration");
                alertLogout.setMessage("Are you sure you want to register as a driver?");
                alertLogout.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        becomeDriver();
                    }
                });
                alertLogout.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                Dialog dialog = alertLogout.create();
                dialog.show();
                break;
            case R.id.btnStatusPassenger:
                toggleUser();
                break;
            case R.id.btnStatusDriver:
                toggleUser();
                break;
            case R.id.btnAddVehicle:
                Intent intent = new Intent(SettingsActivity.this, AddVehicleActivity.class);
                startActivity(intent);
                break;
            case R.id.btnViewVehicles:
                Intent intent1 = new Intent(SettingsActivity.this, VehicleListActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchDriver:
                if (!isChecked) {
                    toggleUser();
                }
                break;
            case R.id.switchRider:
                if (!isChecked) {
                    toggleUser();
                }
                break;
            default:
                break;
        }
    }

    private void becomeDriver() {
        utils.showProgressDialog("Please wait...");
        AndroidNetworking.post(Constants.BECOME_DRIVER)
                .addHeaders("Authorization", "Bearer " + preferenceHelper.getAccessToken())
                .addBodyParameter("latlong", "-4.365655,37.12444")
                .setTag("becomeDriver")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            JSONObject jsonObject = jsonArray.getJSONObject("data");

                            final String id = jsonObject.getString("id");
                            final String name = jsonObject.getString("name");
                            final String email = jsonObject.getString("email");
                            final String phone = jsonObject.getString("phone");
                            final String image = jsonObject.getString("image");
                            final String isRider = jsonObject.getString("isRider");
                            final String isDriver = jsonObject.getString("isDriver");
                            final String profile = jsonObject.getString("profile");

                            User user = new User();
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setImage(image);
                            user.setRider(isRider);
                            user.setDriver(isDriver);
                            user.setProfile(profile);

                            databaseHelper.updateUser(user);

                            indicateUserStatus(user);

                            utils.dismissProgressDialog();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        utils.dismissProgressDialog();
                        String response_string = error.getErrorBody();
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

    private void toggleUser() {
        utils.showProgressToast("Changing status...");
        AndroidNetworking.post(Constants.TOGGLE_USER)
                .addHeaders("Authorization", "Bearer " + preferenceHelper.getAccessToken())
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("Accept", "application/json")
                .setTag("toggleUser")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            JSONObject jsonObject = jsonArray.getJSONObject("data");

                            final String id = jsonObject.getString("id");
                            final String name = jsonObject.getString("name");
                            final String email = jsonObject.getString("email");
                            final String phone = jsonObject.getString("phone");
                            final String image = jsonObject.getString("image");
                            final String isRider = jsonObject.getString("isRider");
                            final String isDriver = jsonObject.getString("isDriver");
                            final String profile = jsonObject.getString("profile");

                            User user = new User();
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setImage(image);
                            user.setRider(isRider);
                            user.setDriver(isDriver);
                            user.setProfile(profile);

                            databaseHelper.updateUser(user);

                            indicateUserStatus(user);

                            utils.showSuccessToast("User status has been changed to " + profile);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        String response_string = error.getErrorBody();
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

    private void indicateUserStatus(User user) {
        if (user.getProfile().equals("driver")) {
            switchCompatDriver.setChecked(true);
            txtLabelSwitchDriver.setText(getString(R.string.string_disable_driver));

            switchCompatPassenger.setChecked(false);
            txtLabelSwitchPassenger.setText(getString(R.string.string_switch_to_passenger));

            if (layoutVehicleDetails.getVisibility() == View.GONE) {
                layoutVehicleDetails.setVisibility(View.VISIBLE);
            }

        } else if (user.getProfile().equals("rider")) {
            switchCompatPassenger.setChecked(true);
            txtLabelSwitchPassenger.setText(getString(R.string.string_disable_passenger));

            switchCompatDriver.setChecked(false);
            txtLabelSwitchDriver.setText(getString(R.string.string_switch_to_driver));

            if (layoutVehicleDetails.getVisibility() == View.VISIBLE) {
                layoutVehicleDetails.setVisibility(View.GONE);
            }
        }

    }
}
