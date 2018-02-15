package com.shadtaxi.shadtaxi.adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.models.NearestDriver;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dennis on 11/8/17.
 */

public class NearestDriversAdapter extends RecyclerView.Adapter<NearestDriversAdapter.ViewHolder> {
    private Utils utils;
    private PreferenceHelper preferenceHelper;
    private Activity context;
    private AppCompatActivity activity;
    private List<NearestDriver> driverList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TxtSemiBold txtDriverName,txtDriverDistance;
        private CircleImageView imageDriver;
        private AppCompatRatingBar ratingBar;
        private Btn btnBook;

        public ViewHolder(View view) {
            super(view);
            txtDriverName = (TxtSemiBold) view.findViewById(R.id.txtDriverName);
            txtDriverDistance = (TxtSemiBold) view.findViewById(R.id.txtDriverDistance);
            imageDriver = (CircleImageView) view.findViewById(R.id.imgDriverImage);
            ratingBar = (AppCompatRatingBar) view.findViewById(R.id.ratingDriverRating);
            btnBook = (Btn) view.findViewById(R.id.btnDriverBook);
        }
    }

    public NearestDriversAdapter(Activity context1, AppCompatActivity activity, ArrayList<NearestDriver> driverArrayList) {
        this.context = context1;
        this.activity = activity;
        this.driverList = driverArrayList;
        this.utils = new Utils(context1, activity);
        this.preferenceHelper = new PreferenceHelper(context1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_available_rides_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtDriverName.setText(driverList.get(position).getName());
        holder.txtDriverDistance.setText(driverList.get(position).getDistance() + " away");
        holder.ratingBar.setRating(driverList.get(position).getRating());
        Glide.with(context).load(driverList.get(position).getImage_url()).into(holder.imageDriver);

        showConfirmation(holder, position);
    }

    private void showConfirmation(ViewHolder viewHolder, final int position) {
        viewHolder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.showConfirmationDialog(driverList.get(position).getEmail(), driverList.get(position).getId(), preferenceHelper.getPickUpAddress(), preferenceHelper.getDropOffAddress(), driverList.get(position).getName(), driverList.get(position).getDistance(), driverList.get(position).getRating(), driverList.get(position).getImage_url());
            }
        });
    }

}
