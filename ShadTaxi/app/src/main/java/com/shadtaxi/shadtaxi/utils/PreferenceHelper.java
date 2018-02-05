package com.shadtaxi.shadtaxi.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dennis on 11/10/17.
 */

public class PreferenceHelper {
    private Context context = null;
    private SharedPreferences app_prefs;

    public PreferenceHelper(Context context) {
        this.context = context;
        app_prefs = context.getSharedPreferences("SAFIREE", Context.MODE_PRIVATE);
    }

    public void putUserDetails(String user_id, String user_name, String user_phone, String user_image, String isRider, String isDriver, String profile, String user_email) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("user_id", user_id);
        edit.putString("user_name", user_name);
        edit.putString("user_phone", user_phone);
        edit.putString("user_image", user_image);
        edit.putString("user_email", user_email);
        edit.putString("isRider", isRider);
        edit.putString("isDriver", isDriver);
        edit.putString("user_profile", profile);
        edit.apply();
    }

    public String getUser_Id() {
        return app_prefs.getString("user_id", "");
    }

    public String getUserName() {
        return app_prefs.getString("user_name", "");
    }

    public String getUserPhone() {
        return app_prefs.getString("user_phone", "");
    }

    public String getUserImage() {
        return app_prefs.getString("user_image", "");
    }

    public String isRider() {
        return app_prefs.getString("isRider", "");
    }

    public String isDriver() {
        return app_prefs.getString("isDriver", "");
    }

    public String getUserProfile() {
        return app_prefs.getString("user_profile", "");
    }

    public String getUserEmail() {
        return app_prefs.getString("user_email", "");
    }

    public int getCurrentVehicleId() {
        return app_prefs.getInt("vehicle_id", 0);
    }

    public void putCurrentVehicleId(int vehicle_id) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putInt("vehicle_id", vehicle_id);
        edit.apply();
    }

    public void putSelectedVehicleType(String selected_vehicle_type) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("selected_vehicle_type", selected_vehicle_type);
        edit.apply();
    }

    public void putSelectedVehicleTypeId(int vehicle_type_id) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putInt("selected_vehicle_type_id", vehicle_type_id);
        edit.apply();
    }

    public void putPickUpAddress(String pick_up_address) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("pick_up_address", pick_up_address);
        edit.apply();
    }

    public void putDropOffAddress(String drop_off_address) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("drop_off_address", drop_off_address);
        edit.apply();
    }

    public String getPickUpAddress() {
        return app_prefs.getString("pick_up_address", "");
    }

    public String getDropOffAddress() {
        return app_prefs.getString("drop_off_address", "");
    }

    public String getSelectedVehicleType() {
        return app_prefs.getString("selected_vehicle_type", "");
    }

    public int getSelectedVehicleTypeId() {
        return app_prefs.getInt("selected_vehicle_type_id", 0);
    }

    public void putIsLoggedIn(boolean logged_in) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean("logged_in", logged_in);
        edit.apply();
    }

    public void putAccessToken(String access_token) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("access_token", access_token);
        edit.apply();
    }

    public void putRefreshToken(String refresh_token) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("refresh_token", refresh_token);
        edit.apply();
    }

    public void putCurrentLocation(String current_location) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("current_location", current_location);
        edit.apply();
    }

    public String getCurrentLocation() {
        return app_prefs.getString("current_location", "");
    }

    public String getAccessToken() {
        return app_prefs.getString("access_token", "");
    }

    public String getRefreshToken() {
        return app_prefs.getString("refresh_token", "");
    }

    public boolean getIsLoggedIn() {
        return app_prefs.getBoolean("logged_in", false);
    }

    public void clearData() {
        SharedPreferences.Editor shaEditor = app_prefs.edit();
        shaEditor.clear();
        shaEditor.apply();
    }
}
