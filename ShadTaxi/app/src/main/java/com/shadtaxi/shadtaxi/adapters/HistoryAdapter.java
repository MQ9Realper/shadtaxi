package com.shadtaxi.shadtaxi.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.AvailableDriver;
import com.shadtaxi.shadtaxi.models.History;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.UniversalUtils;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Txt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dennis on 11/25/17.
 */

public class HistoryAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private UniversalUtils universalUtils;
    private PreferenceHelper preferenceHelper;
    private Activity context;
    private List<History> originalDriverList = null;
    private List<History> filteredDriverList;
    private LayoutInflater layoutInflater;
    private Filter filter;
    private int selectedPosition = 0;

    public HistoryAdapter(Activity context1, ArrayList<History> driverArrayList) {
        this.context = context1;
        this.originalDriverList = driverArrayList;
        this.layoutInflater = LayoutInflater.from(context1);
        this.filteredDriverList = new ArrayList<>();
        this.filteredDriverList.addAll(originalDriverList);
        this.universalUtils = new UniversalUtils(context1);
        this.preferenceHelper = new PreferenceHelper(context1);
    }

    private static class ViewHolder {
        private TxtSemiBold txtDriverName;
        private Txt txtRideCost, txtRideDate,txtDropOff,txtPickUp;
        private CircleImageView imageDriver;
        private AppCompatRatingBar ratingBar;
    }

    @Override
    public int getCount() {
        return originalDriverList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        selectedPosition = position;
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater layoutInflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_history_item, parent, false);
            viewHolder.txtDriverName = (TxtSemiBold) convertView.findViewById(R.id.txtDriverName);
            viewHolder.txtRideDate = (Txt) convertView.findViewById(R.id.txtRideDate);
            viewHolder.txtRideCost = (Txt) convertView.findViewById(R.id.txtRideCost);
            viewHolder.txtDropOff = (Txt) convertView.findViewById(R.id.txtConfirmDropOff);
            viewHolder.txtPickUp = (Txt) convertView.findViewById(R.id.txtConfirmPickUp);
            viewHolder.imageDriver = (CircleImageView) convertView.findViewById(R.id.imgDriverImage);
            viewHolder.ratingBar = (AppCompatRatingBar) convertView.findViewById(R.id.ratingDriverRating);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ratingBar.setRating(originalDriverList.get(position).getDriver_rating());
        viewHolder.txtDriverName.setText(originalDriverList.get(position).getDriver_name());
        viewHolder.txtRideDate.setText(originalDriverList.get(position).getRide_date());
        viewHolder.txtRideCost.setText(originalDriverList.get(position).getRide_cost());
        viewHolder.txtDropOff.setText(originalDriverList.get(position).getDestination());
        viewHolder.txtPickUp.setText(originalDriverList.get(position).getOrigin());
        Glide.with(context).load(originalDriverList.get(position).getDriver_image()).into(viewHolder.imageDriver);

        return convertView;
    }

    public void refresh(List<History> rideList) {
        rideList.clear();
        rideList.addAll(rideList);
        notifyDataSetChanged();
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        originalDriverList.clear();
        if (charText.length() == 0) {
            originalDriverList.addAll(filteredDriverList);
        } else {
            for (History availableDriver : filteredDriverList) {
                if (availableDriver.getDriver_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    originalDriverList.add(availableDriver);
                }
            }
        }
        notifyDataSetChanged();
    }
}
