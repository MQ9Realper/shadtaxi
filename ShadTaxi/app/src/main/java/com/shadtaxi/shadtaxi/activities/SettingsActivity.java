package com.shadtaxi.shadtaxi.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);

        Utils utils = new Utils(this, this);
        utils.initToolbar(toolbar, getResources().getString(R.string.action_settings), DashboardActivity.class);
    }
}
