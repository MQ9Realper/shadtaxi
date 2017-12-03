package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ListView;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.AvailableDriversAdapter;
import com.shadtaxi.shadtaxi.adapters.HistoryAdapter;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.utils.UniversalUtils;
import com.shadtaxi.shadtaxi.views.Edt;

public class HistoryActivity extends AppCompatActivity {
    private UniversalUtils universalUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        universalUtils = new UniversalUtils(this);

        InitToolbar("My Trips");

        initHistoryList();
    }
    private void InitToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarHistory);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        universalUtils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }

    private void initHistoryList() {
        Data data = new Data();
        final HistoryAdapter ridesAdapter = new HistoryAdapter(this, data.historyArrayList());
        ListView listView = (ListView) findViewById(R.id.listViewHistory);
        listView.setAdapter(ridesAdapter);
    }

}
