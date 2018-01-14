package com.shadtaxi.shadtaxi.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.Country;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.views.Txt;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dennis on 21/09/17.
 */

public class CountriesAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private Activity context;
    private List<Country> originalCountryList = null;
    private List<Country> filteredCountryList;
    private LayoutInflater layoutInflater;
    private Filter filter;
    private PreferenceHelper preferenceHelper;

    public CountriesAdapter(Activity context1, ArrayList<Country> countryArrayList) {
        this.context = context1;
        this.originalCountryList = countryArrayList;
        this.layoutInflater = LayoutInflater.from(context1);
        this.filteredCountryList = new ArrayList<>();
        this.filteredCountryList.addAll(originalCountryList);
        this.preferenceHelper = new PreferenceHelper(context1);
    }

    private static class ViewHolder {
        private Txt txtCountryName, txtCountryCode;
        private ImageView imageCountryFlag;
        private LinearLayout layoutCountryList;
    }

    @Override
    public int getCount() {
        return originalCountryList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_country_list_item, parent, false);
            viewHolder.txtCountryName = (Txt) convertView.findViewById(R.id.txtCountryName);
            viewHolder.txtCountryCode = (Txt) convertView.findViewById(R.id.txtCountryCode);
            viewHolder.imageCountryFlag = (ImageView) convertView.findViewById(R.id.imageCountryFlag);
            viewHolder.layoutCountryList = (LinearLayout) convertView.findViewById(R.id.layoutCountryListItem);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtCountryName.setText(originalCountryList.get(position).getCountry_name());
        viewHolder.txtCountryCode.setText(originalCountryList.get(position).getCountry_code());
        Glide.with(context).load(originalCountryList.get(position).getCountry_flag()).into(viewHolder.imageCountryFlag);

        return convertView;
    }

    public View getViewHolder() {
        return viewHolder.layoutCountryList;
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        originalCountryList.clear();
        if (charText.length() == 0) {
            originalCountryList.addAll(filteredCountryList);
        } else {
            for (Country country : filteredCountryList) {
                if (country.getCountry_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    originalCountryList.add(country);
                }
            }
        }
        notifyDataSetChanged();
    }
}
