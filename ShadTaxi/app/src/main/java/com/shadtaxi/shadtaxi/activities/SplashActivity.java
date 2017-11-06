package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shadtaxi.shadtaxi.R;
import com.wang.avi.AVLoadingIndicatorView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avLoadingIndicator);
        avLoadingIndicatorView.show();
        StartSplashTimer();
    }

    private void StartSplashTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(i);
                finish();
            }
        }, 4000);
    }
}
