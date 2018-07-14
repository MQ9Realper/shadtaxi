package com.shadtaxi.shadtaxi.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.TabLayoutAdapter;
import com.shadtaxi.shadtaxi.fragments.LoginFragment;
import com.shadtaxi.shadtaxi.fragments.RegisterFragment;
import com.shadtaxi.shadtaxi.location_tracker.LocationTrackerAlarmReceiver;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient;
    protected Location location;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 0x9;
    public static final int REQUEST_CHECK_SETTINGS = 0x1;
    private PreferenceHelper preferenceHelper;
    private boolean currentlyTracking;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        preferenceHelper = new PreferenceHelper(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarGetStarted);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutMain);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        initGoogleApiClient();

        if (preferenceHelper.getIsLoggedIn()) {
            //if status == driver, listen to requests
            trackLocation();
            Intent intent = new Intent(StartActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }

        Utils utils = new Utils(this, this);
        utils.initToolbar(toolbar, "Get Started", MainActivity.class);

        initTabLayout(slidingTabLayout, viewPager);
    }

    private void initTabLayout(SlidingTabLayout slidingTabLayout, ViewPager viewPager) {
        LoginFragment loginFragment = new LoginFragment();
        RegisterFragment registerFragment = new RegisterFragment();

        Fragment[] fragments = new Fragment[]{loginFragment, registerFragment};

        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager(), new String[]{"Login", "Create Account"}, 2, fragments);
        viewPager.setAdapter(tabLayoutAdapter);

        slidingTabLayout.setDistributeEvenly(true);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorPrimary);
            }
        });
        slidingTabLayout.setViewPager(viewPager);

    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context,LocationTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                6000,
                pendingIntent);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, LocationTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    protected void trackLocation() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("safiree_tracking", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (currentlyTracking) {
            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
        }

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(GpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (GpsLocationReceiver != null) {
                unregisterReceiver(GpsLocationReceiver);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    /**
     * Initiate Google API Client
     */
    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(StartActivity.this)
                .addApi(LocationServices.API)
                .build();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

    }

    /**
     * Initiate Location permissions enquiry
     */
    private void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(StartActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissions();
            } else {
                showLocationSettingsDialog();
            }

        } else {
            showLocationSettingsDialog();
        }
    }

    /**
     * Show user permission dialog
     */
    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(StartActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /**
     * Show Location Access Dialog
     */
    private void showLocationSettingsDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        getCurrentLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(StartActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /**
     * On Request permission method to check the permisison is granted or not for Marshmallow+ Devices
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If permission granted show location dialog if APIClient is not null
                    if (googleApiClient == null) {
                        initGoogleApiClient();
                        showLocationSettingsDialog();
                    } else
                        showLocationSettingsDialog();
                } else {
                    requestLocationPermissions();
                }
                break;
            }
        }
    }

    /**
     * Receive results
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        getCurrentLocation();
                        break;
                    case RESULT_CANCELED:
                        showLocationSettingsDialog();
                        break;
                }
                break;
        }
    }

    /**
     * Get the current location
     */
    @SuppressWarnings("MissingPermission")
    private void getCurrentLocation() {
        try {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(StartActivity.this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        location = task.getResult();
                        preferenceHelper.putCurrentLocation(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
                        Log.e(TAG, "Latitude:: " + location.getLatitude() + " " + "Longitude:: " + location.getLongitude());
                    } else {
                        showSnackbar(getString(R.string.no_location_detected));
                    }
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

    }

    /**
     * Broadcast receiver to check status of GPS
     */
    private BroadcastReceiver GpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLocationSettingsDialog();
                        }
                    }, 10);

                }

            }
        }
    };

    /**
     * Show SnackBar
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.layoutStart);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

}
