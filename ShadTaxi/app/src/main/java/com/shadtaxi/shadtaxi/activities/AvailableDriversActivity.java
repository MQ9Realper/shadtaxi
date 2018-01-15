package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.AvailableDriversAdapter;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Edt;

import org.apache.commons.lang3.text.WordUtils;

public class AvailableDriversActivity extends AppCompatActivity {
    private Utils utils;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_drivers);
        utils = new Utils(this, this);
        preferenceHelper = new PreferenceHelper(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getExtraData();

        initDriverList();
    }

    private void InitToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAvailableDrivers);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AvailableDriversActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        utils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }

    private void getExtraData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey("vehicle_type")) {
                String vehicle_type = bundle.getString("vehicle_type");
                if (vehicle_type.contains("BodaBoda")) {
                    InitToolbar("Nearby " + WordUtils.capitalizeFully(vehicle_type) + " Riders");
                } else {
                    InitToolbar("Nearby " + WordUtils.capitalizeFully(vehicle_type) + " Drivers");
                }
            }
        } else {
            if (preferenceHelper.getSelectedVehicleType().contains("BodaBoda")) {
                InitToolbar("Nearby " + WordUtils.capitalizeFully(preferenceHelper.getSelectedVehicleType()) + " Riders");
            } else {
                InitToolbar("Nearby " + WordUtils.capitalizeFully(preferenceHelper.getSelectedVehicleType()) + " Drivers");
            }
        }
    }

    private void initDriverList() {
        Data data = new Data();
        final AvailableDriversAdapter ridesAdapter = new AvailableDriversAdapter(this, this, data.driverArrayList());
        ListView listView = (ListView) findViewById(R.id.listViewDrivers);
        listView.setAdapter(ridesAdapter);

        Edt edtSearch = (Edt) findViewById(R.id.edtSearchRides);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ridesAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
