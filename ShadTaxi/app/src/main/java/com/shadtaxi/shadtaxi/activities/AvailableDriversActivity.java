package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.AvailableDriversAdapter;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.models.AvailableDriver;
import com.shadtaxi.shadtaxi.utils.UniversalUtils;
import com.shadtaxi.shadtaxi.views.Edt;

public class AvailableDriversActivity extends AppCompatActivity {
    private UniversalUtils universalUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_drivers);
        universalUtils = new UniversalUtils(this);

        InitToolbar("Nearby Drivers");

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
        universalUtils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }

    private void initDriverList(){
        Data data = new Data();
        final AvailableDriversAdapter ridesAdapter = new AvailableDriversAdapter(this, data.driverArrayList());
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
