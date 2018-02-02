package com.shadtaxi.shadtaxi.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.VehicleTypesAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.models.VehicleType;
import com.shadtaxi.shadtaxi.utils.EqualSpacingItemDecoration;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Edt;
import com.shadtaxi.shadtaxi.views.Txt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int PLACE_PICKER_REQUEST = 0x1;
    private static final int REQUEST_CHECK_SETTINGS = 0x7;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 0x9;
    protected GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private Utils utils;
    private Edt edtPickUpLocation;
    private Txt edtDropOffLocation, txtTotalDistance, txtTotalTime, txtTotalCost, txtUserMobileNumber;
    private TxtSemiBold txtUsername, txtUserProfile;
    private CircleImageView profileImage;
    private String MY_ADDRESS = "";
    private String CURRENCY = "Kes ";
    private String duration_value = "";
    private String VEHICLE_TYPE = "BodaBoda";
    private double DISTANCE = 0.00;
    private LinearLayout layoutBookingDetails, layoutDropOff;
    private DecimalFormat decimalFormat;
    private Btn btnFindTaxi, btnProfileAction;
    private PreferenceHelper preferenceHelper;
    private DatabaseHelper databaseHelper;
    private ArrayList<VehicleType> vehicleTypes;
    private MenuItem menuItemDriverSettings;
    public static final int PERMISSIONS_REQUEST_CODE = 0;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        utils = new Utils(this, this);
        decimalFormat = new DecimalFormat("00.00");
        preferenceHelper = new PreferenceHelper(this);
        databaseHelper = new DatabaseHelper(this);
        ArrayList<User> users = databaseHelper.getAllUsers();
        vehicleTypes = new ArrayList<>();

        initGoogleApiClient();

        initViews();

        utils.initToolbar(toolbar, "Safiree", null);

        checkDropOffAvailability();

        edtDropOffLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPlacesPicker();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menuItemDriverSettings = menu.getItem(3);

        View headerView = navigationView.getHeaderView(0);
        profileImage = (CircleImageView) headerView.findViewById(R.id.profile_image);
        txtUsername = (TxtSemiBold) headerView.findViewById(R.id.txtUsername);
        txtUserMobileNumber = (Txt) headerView.findViewById(R.id.txtUserMobileNumber);
        txtUserProfile = (TxtSemiBold) headerView.findViewById(R.id.txtUserProfile);
        btnProfileAction = (Btn) headerView.findViewById(R.id.btnProfileAction);

        btnProfileAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnProfileAction.getText().equals(R.string.string_become_driver)) {
                    toggleUser();
                } else {
                    becomeDriver();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (databaseHelper.getAllVehicleTypes().isEmpty()) {
            getVehicleTypes();
        } else {
            initVehicleTypes();
        }

        setProfile(users, profileImage, txtUsername, txtUserMobileNumber, txtUserProfile);

    }

    private void initViews() {
        edtPickUpLocation = (Edt) findViewById(R.id.edtPickUpLocation);
        edtDropOffLocation = (Txt) findViewById(R.id.edtDropOffLocation);
        layoutBookingDetails = (LinearLayout) findViewById(R.id.layoutBookingDetails);
        layoutDropOff = (LinearLayout) findViewById(R.id.layoutSelectDropoff);
        txtTotalDistance = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalDistance);
        txtTotalTime = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalDuration);
        txtTotalCost = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalCost);
        btnFindTaxi = (Btn) layoutBookingDetails.findViewById(R.id.btnBookTaxi);

        btnFindTaxi.setOnClickListener(this);
    }

    private void setProfile(ArrayList<User> users, CircleImageView profile_image, TxtSemiBold user_name, Txt user_mobile_number, TxtSemiBold user_profile) {
        if (users.isEmpty()) {
            user_name.setText(preferenceHelper.getUserName());
            user_mobile_number.setText(preferenceHelper.getUserEmail());
            user_profile.setText(preferenceHelper.getUserProfile());
            if (preferenceHelper.getUserProfile().equals("rider")) {
                btnProfileAction.setText(R.string.string_switch_to_driver);
                menuItemDriverSettings.setVisible(false);
            } else if (preferenceHelper.getUserProfile().equals("driver")) {
                btnProfileAction.setText(R.string.string_switch_to_passenger);
                menuItemDriverSettings.setVisible(true);
            }

            if (!preferenceHelper.getUserImage().isEmpty()) {
                Glide.with(this).load(preferenceHelper.getUserImage()).into(profile_image);
            } else {
                Glide.with(this).load(R.drawable.default_image).into(profile_image);
            }

            if (preferenceHelper.getUserProfile().equals("driver")) {
                layoutDropOff.setVisibility(View.GONE);
            } else if (preferenceHelper.getUserProfile().equals("rider")) {
                layoutDropOff.setVisibility(View.VISIBLE);
            }

        } else {
            User user = users.get(0);
            user_name.setText(user.getName());
            user_mobile_number.setText(user.getEmail()); //phone number
            user_profile.setText(WordUtils.capitalizeFully(user.getProfile()));

            if (user.getProfile().equals("rider")) {
                btnProfileAction.setText(R.string.string_switch_to_driver);
                menuItemDriverSettings.setVisible(false);
            } else if (user.getProfile().equals("driver")) {
                btnProfileAction.setText(R.string.string_switch_to_passenger);
                menuItemDriverSettings.setVisible(true);
            }

            if (!user.getImage().isEmpty()) {
                Glide.with(this).load(user.getImage()).into(profile_image);
            } else {
                Glide.with(this).load(R.drawable.default_image).into(profile_image);
            }

            if (user.getProfile().equals("driver")) {
                layoutDropOff.setVisibility(View.GONE);
            } else if (user.getProfile().equals("rider")) {
                layoutDropOff.setVisibility(View.VISIBLE);
            }
        }

    }

    private void checkDropOffAvailability() {
        if (edtDropOffLocation.getText().toString().isEmpty()) {
            layoutBookingDetails.setVisibility(View.GONE);
        } else {
            layoutBookingDetails.setVisibility(View.VISIBLE);
        }
    }

    private void initPlacesPicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST); // for activty
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initiate Google API Client
     */
    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .build();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (preferenceHelper.getIsLoggedIn()) {
                return;
            }
        }

        super.onBackPressed();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        try {
            if (GpsLocationReceiver != null) {
                unregisterReceiver(GpsLocationReceiver);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBookTaxi:
                Intent intent = new Intent(DashboardActivity.this, AvailableDriversActivity.class);
                intent.putExtra("vehicle_type", VEHICLE_TYPE);
                preferenceHelper.putSelectedVehicleType(VEHICLE_TYPE);
                preferenceHelper.putDropOffAddress(edtDropOffLocation.getText().toString());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initDistanceMatrix(String origin, String destination, String api_key) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Calculating fare...");
        progressDialog.show();

        AndroidNetworking.get("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins={origin}&destinations={destination}&key={api_key}")
                .addPathParameter("origin", origin)
                .addPathParameter("destination", destination)
                .addPathParameter("api_key", api_key)
                .setTag("distance_matrix")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonRespRouteDistance = new JSONObject(response)
                                    .getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("duration");

                            String duration = jsonRespRouteDistance.get("text").toString();
                            duration_value = jsonRespRouteDistance.get("value").toString();
                            txtTotalTime.setText(duration);

                            txtTotalCost.setText(CURRENCY + initFareCalculation(DISTANCE, duration_value, 0));

                            changeButtonText(databaseHelper.getAllVehicleTypes().size() - 1);

                            checkDropOffAvailability();

                            progressDialog.dismiss();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void changeButtonText(int position) {
        ArrayList<VehicleType> vehicleTypes = databaseHelper.getAllVehicleTypes();
        btnFindTaxi.setText("Find " + WordUtils.capitalizeFully(vehicleTypes.get(position).getName()));
        VEHICLE_TYPE = vehicleTypes.get(position).getName();
    }

    private String initFareCalculation(double total_distance, String total_duration, int position) {
        ArrayList<VehicleType> vehicleTypes = databaseHelper.getAllVehicleTypes();
        double total_fare = 0;
        double duration = Double.valueOf(total_duration) / 60;

        total_fare = (vehicleTypes.get(position).getPer_distance() * total_distance) + (vehicleTypes.get(position).getPer_minute() * duration);

        return decimalFormat.format(utils.roundOff(total_fare));
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();

                            (new GetAddressTask(DashboardActivity.this)).execute(location);

                            LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                            MarkerOptions markerOptions = new MarkerOptions().position(myLocation).icon(bitmapDescriptor);
                            //Location.distanceBetween(markerOptions.getPosition().latitude, markerOptions.getPosition().longitude, bidcoHq.latitude, bidcoHq.longitude, results);
                            markerOptions.title(MY_ADDRESS);
                            mMap.addMarker(markerOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));

                            mMap.setTrafficEnabled(true);
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                        } else {
                            Log.w("maps::", "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_trips:
                Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/html");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>Download Safiree and have a feel of the best taxi hailing app!.</p>"));
                startActivity(Intent.createChooser(sharingIntent, "Share Safiree via..."));
                break;
            case R.id.nav_contact_us:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Contact Us")
                        .setContentText("Press 'Call' to get in touch with us!")
                        .setCancelText("Cancel")
                        .setConfirmText("Call Us")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                checkPermissionsAndCall();
                            }

                        })
                        .show();
                break;
            case R.id.nav_logout:
                AlertDialog.Builder alertLogout = new AlertDialog.Builder(this);
                alertLogout.setTitle("Confirm Logout");
                alertLogout.setMessage("Are you sure you want to logout?");
                alertLogout.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getApplicationContext().deleteDatabase("Safiree");
                        preferenceHelper.putIsLoggedIn(false);
                        preferenceHelper.clearData();
                        Intent intent1 = new Intent(DashboardActivity.this, StartActivity.class);
                        startActivity(intent1);
                        finish();
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
            case R.id.nav_driver_settings:
                Intent settingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkPermissionsAndCall() {
        String permission = Manifest.permission.CALL_PHONE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "Allow external storage to be read.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+254788524627"));
            startActivity(callIntent);
        }
    }

    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " +
                        Double.toString(loc.getLatitude()) +
                        " , " +
                        Double.toString(loc.getLongitude()) +
                        " passed to address service";
                Log.e("LocationSampleActivity", errorString);
                e2.printStackTrace();
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
            /*
             * Format the first line of address (if available),
             * city, and country name.
             */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return address.getAddressLine(0);
            } else {
                return "No address found";
            }
        }

        @Override
        protected void onPostExecute(String address) {
            edtPickUpLocation.setText(address);
            preferenceHelper.putPickUpAddress(address);
            MY_ADDRESS = address;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initVehicleTypes() {
        VehicleTypesAdapter vehicleTypesAdapter = new VehicleTypesAdapter(this, databaseHelper.getAllVehicleTypes());
        RecyclerView listVehicleTypes = (RecyclerView) findViewById(R.id.listVehicleTypes);
        listVehicleTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        listVehicleTypes.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
        //listVehicleTypes.smoothScrollToPosition(selectedPosition);
        listVehicleTypes.setHasFixedSize(false);

        listVehicleTypes.addOnItemTouchListener(new RecyclerTouchListener(this, listVehicleTypes, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                txtTotalCost.setText(CURRENCY + initFareCalculation(DISTANCE, duration_value, position));
                changeButtonText(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        listVehicleTypes.setAdapter(vehicleTypesAdapter);
    }

    /**
     * RecyclerView: Implementing single item click and long press (Part-II)
     * <p>
     * - creating an Interface for single tap and long press
     * - Parameters are its respective view and its position
     */

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    private class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void getVehicleTypes() {
        String token = preferenceHelper.getAccessToken();
        AndroidNetworking.post(Constants.GET_VEHICLE_TYPES)
                .addHeaders("Authorization", "Bearer " + token)
                .addHeaders("Accept", "application/json")
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("latlong", "-1.248462, 36.772894")
                .setTag("vehicleTypes")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            JSONObject jsonObject = jsonArray.getJSONObject("data");
                            JSONArray jsonArray1 = jsonObject.getJSONArray("vehicletypes");

                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
                                final String id = jsonObject1.getString("id");
                                final String name = jsonObject1.getString("name");
                                final String icon = jsonObject1.getString("icon");
                                final double per_distance = jsonObject1.getDouble("per_distance");
                                final double per_minute = jsonObject1.getDouble("per_minute");
                                final double minimum_price = jsonObject1.getDouble("minimum_price");
                                final double base_price = jsonObject1.getDouble("base_price");

                                VehicleType vehicleType = new VehicleType();
                                vehicleType.setId(id);
                                vehicleType.setName(name);
                                vehicleType.setIcon(icon);
                                vehicleType.setBase_price(base_price);
                                vehicleType.setMinimum_price(minimum_price);
                                vehicleType.setPer_distance(per_distance);
                                vehicleType.setPer_minute(per_minute);

                                databaseHelper.addVehicleType(vehicleType);

                            }


                        } catch (JSONException e) {
                            Log.e("vehicles::", e.getMessage());
                        }

                        initVehicleTypes();

                    }

                    @Override
                    public void onError(ANError error) {
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
                .Builder(this)
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.RED)
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }
    }

    /**
     * Initiate Location permissions enquiry
     */
    private void checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
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
                        getLastLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(DashboardActivity.this, REQUEST_CHECK_SETTINGS);
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
                        getLastLocation();
                        break;
                    case RESULT_CANCELED:
                        showLocationSettingsDialog();
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                float[] results = new float[1];
                Place place = PlacePicker.getPlace(this, data);
                String placeName = String.format("Place: %s", place.getName());
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;

                edtDropOffLocation.setText(place.getName().toString());

                initDistanceMatrix(MY_ADDRESS, place.getAddress().toString(), "AIzaSyAK59qWv6ZvFvD44uvJaRipiH88B5lqTKU");

                Location.distanceBetween(latitude, longitude, location.getLatitude(), location.getLongitude(), results);

                DISTANCE = Double.valueOf(results[0] / 1000);

                txtTotalDistance.setText(String.valueOf(decimalFormat.format(Double.valueOf(results[0] / 1000))) + " kms");
                break;
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

    /**
     * Change user status
     */
    private void toggleUser() {
        utils.showProgressDialog("Please wait...");
        AndroidNetworking.post(Constants.TOGGLE_USER)
                .addHeaders("Authorization", "Bearer " + preferenceHelper.getAccessToken())
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("Accept", "application/json")
                .setTag("toggleUser")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    public void onResponse(String response) {
                        utils.dismissProgressDialog();
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

                            utils.showSuccessToast("Success!");

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

    /**
     * Register as a driver
     */
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

    private void indicateUserStatus(User user) {
        if (user.getProfile().equals("driver")) {
            btnProfileAction.setText(getString(R.string.string_switch_to_passenger));
            txtUserProfile.setText(WordUtils.capitalizeFully(user.getProfile()));
            menuItemDriverSettings.setVisible(true);
            layoutDropOff.setVisibility(View.GONE);

        } else if (user.getProfile().equals("rider")) {
            btnProfileAction.setText(getString(R.string.string_switch_to_driver));
            txtUserProfile.setText(WordUtils.capitalizeFully(user.getProfile()));
            menuItemDriverSettings.setVisible(false);
            layoutDropOff.setVisibility(View.VISIBLE);
        }

    }
}
