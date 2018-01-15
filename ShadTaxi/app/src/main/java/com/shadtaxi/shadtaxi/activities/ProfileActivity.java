package com.shadtaxi.shadtaxi.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.utils.Utils;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Edt;
import com.shadtaxi.shadtaxi.views.TxtLight;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Utils utils;
    private DatabaseHelper databaseHelper;
    private PreferenceHelper preferenceHelper;

    private CircleImageView profileImageview;
    private TxtSemiBold txtProfileName;
    private TxtLight txtProfilePhoneNumber;
    private Edt edtProfileName, edtProfileEmail, edtRegMobileNumber;
    private Btn btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        utils = new Utils(this, this);
        databaseHelper = new DatabaseHelper(this);
        preferenceHelper = new PreferenceHelper(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();

        InitToolbar("Profile");

        getProfileInfo();
    }

    private void InitToolbar(String name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        utils.centerToolbarTitle(toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViews() {
        profileImageview = (CircleImageView) findViewById(R.id.profile_image);
        txtProfileName = (TxtSemiBold) findViewById(R.id.txtProfileName);
        txtProfilePhoneNumber = (TxtLight) findViewById(R.id.txtProfilePhoneNumber);
        edtProfileName = (Edt) findViewById(R.id.edtProfileName);
        edtProfileEmail = (Edt) findViewById(R.id.edtProfileEmail);
        edtRegMobileNumber = (Edt) findViewById(R.id.edtRegMobileNumber);
        btnUpdateProfile = (Btn) findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(this);

    }

    private void getProfileInfo() {
        ArrayList<User> user = databaseHelper.getAllUsers();
        txtProfileName.setText(user.get(0).getName().toUpperCase(Locale.ENGLISH));
        txtProfilePhoneNumber.setText(user.get(0).getEmail());
        edtProfileName.setText(user.get(0).getName());
        edtProfileEmail.setText(user.get(0).getPhone());
        edtRegMobileNumber.setText(user.get(0).getEmail());

        if (!user.get(0).getImage().isEmpty()) {
            Glide.with(this).load(user.get(0).getImage()).into(profileImageview);
        } else {
            Glide.with(this).load(R.drawable.default_image).into(profileImageview);
        }
    }

    private void updateProfile(String name, String phone) {
        utils.showProgressDialog("Updating profile...");
        AndroidNetworking.post(Constants.UPDATE_PROFILE)
                .addHeaders("Authorization", "Bearer " + preferenceHelper.getAccessToken())
                .addBodyParameter("name", name)
                .addBodyParameter("phone", phone)
                .setTag("update")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonArray = new JSONObject(response);
                            JSONObject jsonObject = jsonArray.getJSONObject("data");

                            final String id = jsonObject.getString("id");
                            final String name = jsonObject.getString("name");
                            final String email = jsonObject.getString("email");
                            final String phone = jsonObject.getString("phone");
                            final String image = jsonObject.getString("image");
                            final String isRider = jsonObject.getString("isRider");
                            final String isDriver = jsonObject.getString("isDriver");
                            final String profile = jsonObject.getString("profile");

                            User user = new User();
                            user.setId(id);
                            user.setName(name);
                            user.setEmail(email);
                            user.setPhone(phone);
                            user.setImage(image);
                            user.setRider(isRider);
                            user.setDriver(isDriver);
                            user.setProfile(profile);

                            databaseHelper.updateUser(user);

                            refreshToken();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        utils.dismissProgressDialog();
                        String response_string = error.getErrorBody();
                        if (response_string != null) {
                            if (response_string.contains("data")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    utils.showErrorToast(jsonObject1.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    utils.showErrorToast(jsonObject.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else {
                            utils.showErrorToast("Internet is not available, please try again!");
                        }
                    }
                });
    }

    public void refreshToken() {
        AndroidNetworking.post(Constants.LOGIN_URL)
                .addBodyParameter("grant_type", "refresh_token")
                .addBodyParameter("refresh_token", preferenceHelper.getRefreshToken())
                .setTag("Refresh token")
                .setContentType("application/x-www-form-urlencoded")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access_token");
                            String refresh_token = jsonObject.getString("refresh_token");

                            preferenceHelper.putAccessToken(access_token);
                            preferenceHelper.putRefreshToken(refresh_token);

                            getProfileInfo();

                            utils.dismissProgressDialog();
                            utils.showSuccessToast("Profile has been updated!");

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        utils.dismissProgressDialog();
                        String response_string = anError.getErrorBody();
                        if (response_string != null) {
                            if (response_string.contains("data")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    utils.showErrorToast(jsonObject1.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response_string);
                                    utils.showErrorToast(jsonObject.getString("message"));

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } else {
                            utils.showErrorToast("Internet is not available, please try again!");
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpdateProfile:
                updateProfile(edtProfileName.getText().toString(), edtRegMobileNumber.getText().toString());
                break;
            default:
                break;
        }
    }
}
