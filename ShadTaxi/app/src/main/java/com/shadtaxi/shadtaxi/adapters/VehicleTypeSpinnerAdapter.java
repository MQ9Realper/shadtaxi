package com.shadtaxi.shadtaxi.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.VehicleType;
import com.shadtaxi.shadtaxi.views.Txt;

import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

/**
 * Created by dennis on 1/18/18.
 */

public class VehicleTypeSpinnerAdapter extends ArrayAdapter<VehicleType> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<VehicleType> vehicleTypeList;
    private final int mResource;

    public VehicleTypeSpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                                     @NonNull List objects) {
        super(context, resource, 0, objects);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        vehicleTypeList = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view = mInflater.inflate(mResource, parent, false);
        Txt txtVehicleType = (Txt) view.findViewById(R.id.txtSpinnerItem);
        txtVehicleType.setText(WordUtils.capitalizeFully(vehicleTypeList.get(position).getName()));

        return view;
    }
}
