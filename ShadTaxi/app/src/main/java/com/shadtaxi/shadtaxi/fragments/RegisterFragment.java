package com.shadtaxi.shadtaxi.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.adapters.CountriesAdapter;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.models.Country;
import com.shadtaxi.shadtaxi.views.Btn;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private Data data;
    private LinearLayout layoutSelectCountry;
    private ImageView imageViewSelectedCountry;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        data = new Data();
        layoutSelectCountry = (LinearLayout) view.findViewById(R.id.layoutSelectCountry);
        imageViewSelectedCountry = (ImageView) layoutSelectCountry.findViewById(R.id.imageViewSelectedImage);

        layoutSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountriesDialog();
            }
        });
        return view;
    }

    private void showCountriesDialog() {
        final AlertDialog dialogCountries = new AlertDialog.Builder(getActivity()).create();
        View view = getLayoutInflater().inflate(R.layout.layout_select_country_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.listViewCountries);
        Btn btnCancel = (Btn) view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCountries.dismiss();
            }
        });

        final CountriesAdapter countriesAdapter = new CountriesAdapter(getActivity(), data.countryList());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogCountries.dismiss();
                Country country = data.countryList().get(i);
                Glide.with(getActivity()).load(country.getCountry_flag()).into(imageViewSelectedCountry);
            }
        });

        listView.setAdapter(countriesAdapter);

        dialogCountries.setView(view);
        dialogCountries.show();

    }

}
