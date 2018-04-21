package com.shadtaxi.shadtaxi.activities;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.TabLayoutAdapter;
import com.shadtaxi.shadtaxi.fragments.AddVehicleFragment;
import com.shadtaxi.shadtaxi.fragments.VehicleListFragment;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        Utils utils = new Utils(this, this);
        utils.initToolbar(toolbar, getResources().getString(R.string.action_settings), DashboardActivity.class);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutMain);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        initTabLayout(slidingTabLayout, viewPager);

    }

    private void initTabLayout(SlidingTabLayout slidingTabLayout, ViewPager viewPager) {
        AddVehicleFragment addVehicleFragment = new AddVehicleFragment();
        VehicleListFragment vehicleListFragment = new VehicleListFragment();

        Fragment[] fragments = new Fragment[]{addVehicleFragment, vehicleListFragment};

        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager(), new String[]{"Add Vehicle", "Vehicle List"}, 2, fragments);
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

}
