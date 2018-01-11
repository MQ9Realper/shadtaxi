package com.shadtaxi.shadtaxi.constants;

/**
 * Created by dennis on 07/11/17.
 */

public class Constants {
    public static final String BASE_URL = "http://31bbd90d.ngrok.io";
    public static final String REGISTRATION_URL = BASE_URL + "/api/user/register/";
    public static final String LOGIN_URL = BASE_URL + "/oauth/token";
    public static final String GET_USER = BASE_URL + "/api/user";

    public static final int BODA_PRICE_PER_KILOMETER = 100;
    public static final int BODA_PRICE_PER_MINUTE = 5;

    public static final int TUK_PRICE_PER_KILOMETER = 120;
    public static final int TUK_PRICE_PER_MINUTE = 5;

    public static final int SALON_PRICE_PER_KILOMETER = 180;
    public static final int SALON_PRICE_PER_MINUTE = 5;

    public static final int MATATU_PRICE_PER_KILOMETER = 30;
    public static final int MATATU_PRICE_PER_MINUTE = 5;
}
