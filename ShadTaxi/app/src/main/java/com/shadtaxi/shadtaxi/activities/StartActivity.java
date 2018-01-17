package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.TabLayoutAdapter;
import com.shadtaxi.shadtaxi.fragments.LoginFragment;
import com.shadtaxi.shadtaxi.fragments.RegisterFragment;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        PreferenceHelper preferenceHelper = new PreferenceHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarGetStarted);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutMain);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (preferenceHelper.getIsLoggedIn()) {
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

}
