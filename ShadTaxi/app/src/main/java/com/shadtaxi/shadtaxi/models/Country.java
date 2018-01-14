package com.shadtaxi.shadtaxi.models;

/**
 * Created by dennis on 21/09/17.
 */

public class Country {
    private String country_name;
    private String country_code;
    private int country_flag;

    public Country(String country_name, String country_code, int country_flag) {
        this.country_name = country_name;
        this.country_code = country_code;
        this.country_flag = country_flag;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public int getCountry_flag() {
        return country_flag;
    }
}
