package com.shadtaxi.shadtaxi.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.shadtaxi.shadtaxi.BuildConfig;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.VehicleTypesAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.utils.EqualSpacingItemDecoration;
import com.shadtaxi.shadtaxi.utils.UniversalUtils;
import com.shadtaxi.shadtaxi.views.Edt;
import com.shadtaxi.shadtaxi.views.Txt;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_PICKER_REQUEST = 0x1;
    protected GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Location location;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String TAG = DashboardActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private UniversalUtils universalUtils;
    private Toolbar toolbar;
    private Edt edtPickUpLocation;
    private Txt edtDropOffLocation, txtTotalDistance, txtTotalTime, txtTotalCost;
    private String MY_ADDRESS = "";
    private String CURRENCY = "Kes ";
    private String duration_value = "";
    private double DISTANCE = 0.00;
    private LinearLayout layoutBookingDetails;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        universalUtils = new UniversalUtils(this);
        decimalFormat = new DecimalFormat("0.00");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        InitToolbar(getResources().getString(R.string.app_name));

        initViews();

        checkDropOffAvailability();

        edtDropOffLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPlacesPicker();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        initVehicleTypes();
    }

    private void initViews() {
        edtPickUpLocation = (Edt) findViewById(R.id.edtPickUpLocation);
        edtDropOffLocation = (Txt) findViewById(R.id.edtDropOffLocation);
        layoutBookingDetails = (LinearLayout) findViewById(R.id.layoutBookingDetails);
        txtTotalDistance = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalDistance);
        txtTotalTime = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalDuration);
        txtTotalCost = (Txt) layoutBookingDetails.findViewById(R.id.txtTotalCost);
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

    private void InitToolbar(String name) {
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        universalUtils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checkPermissionOnActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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

            }
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

                            txtTotalCost.setText(CURRENCY + initFareCalculation(DISTANCE, duration_value,0));

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

    private String initFareCalculation(double total_distance, String total_duration, int position) {
        double total_fare = 0;
        double duration = Double.valueOf(total_duration) / 60;

        switch (position){
            case 0:
                total_fare = (Constants.BODA_PRICE_PER_KILOMETER * total_distance) + (Constants.BODA_PRICE_PER_MINUTE * duration);
                break;
            case 1:
                total_fare = (Constants.TUK_PRICE_PER_KILOMETER * total_distance) + (Constants.TUK_PRICE_PER_MINUTE * duration);
                break;
            case 2:
                total_fare = (Constants.SALON_PRICE_PER_KILOMETER * total_distance) + (Constants.SALON_PRICE_PER_MINUTE * duration);
                break;
            case 3:
                total_fare = (Constants.MATATU_PRICE_PER_KILOMETER * total_distance) + (Constants.MATATU_PRICE_PER_MINUTE * duration);
                break;
            default:
                break;
        }

        return decimalFormat.format(total_fare);
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

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.map);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(DashboardActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.action_settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            MY_ADDRESS = address;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initVehicleTypes() {
        Data data = new Data();
        VehicleTypesAdapter vehicleTypesAdapter = new VehicleTypesAdapter(this, data.vehicleTypeArrayList());
        RecyclerView listVehicleTypes = (RecyclerView) findViewById(R.id.listVehicleTypes);
        listVehicleTypes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        listVehicleTypes.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
        //listVehicleTypes.smoothScrollToPosition(selectedPosition);
        listVehicleTypes.setHasFixedSize(true);

        listVehicleTypes.addOnItemTouchListener(new RecyclerTouchListener(this, listVehicleTypes, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                txtTotalCost.setText(CURRENCY + initFareCalculation(DISTANCE, duration_value,position));
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
}
