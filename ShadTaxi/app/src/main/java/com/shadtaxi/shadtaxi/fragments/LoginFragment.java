package com.shadtaxi.shadtaxi.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.activities.DashboardActivity;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.User;
import com.shadtaxi.shadtaxi.utils.PreferenceHelper;
import com.shadtaxi.shadtaxi.views.Btn;
import com.shadtaxi.shadtaxi.views.Edt;
import com.shadtaxi.shadtaxi.views.TxtSemiBold;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private Edt edtLoginMobileNumber, edtLoginPassword;
    private ProgressDialog progressDialog;
    private PreferenceHelper preferenceHelper;
    private DatabaseHelper databaseHelper;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        databaseHelper = new DatabaseHelper(getActivity());
        preferenceHelper = new PreferenceHelper(getActivity());
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TxtSemiBold txtTooglePassword = (TxtSemiBold) view.findViewById(R.id.txtShowPassword);
        edtLoginMobileNumber = (Edt) view.findViewById(R.id.edtLoginMobile);
        edtLoginPassword = (Edt) view.findViewById(R.id.edtLoginPassword);
        Btn btnSignIn = (Btn) view.findViewById(R.id.btnCompleteSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        txtTooglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtLoginPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    //password is visible
                    edtLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txtTooglePassword.setText(getResources().getString(R.string.string_hide_password));
                } else if (edtLoginPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    //password is hidden
                    edtLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtTooglePassword.setText(getResources().getString(R.string.string_show_password));
                }
            }
        });
    }

    private void login() {
        if (TextUtils.isEmpty(edtLoginMobileNumber.getText().toString())) {
            showErrorToast("Please enter your mobile number");
        } else if (TextUtils.isEmpty(edtLoginPassword.getText().toString())) {
            showErrorToast("Please enter password");
        } else {
            if (isOnline()) {
                loginCommand(edtLoginMobileNumber.getText().toString(), edtLoginPassword.getText().toString());
            } else {
                showErrorToast("No network connection");
            }
        }
    }

    public void loginCommand(String username, final String password) {
        showProgressDialog("Logging in. Please wait...");
        AndroidNetworking.post(Constants.LOGIN_URL)
                .addBodyParameter("username", username)
                .addBodyParameter("password", password)
                .addBodyParameter("grant_type", "password")
                .setTag("login")
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

                            //set preference helper
                            preferenceHelper.putIsLoggedIn(true);

                            getUserDetails();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        dismissProgressDialog();
                        String response_string = anError.getErrorBody();
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

    private void getUserDetails() {
        String token = preferenceHelper.getAccessToken();
        AndroidNetworking.get(Constants.GET_USER)
                .addHeaders("Authorization", "Bearer " + token)
                .setTag("userDetails")
                .setPriority(Priority.LOW)
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

                            databaseHelper.addUser(user);
                            preferenceHelper.putUserDetails(id, name, phone, image, isRider, isDriver, profile, email);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dismissProgressDialog();

                        Intent intent_main = new Intent(getActivity(), DashboardActivity.class);
                        getActivity().startActivity(intent_main);
                        getActivity().finish();
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

    private void showErrorToast(String message) {
        StyleableToast styleableToast = new StyleableToast
                .Builder(getActivity())
                .duration(Toast.LENGTH_LONG)
                .text(message)
                .textColor(Color.WHITE)
                .typeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Medium.ttf"))
                .backgroundColor(Color.RED)
                .build();

        if (styleableToast != null) {
            styleableToast.show();
            styleableToast = null;
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
