package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.utils.UniversalUtils;

public class SignUpActivity extends AppCompatActivity {
    private UniversalUtils universalUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        universalUtils = new UniversalUtils(this);

        InitToolbar("Register");
    }

    private void InitToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSignUp);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        universalUtils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }
}
