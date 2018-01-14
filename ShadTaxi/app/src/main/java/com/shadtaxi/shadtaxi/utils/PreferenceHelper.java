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

    public void putSelectedVehicleType(String selected_vehicle_type) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("selected_vehicle_type", selected_vehicle_type);
        edit.apply();
    }

    public void putPickUpAddress(String pick_up_address){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("pick_up_address", pick_up_address);
        edit.apply();
    }

    public void putDropOffAddress(String drop_off_address){
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString("drop_off_address", drop_off_address);
        edit.apply();
    }

    public String getPickUpAddress(){
        return app_prefs.getString("pick_up_address", "");
    }

    public String getDropOffAddress(){
        return app_prefs.getString("drop_off_address", "");
    }

    public String getSelectedVehicleType(){
        return app_prefs.getString("selected_vehicle_type", "");
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

    public String getAccessToken() {
        return app_prefs.getString("access_token", "");
    }

    public String getRefreshToken() {
        return app_prefs.getString("refresh_token", "");
    }

    public boolean getIsLoggedIn() {
        return app_prefs.getBoolean("logged_in", false);
    }
}
