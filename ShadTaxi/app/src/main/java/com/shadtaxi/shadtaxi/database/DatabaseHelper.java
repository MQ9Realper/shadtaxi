package com.shadtaxi.shadtaxi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.models.VehicleType;

import java.util.ArrayList;

/**
 * Created by dennis on 23/11/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "Safiree";

    private static final String TABLE_USER = "table_user";
    private static final String TABLE_VEHICLE_TYPES = "table_vehicle_types";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_IMAGE = "user_image";
    private static final String KEY_USER_ISDRIVER = "user_isdriver";
    private static final String KEY_USER_ISRIDER = "user_isrider";
    private static final String KEY_USER_PROFILE = "user_profile";

    private static final String KEY_VEHICLE_ID = "vehicle_id";
    private static final String KEY_VEHICLE_NAME = "vehicle_name";
    private static final String KEY_VEHICLE_ICON = "vehicle_icon";
    private static final String KEY_VEHICLE_PER_DISTANCE = "vehicle_per_distance";
    private static final String KEY_VEHICLE_PER_MINUTE = "vehicle_per_minute";
    private static final String KEY_VEHICLE_MINIMUM_PRICE = "vehicle_minimum_price";
    private static final String KEY_VEHICLE_BASE_PRICE = "vehicle_base_price";

    // User table create statement
    private static final String CREATE_USER_TABLE = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_USER_ID + " INTEGER PRIMARY KEY, "
            + KEY_USER_NAME + " TEXT," + KEY_USER_EMAIL + " TEXT," + KEY_USER_PHONE + " TEXT," + KEY_USER_IMAGE + " TEXT," + KEY_USER_ISDRIVER + " TEXT," + KEY_USER_ISRIDER + " TEXT," + KEY_USER_PROFILE + " TEXT" + ")";

    // User table create statement
    private static final String CREATE_VEHICLE_TYPE_TABLE = "CREATE TABLE "
            + TABLE_VEHICLE_TYPES + "(" + KEY_VEHICLE_ID + " INTEGER PRIMARY KEY, "
            + KEY_VEHICLE_NAME + " TEXT," + KEY_VEHICLE_ICON + " TEXT," + KEY_VEHICLE_PER_DISTANCE + " DOUBLE," + KEY_VEHICLE_PER_MINUTE + " DOUBLE," + KEY_VEHICLE_MINIMUM_PRICE + " DOUBLE," + KEY_VEHICLE_BASE_PRICE + " DOUBLE" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_VEHICLE_TYPE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLE_TYPES);

        // Create tables again
        onCreate(db);
    }

    // Adding user
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_PHONE, user.getPhone());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_IMAGE, user.getImage());
        values.put(KEY_USER_ISRIDER, user.isRider());
        values.put(KEY_USER_ISDRIVER, user.isDriver());
        values.put(KEY_USER_PROFILE, user.getProfile());

        db.insert(TABLE_USER, null, values);
        db.close();

    }

    // Adding vehicles
    public void addVehicleType(VehicleType vehicleType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VEHICLE_ID, vehicleType.getId());
        values.put(KEY_VEHICLE_NAME, vehicleType.getName());
        values.put(KEY_VEHICLE_ICON, vehicleType.getIcon());
        values.put(KEY_VEHICLE_BASE_PRICE, vehicleType.getBase_price());
        values.put(KEY_VEHICLE_MINIMUM_PRICE, vehicleType.getMinimum_price());
        values.put(KEY_VEHICLE_PER_DISTANCE, vehicleType.getPer_distance());
        values.put(KEY_VEHICLE_PER_MINUTE, vehicleType.getPer_minute());

        db.insert(TABLE_VEHICLE_TYPES, null, values);
        db.close();
    }

    // Get all vehicles
    // Get all users
    public ArrayList<VehicleType> getAllVehicleTypes() {
        ArrayList<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
        String selectQuery = "SELECT * FROM " + TABLE_VEHICLE_TYPES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                VehicleType vehicleType = new VehicleType();
                vehicleType.setId(cursor.getString(0));
                vehicleType.setName(cursor.getString(1));
                vehicleType.setIcon(cursor.getString(2));
                vehicleType.setBase_price(cursor.getDouble(3));
                vehicleType.setMinimum_price(cursor.getDouble(4));
                vehicleType.setPer_distance(cursor.getDouble(5));
                vehicleType.setPer_minute(cursor.getDouble(5));

                vehicleTypes.add(vehicleType);
            } while (cursor.moveToNext());
        }
        return vehicleTypes;
    }

    // Get a single user
    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_PHONE, KEY_USER_EMAIL, KEY_USER_IMAGE, KEY_USER_ISRIDER, KEY_USER_ISDRIVER, KEY_USER_PROFILE}, KEY_USER_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User();
        user.setId(cursor.getString(0));
        user.setName(cursor.getString(1));
        user.setPhone(cursor.getString(2));
        user.setEmail(cursor.getString(3));
        user.setImage(cursor.getString(4));
        user.setRider(cursor.getString(5));
        user.setDriver(cursor.getString(6));
        user.setProfile(cursor.getString(7));

        return user;
    }

    // Get all users
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setPhone(cursor.getString(2));
                user.setEmail(cursor.getString(3));
                user.setImage(cursor.getString(4));
                user.setRider(cursor.getString(5));
                user.setDriver(cursor.getString(6));
                user.setProfile(cursor.getString(7));

                users.add(user);
            } while (cursor.moveToNext());
        }
        return users;
    }

    // Updating single user
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_PHONE, user.getPhone());
        values.put(KEY_USER_EMAIL, user.getEmail());
        values.put(KEY_USER_IMAGE, user.getImage());
        values.put(KEY_USER_ISRIDER, user.isRider());
        values.put(KEY_USER_ISDRIVER, user.isDriver());
        values.put(KEY_USER_PROFILE, user.getProfile());

        // updating row
        db.update(TABLE_USER, values, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
    }

    // Deleting single CartItem
    public void deleteUser(ArrayList<User> users) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (User user : users) {
            db.delete(TABLE_USER, KEY_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        }
        db.close();
    }

    // Delete all Cart Items
    public void clearVehicleTypes(ArrayList<VehicleType> vehicleTypes) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (VehicleType vehicleType : vehicleTypes) {
            db.delete(TABLE_VEHICLE_TYPES, KEY_VEHICLE_ID + " = ?", new String[]{String.valueOf(vehicleType.getId())});
        }
        db.close();
    }
}
