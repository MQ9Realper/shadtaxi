package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.utils.Utils;

public class ProfileActivity extends AppCompatActivity {
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        utils = new Utils(this, this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        InitToolbar("Profile");
    }

    private void InitToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        utils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }
}
