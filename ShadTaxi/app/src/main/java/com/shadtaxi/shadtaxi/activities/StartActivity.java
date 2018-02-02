package com.shadtaxi.shadtaxi.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.TabLayoutAdapter;
import com.shadtaxi.shadtaxi.fragments.LoginFragment;
import com.shadtaxi.shadtaxi.fragments.RegisterFragment;
import com.shadtaxi.shadtaxi.location_tracker.LocationTrackerAlarmReceiver;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;

import java.util.UUID;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "LocationService";
    private boolean currentlyTracking;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        PreferenceHelper preferenceHelper = new PreferenceHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarGetStarted);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutMain);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (preferenceHelper.getIsLoggedIn()) {
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

}
