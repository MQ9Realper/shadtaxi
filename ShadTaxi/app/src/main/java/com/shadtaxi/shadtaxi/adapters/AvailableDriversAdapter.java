package com.shadtaxi.shadtaxi.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.activities.BookDriverActivity;
import com.shadtaxi.shadtaxi.models.AvailableDriver;
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
 * Created by dennis on 11/8/17.
 */

public class AvailableDriversAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private UniversalUtils universalUtils;
    private PreferenceHelper preferenceHelper;
    private Activity context;
    private List<AvailableDriver> originalDriverList = null;
    private List<AvailableDriver> filteredDriverList;
    private LayoutInflater layoutInflater;
    private Filter filter;
    private int selectedPosition = 0;

    public AvailableDriversAdapter(Activity context1, ArrayList<AvailableDriver> driverArrayList) {
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
        private Txt txtDriverDistance;
        private CircleImageView imageDriver;
        private AppCompatRatingBar ratingBar;
        private Btn btnCall, btnBook;
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
            convertView = layoutInflater.inflate(R.layout.layout_rides_list_item, parent, false);
            viewHolder.txtDriverName = (TxtSemiBold) convertView.findViewById(R.id.txtDriverName);
            viewHolder.txtDriverDistance = (Txt) convertView.findViewById(R.id.txtDriverDistance);
            viewHolder.imageDriver = (CircleImageView) convertView.findViewById(R.id.imgDriverImage);
            viewHolder.ratingBar = (AppCompatRatingBar) convertView.findViewById(R.id.ratingDriverRating);
            viewHolder.btnCall = (Btn) convertView.findViewById(R.id.btnDriverCall);
            viewHolder.btnBook = (Btn) convertView.findViewById(R.id.btnDriverBook);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ratingBar.setRating(originalDriverList.get(position).getDriver_rating());
        viewHolder.txtDriverName.setText(originalDriverList.get(position).getDriver_name());
        viewHolder.txtDriverDistance.setText(originalDriverList.get(position).getDriver_distance());
        Glide.with(context).load(originalDriverList.get(position).getDriver_image()).into(viewHolder.imageDriver);

        /** click listeners */
        viewHolder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                universalUtils.showConfirmationDialog(preferenceHelper.getPickUpAddress(), preferenceHelper.getDropOffAddress(), originalDriverList.get(position).getDriver_name(),originalDriverList.get(position).getDriver_distance(),originalDriverList.get(position).getDriver_rating(),originalDriverList.get(position).getDriver_image());
            }
        });
        return convertView;
    }

    public void refresh(List<AvailableDriver> rideList) {
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
            for (AvailableDriver availableDriver : filteredDriverList) {
                if (availableDriver.getDriver_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    originalDriverList.add(availableDriver);
                }
            }
        }
        notifyDataSetChanged();
    }
}
