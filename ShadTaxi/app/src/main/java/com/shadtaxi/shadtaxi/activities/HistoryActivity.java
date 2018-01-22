package com.shadtaxi.shadtaxi.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.utils.Utils;

public class HistoryActivity extends AppCompatActivity {
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHistory);
        RecyclerView listView = (RecyclerView) findViewById(R.id.listViewHistory);
        LinearLayout layoutEmptyList = (LinearLayout) findViewById(R.id.layoutEmptyList);

        layoutEmptyList.setVisibility(View.VISIBLE);

        utils = new Utils(this, this);
        utils.initToolbar(toolbar, "My Trips", DashboardActivity.class);

    }

}
