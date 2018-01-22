package com.shadtaxi.shadtaxi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.Vehicle;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.util.List;
import java.util.Random;

/**
 * Created by dennis on 20/01/18.
 */

public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.ViewHolder> {
    private List<Vehicle> listVehicles;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TxtSemiBold txtVehicleNumber, txtVehicleModel, txtVehicleCapacity, txtActivateVehicle;
        private View eventView;

        public ViewHolder(View v) {
            super(v);
            txtVehicleNumber = (TxtSemiBold) v.findViewById(R.id.txtVehicleNumber);
            txtVehicleModel = (TxtSemiBold) v.findViewById(R.id.txtVehicleModel);
            txtVehicleCapacity = (TxtSemiBold) v.findViewById(R.id.txtVehicleCapacity);
            txtActivateVehicle = (TxtSemiBold) v.findViewById(R.id.txtActivateVehicle);
            eventView = (View) v.findViewById(R.id.vehicleView);
        }
    }

    public VehicleListAdapter(Context context, List<Vehicle> listVehicles) {
        this.context = context;
        this.listVehicles = listVehicles;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txtVehicleNumber.setText(listVehicles.get(position).getNumber());
        holder.txtVehicleModel.setText(listVehicles.get(position).getModel());
        holder.txtVehicleCapacity.setText(listVehicles.get(position).getCapacity());

        holder.eventView.setBackgroundColor(generateRandomColor());

        holder.txtActivateVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listVehicles.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_vehicle_list_item, parent, false);
        return new ViewHolder(view);
    }

    private int generateRandomColor() {
        Random random = new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

}
