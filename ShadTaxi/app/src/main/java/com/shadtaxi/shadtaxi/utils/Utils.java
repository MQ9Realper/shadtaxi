package com.shadtaxi.shadtaxi.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.activities.DashboardActivity;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Txt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dennismwebia on 9/6/17.
 */

public class Utils {
    private Activity context;
    private AppCompatActivity appCompatActivity;
    private AlertDialog dialogConfirm, dialogTimer;
    private Dialog dialogOnTrip, dialogReceipt;
    private ProgressDialog progressDialog;
    private TxtSemiBold txtTimer;

    public Utils(Activity activity, AppCompatActivity appCompatActivity) {
        this.context = activity;
        this.appCompatActivity = appCompatActivity;
    }

    public void centerToolbarTitle(@NonNull final Toolbar toolbar) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.LEFT);
            titleView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf"));
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            toolbar.requestLayout();
        }
    }

    public void initToolbar(Toolbar toolbar, String title, final Class<?> destination_class) {
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, destination_class);
                context.startActivity(intent);
                context.finish();
            }
        });
        centerToolbarTitle(toolbar);
        appCompatActivity.setSupportActionBar(toolbar);
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

    private void showDriverTimer(String driver_name, int driver_image) {
        dialogTimer = new AlertDialog.Builder(context).create();
        dialogTimer.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_arriving_driver, null);
        dialogTimer.setView(dialogView);
        dialogTimer.setCancelable(false);

        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtConfirmDriverName);
        Btn btnCancelBooking = (Btn) dialogView.findViewById(R.id.btnCancelBooking);

        btnCancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTimer.dismiss();
            }
        });

        cn.iwgang.countdownview.CountdownView countDownView = (cn.iwgang.countdownview.CountdownView) dialogView.findViewById(R.id.countDownView);

        for (int time = 0; time < 100000; time++) {
            countDownView.updateShow(time);
        }

        CircleImageView driverImage = (CircleImageView) dialogView.findViewById(R.id.imgConfirmDriverImage);

        Glide.with(context).load(driver_image).into(driverImage);
        txtDriverName.setText(driver_name);

        showOnTripDialogTimer(driver_name, driver_image);

        dialogTimer.show();
    }

    private void showOnTripDialog(String driver_name, int driver_image) {
        dialogOnTrip = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_on_trip, null);
        dialogOnTrip.setCancelable(false);
        dialogOnTrip.setContentView(dialogView);

        CircleImageView profileImage = (CircleImageView) dialogView.findViewById(R.id.imgOnTripImage);
        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtOnTripDriverName);

        Glide.with(context).load(driver_image).into(profileImage);
        txtDriverName.setText(driver_name);

        showTripReceiptTimer();

        dialogOnTrip.show();

    }

    private void showReceiptDialog() {
        dialogReceipt = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_trip_receipt, null);
        dialogReceipt.setCancelable(false);
        dialogReceipt.setContentView(dialogView);

        CircleImageView profileImage = (CircleImageView) dialogView.findViewById(R.id.imgOnTripImage);
        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtOnTripDriverName);
        Btn btnProceed = (Btn) dialogView.findViewById(R.id.btnReceiptProceed);

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Thank You!")
                        .setContentText("Thank you for riding with Safiree!")
                        .setConfirmText("Okay")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Intent intent = new Intent(context, DashboardActivity.class);
                                context.startActivity(intent);
                                context.finish();
                            }
                        })
                        .show();
            }
        });

        //Glide.with(context).load(driver_image).into(profileImage);
        //txtDriverName.setText(driver_name);

        dialogReceipt.show();
    }

    private void showOnTripDialogTimer(final String driver_name, final int driver_image) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showOnTripDialog(driver_name, driver_image);
            }
        }, 4000);
    }

    private void showTripReceiptTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showReceiptDialog();
            }
        }, 4000);
    }

    public long roundOff(double input) {
        return Math.round(input);
    }

    public void showSuccessToast(String message) {
        StyleableToast styleableToast = new StyleableToast
                .Builder(context)
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.parseColor("#2cb742"))
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }

    }

    public void showErrorToast(String message) {
        StyleableToast styleableToast = new StyleableToast
                .Builder(context)
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.RED)
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }
    }

    public void showProgressToast(String message){
        StyleableToast styleableToast = new StyleableToast
                .Builder(context)
                .duration(Toast.LENGTH_LONG)
                .icon(R.drawable.ic_autorenew_white_24dp)
                .spinIcon()
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(context.getResources().getColor(R.color.colorSecondary))
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }
    }

    public void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public String getFilePath(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
