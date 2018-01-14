package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Edt;
import com.shadtaxi.shadtaxi.views.TxtLight;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private Utils utils;
    private DatabaseHelper databaseHelper;
    private PreferenceHelper preferenceHelper;

    private CircleImageView profileImageview;
    private TxtSemiBold txtProfileName;
    private TxtLight txtProfilePhoneNumber;
    private Edt edtProfileName, edtProfileEmail, edtRegMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        utils = new Utils(this, this);
        databaseHelper = new DatabaseHelper(this);
        preferenceHelper = new PreferenceHelper(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();

        InitToolbar("Profile");

        getProfileInfo();
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

    private void initViews() {
        profileImageview = (CircleImageView) findViewById(R.id.profile_image);
        txtProfileName = (TxtSemiBold) findViewById(R.id.txtProfileName);
        txtProfilePhoneNumber = (TxtLight) findViewById(R.id.txtProfilePhoneNumber);
        edtProfileName = (Edt) findViewById(R.id.edtProfileName);
        edtProfileEmail = (Edt) findViewById(R.id.edtProfileEmail);
        edtRegMobileNumber = (Edt) findViewById(R.id.edtRegMobileNumber);

    }

    private void getProfileInfo() {
        ArrayList<User> user = databaseHelper.getAllUsers();
        txtProfileName.setText(user.get(0).getName().toUpperCase(Locale.ENGLISH));
        txtProfilePhoneNumber.setText(user.get(0).getEmail());
        edtProfileName.setText(user.get(0).getName());
        edtProfileEmail.setText(user.get(0).getPhone());
        edtRegMobileNumber.setText(user.get(0).getEmail());

        if (!user.get(0).getImage().isEmpty()) {
            Glide.with(this).load(user.get(0).getImage()).into(profileImageview);
        } else {
            Glide.with(this).load(R.drawable.default_image).into(profileImageview);
        }
    }
}
