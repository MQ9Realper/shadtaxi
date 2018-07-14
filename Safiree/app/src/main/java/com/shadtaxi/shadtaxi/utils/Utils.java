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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.util.HttpAuthorizer;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.activities.DashboardActivity;
import com.shadtaxi.shadtaxi.activities.NearestDriversActivity;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Txt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private PreferenceHelper preferenceHelper;
    private TxtSemiBold txtTimer;

    public Utils(Activity activity, AppCompatActivity appCompatActivity) {
        this.context = activity;
        this.appCompatActivity = appCompatActivity;
        this.preferenceHelper = new PreferenceHelper(activity);
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

    public void showConfirmationDialog(final String driver_email, final int driver_id, String pick_up, String drop_off, final String driver_name, String driver_distance, float driver_rating, final String driver_image) {
        dialogConfirm = new AlertDialog.Builder(context).create();
        dialogConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_confirm_booking, null);
        dialogConfirm.setView(dialogView);
        dialogConfirm.setCancelable(false);

        TxtSemiBold txtDriverDistance = (TxtSemiBold) dialogView.findViewById(R.id.txtConfirmDriverDistance);
        Txt txtConfirmPickUp = (Txt) dialogView.findViewById(R.id.txtConfirmPickUp);
        Txt txtConfirmDropOff = (Txt) dialogView.findViewById(R.id.txtConfirmDropOff);
        TxtSemiBold txtDriverName = (TxtSemiBold) dialogView.findViewById(R.id.txtConfirmDriverName);
        AppCompatRatingBar ratingDriver = (AppCompatRatingBar) dialogView.findViewById(R.id.ratingConfirmDriverRating);
        CircleImageView driverImage = (CircleImageView) dialogView.findViewById(R.id.imgConfirmDriverImage);

        txtConfirmPickUp.setText(pick_up);
        txtConfirmDropOff.setText(drop_off);
        txtDriverDistance.setText(driver_distance + " away");
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
                createRequest(driver_id, driver_email);

              /*  new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        if (dialogConfirm.isShowing()) {
                            dialogConfirm.dismiss();
                        }

                        showDriverTimer(driver_name, driver_image);
                    }
                }, 4000);*/

            }
        });

        dialogConfirm.show();
    }

    private void showDriverTimer(String driver_name, String driver_image) {
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

    private void showOnTripDialog(String driver_name, String driver_image) {
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

    private void showOnTripDialogTimer(final String driver_name, final String driver_image) {
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

    public void showProgressToast(String message) {
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

    /**
     * Show SnackBar
     */
    public void showSnackBar(View container, final String text) {
        Snackbar snackbar = Snackbar.make(container, text, Snackbar.LENGTH_LONG);
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        textView.setTextColor(context.getResources().getColor(R.color.colorWhite));
        textView.setTypeface(typeface);
        snackbar.show();
    }

    /**
     * Create Request
     */
    private void createRequest(int driver_id, final String driver_email) {
        showProgressDialog("Booking driver...");
        AndroidNetworking.post(Constants.CREATE_REQUEST)
                .addHeaders("Authorization", "Bearer " + preferenceHelper.getAccessToken())
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("Accept", "application/json")
                .addBodyParameter("latlong", preferenceHelper.getCurrentLocation())
                .addBodyParameter("location", preferenceHelper.getPickUpAddress())
                .addBodyParameter("driver", String.valueOf(driver_id))
                .addBodyParameter("city", String.valueOf(preferenceHelper.getCityId()))
                .setTag("createRequest")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    public void onResponse(String response) {
                        dismissProgressDialog();
                        if (response.contains("success")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                showErrorToast(WordUtils.capitalizeFully(jsonObject1.getString("message")));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                String tripId = jsonObject1.getString("id");
                                subscribeToPusher(tripId);

                                if (dialogConfirm.isShowing()) {
                                    dialogConfirm.dismiss();
                                }

                                showProgressDialog("Waiting for driver accept...");

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                            // Display waiting for driver to accept dialog
                            Log.e("createRequest::", response);

                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        dismissProgressDialog();
                        String response_string = error.getErrorBody();
                        if (response_string != null) {
                            if (response_string.contains("data")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    showErrorToast(jsonObject1.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    showErrorToast(jsonObject.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else {
                            showErrorToast("Internet is not available, please try again!");
                        }
                    }
                });
    }

    private void subscribeToPusher(String trip_id) {
        Map<String, String> mapHeaders = new HashMap<>();
        mapHeaders.put("Authorization", "Bearer " + preferenceHelper.getAccessToken());
        HttpAuthorizer authorizer = new HttpAuthorizer(Constants.PUSHER_AUTHENTICATOR);
        authorizer.setHeaders(mapHeaders);

        PusherOptions pusherOptions = new PusherOptions().setAuthorizer(authorizer);
        pusherOptions.setCluster(Constants.PUSHER_APP_CLUSTER);

        Pusher pusher = new Pusher(Constants.PUSHER_APP_KEY, pusherOptions);

        PrivateChannel channel = pusher.subscribePrivate("private-Trips-" + trip_id, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String s, Exception e) {
                Log.e("auth::", "Authentication failed!!" + s);
            }

            @Override
            public void onSubscriptionSucceeded(String s) {
                Log.e("auth::", "Authentication succeeded!!" + s);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Log.e("pusher","EventName::" + eventName);
                if (eventName.contains("TripAccepted")){
                    Log.e("pusher","EventName::" + data);
                }

            }
        },"App\\Events\\TripAccepted", "App\\Events\\TripArrived","App\\Events\\TripStart","App\\Events\\TripEnd","App\\Events\\TripPaid");

        pusher.connect();

//        channel.bind("App\\Events\\TripAccepted", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataAccepted::", data);
//                processEventData(data);
//            }
//        });
//
//        channel.bind("App\\Events\\TripRiderCancel", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataRiderCancel::", data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripCancelled", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataCancelled::", data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripRejected", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataRejected::", data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripArrived", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataArrived::", data);
//                processEventData(data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripStart", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataStart::", data);
//                //showOnTripDialog();
//                processEventData(data);
//            }
//        });
//
//        channel.bind("App\\Events\\TripEnd", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataEnd::", data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripPaid", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataPaid::", data);
//            }
//        });
//
//        channel.bind("App\\Events\\TripRiderRate", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataRiderRate::", data);
//
//            }
//        });
//
//        channel.bind("App\\Events\\TripDriverRate", new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                Log.e("dataDriverRate::", data);
//
//            }
//        });
    }

    private void processEventData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject tripJSON = jsonObject.getJSONObject("trip");
            String status = tripJSON.getString("status");

            showSuccessToast(status);
            switch (status) {
                case "ACCEPTED":
                    dismissProgressDialog();
                    showDriverTimer("Maurice", ""); // Also include time
                    Log.e("status", "=== " + status);
                    break;
                case "ARRIVED":
                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                    break;
                case "STARTED":
                    break;
                case "ENDED":
                    break;
                case "PAID":
                    break;
                case "RATED":
                    break;
                default:
                    break;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
