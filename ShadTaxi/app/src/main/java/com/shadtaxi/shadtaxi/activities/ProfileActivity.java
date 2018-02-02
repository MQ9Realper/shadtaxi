package com.shadtaxi.shadtaxi.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
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
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Utils utils;
    private DatabaseHelper databaseHelper;
    private PreferenceHelper preferenceHelper;
    public static final int PERMISSIONS_REQUEST_CODE = 0x4;
    public static final int PROFILE_REQUEST_CODE = 0x5;
    private File file;
    private CircleImageView profileImageview;
    private TxtSemiBold txtProfileName;
    private TxtLight txtProfilePhoneNumber;
    private Edt edtProfileName, edtProfileEmail, edtRegMobileNumber;
    private Btn btnUpdateProfile, btnChangePhoto;

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
        btnChangePhoto = (Btn) findViewById(R.id.btnChangePhoto);

        btnUpdateProfile.setOnClickListener(this);
        btnChangePhoto.setOnClickListener(this);

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

    private void updatePhoto(File photo, String token) {
        utils.showProgressDialog("Uploading photo...");
        AndroidNetworking.upload(Constants.UPDATE_PHOTO)
                .addHeaders("Authorization", "Bearer " + token)
                .addMultipartFile("photo", photo)
                .setTag("uploadPhoto")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
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

                            Picasso.with(ProfileActivity.this).load(user.getImage()).into(DashboardActivity.profileImage);

                            utils.dismissProgressDialog();

                            utils.showSuccessToast("Photo has been uploaded!");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
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
        switch (v.getId()) {
            case R.id.btnUpdateProfile:
                updateProfile(edtProfileName.getText().toString(), edtRegMobileNumber.getText().toString());
                break;
            case R.id.btnChangePhoto:
                checkPermissionsAndOpenFilePicker();
                break;
            default:
                break;
        }
    }

    private void checkPermissionsAndOpenFilePicker() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                utils.showErrorToast("Allow external storage to be read.");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFilePicker();
                } else {
                    utils.showErrorToast("Allow external storage to be read.");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case PROFILE_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        List<Uri> path = Matisse.obtainResult(data);
                        file = new File(utils.getFilePath(path.get(0)));
                        Picasso.with(this).load(path.get(0)).into(profileImageview);
                        updatePhoto(file, preferenceHelper.getAccessToken());
                        break;
                }
                break;

            default:
                break;
        }
    }

    private void openFilePicker() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(PROFILE_REQUEST_CODE);
    }
}
