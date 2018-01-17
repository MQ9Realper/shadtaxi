package com.shadtaxi.shadtaxi.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dennis on 12/5/17.
 */

public class TabLayoutAdapter extends FragmentPagerAdapter {
    private String[] tab_titles;
    private int tab_quantity;
    private Fragment[] tab_fragments;

    public TabLayoutAdapter(FragmentManager fragmentManager, String[] tab_titles, int tab_quantity, Fragment[] tab_fragments){
        super(fragmentManager);
        this.tab_titles = tab_titles;
        this.tab_quantity = tab_quantity;
        this.tab_fragments = tab_fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return tab_fragments[position];
    }

    @Override
    public int getCount() {
        return tab_quantity;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tab_titles[position];
    }
}
