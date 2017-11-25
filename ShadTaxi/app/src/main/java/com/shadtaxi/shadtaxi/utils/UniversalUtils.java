package com.shadtaxi.shadtaxi.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Txt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dennismwebia on 9/6/17.
 */

public class UniversalUtils {
    private Context context = null;
    private AlertDialog dialogConfirm, dialogTimer;
    private Handler customHandler = new Handler();
    private TxtSemiBold txtTimer;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;


    public UniversalUtils(Context context) {
        this.context = context;
    }

    public void centerToolbarTitle(@NonNull final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.LEFT);
            titleView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Muli-Light.ttf"));
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
        }
    }

    public void showConfirmationDialog(String pick_up, String drop_off, final String driver_name, String driver_distance, float driver_rating, final int driver_image) {
        dialogConfirm = new AlertDialog.Builder(context).create();
        dialogConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_confirm_booking, null);
        dialogConfirm.setView(dialogView);
        dialogConfirm.setCancelable(false);

        Txt txtDriverDistance = (Txt) dialogView.findViewById(R.id.txtConfirmDriverDistance);
        Txt txtConfirmPickUp = (Txt) dialogView.findViewById(R.id.txtConfirmPickUp);
        Txt txtConfirmDropOff = (Txt) dialogView.findViewById(R.id.txtConfirmDropOff);
        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtConfirmDriverName);
        AppCompatRatingBar ratingDriver = (AppCompatRatingBar) dialogView.findViewById(R.id.ratingConfirmDriverRating);
        CircleImageView driverImage = (CircleImageView) dialogView.findViewById(R.id.imgConfirmDriverImage);

        txtConfirmPickUp.setText(pick_up);
        txtConfirmDropOff.setText(drop_off);
        txtDriverDistance.setText(driver_distance);
        txtDriverName.setText(driver_name);
        ratingDriver.setRating(driver_rating);
        Glide.with(context).load(driver_image).into(driverImage);

        Btn btnCancel = (Btn) dialogView.findViewById(R.id.btnConfirmCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogConfirm.isShowing()) {
                    dialogConfirm.dismiss();
                }
            }
        });

        Btn btnBook = (Btn) dialogView.findViewById(R.id.btnConfirmBook);
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Booking driver. Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (dialogConfirm.isShowing()) {
                            dialogConfirm.dismiss();
                        }

                        showDriverTimer(driver_name, driver_image);
                    }
                }, 4000);

            }
        });

        dialogConfirm.show();
    }

    private void showDriverTimer(String driver_name,int driver_image){
        dialogTimer = new AlertDialog.Builder(context).create();
        dialogTimer.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_arriving_driver, null);
        dialogTimer.setView(dialogView);
        dialogTimer.setCancelable(false);

        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtConfirmDriverName);
        txtTimer = (TxtSemiBold) dialogView.findViewById(R.id.txtTimer);
        CircleImageView driverImage = (CircleImageView) dialogView.findViewById(R.id.imgConfirmDriverImage);

        Glide.with(context).load(driver_image).into(driverImage);
        txtDriverName.setText(driver_name);

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        dialogTimer.show();
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);

            txtTimer.setText("" + mins + ":"

                            + String.format("%02d", secs) + ":"

                            + String.format("%03d", milliseconds));

            customHandler.postDelayed(this, 0);

        }

    };

}
