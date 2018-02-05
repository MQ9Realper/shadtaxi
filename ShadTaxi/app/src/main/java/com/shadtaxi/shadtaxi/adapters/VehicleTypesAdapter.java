package com.shadtaxi.shadtaxi.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.VehicleType;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;

/**
 * Created by dennis on 10/12/17.
 */

public class VehicleTypesAdapter extends RecyclerView.Adapter<VehicleTypesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<VehicleType> vehicleTypeArrayList;
    public ArrayList<Integer> selectedPositions = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TxtSemiBold txtVehicleType;
        private ImageView imageViewVehicleIcon;
        private LinearLayout layoutVehicleType;

        public ViewHolder(View v) {
            super(v);
            txtVehicleType = (TxtSemiBold) v.findViewById(R.id.txtVehicleType);
            imageViewVehicleIcon = (ImageView) v.findViewById(R.id.imageViewVehicleType);
            layoutVehicleType = (LinearLayout) v.findViewById(R.id.layoutVehicleType);
        }
    }

    public VehicleTypesAdapter(Context context, ArrayList<VehicleType> vehicleTypeArrayList) {
        this.context = context;
        this.vehicleTypeArrayList = vehicleTypeArrayList;
        this.selectedPositions.add(vehicleTypeArrayList.size() - 1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txtVehicleType.setText(WordUtils.capitalizeFully(vehicleTypeArrayList.get(position).getName()));

        if (!selectedPositions.contains(position)) {
            if (vehicleTypeArrayList.get(position).getName().contains("BODA")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.motorcycle);
            } else if (vehicleTypeArrayList.get(position).getName().contains("TUK")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.tuk_tuk);
            } else if (vehicleTypeArrayList.get(position).getName().contains("TAXI")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.car);
            }
            holder.txtVehicleType.setTextColor(context.getResources().getColor(R.color.colorBlack));
        } else {
            if (vehicleTypeArrayList.get(position).getName().contains("BODA")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.motorcycle_active);
            } else if (vehicleTypeArrayList.get(position).getName().contains("TUK")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.tuk_tuk_active);
            } else if (vehicleTypeArrayList.get(position).getName().contains("TAXI")) {
                holder.imageViewVehicleIcon.setImageResource(R.drawable.car_active);
            }
            holder.txtVehicleType.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        holder.layoutVehicleType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedPositions.contains(position)) {
                    selectedPositions.clear();
                    selectedPositions.add(position);
                    if (vehicleTypeArrayList.get(position).getName().contains("Boda")) {
                        holder.imageViewVehicleIcon.setImageResource(R.drawable.motorcycle_active);
                    } else if (vehicleTypeArrayList.get(position).getName().contains("Tuk")) {
                        holder.imageViewVehicleIcon.setImageResource(R.drawable.tuk_tuk_active);
                    } else if (vehicleTypeArrayList.get(position).getName().contains("Salon")) {
                        holder.imageViewVehicleIcon.setImageResource(R.drawable.car_active);
                    }
                    holder.txtVehicleType.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vehicle_types_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return vehicleTypeArrayList.size();
    }
}
