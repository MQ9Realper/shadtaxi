package com.shadtaxi.shadtaxi.activities;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.StartScreenAdapter;
import com.shadtaxi.shadtaxi.fragments.LoginFragment;
import com.shadtaxi.shadtaxi.fragments.RegisterFragment;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.SlidingTabLayout;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarGetStarted);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayoutMain);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        Utils utils = new Utils(this, this);
        utils.initToolbar(toolbar, "Get Started", MainActivity.class);

        initTabLayout(slidingTabLayout, viewPager);
    }

    private void initTabLayout(SlidingTabLayout slidingTabLayout, ViewPager viewPager) {
        LoginFragment loginFragment = new LoginFragment();
        RegisterFragment registerFragment = new RegisterFragment();

        Fragment[] fragments = new Fragment[]{loginFragment, registerFragment};

        StartScreenAdapter startScreenAdapter = new StartScreenAdapter(getSupportFragmentManager(), new String[]{"Login", "Create Account"}, 2, fragments);
        viewPager.setAdapter(startScreenAdapter);

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
