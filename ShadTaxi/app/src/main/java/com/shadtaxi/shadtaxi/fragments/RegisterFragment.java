package com.shadtaxi.shadtaxi.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.shadtaxi.shadtaxi.R;
import com.shadtaxi.shadtaxi.activities.DashboardActivity;
import com.shadtaxi.shadtaxi.adapters.CountriesAdapter;
import com.shadtaxi.shadtaxi.constants.Constants;
import com.shadtaxi.shadtaxi.data.Data;
import com.shadtaxi.shadtaxi.database.DatabaseHelper;
import com.shadtaxi.shadtaxi.models.Country;
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
public class RegisterFragment extends Fragment {
    private Data data;
    private PreferenceHelper preferenceHelper;
    private DatabaseHelper databaseHelper;
    private LinearLayout layoutSelectCountry;
    private ImageView imageViewSelectedCountry;
    private Edt edtFullName, edtEmail, edtMobileNumber, edtPassword, edtConfirmPassword;
    private CheckBox checkBoxTerms;
    private ProgressDialog progressDialog;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        data = new Data();
        preferenceHelper = new PreferenceHelper(getActivity());
        databaseHelper = new DatabaseHelper(getActivity());
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        final TxtSemiBold txtShowRegPassword = (TxtSemiBold) view.findViewById(R.id.txtShowRegistrationPassword);
        final TxtSemiBold txtShowRegConfirmPassword = (TxtSemiBold) view.findViewById(R.id.txtShowRegistrationConfirmPassword);
        Btn btnRegister = (Btn) view.findViewById(R.id.btnCreateAccount);
        edtFullName = (Edt) view.findViewById(R.id.edtRegFirstName);
        edtEmail = (Edt) view.findViewById(R.id.edtRegEmail);
        edtMobileNumber = (Edt) view.findViewById(R.id.edtRegMobileNumber);
        edtPassword = (Edt) view.findViewById(R.id.edtRegPassword);
        edtConfirmPassword = (Edt) view.findViewById(R.id.edtRegPasswordConfirm);
        checkBoxTerms = (CheckBox) view.findViewById(R.id.checkboxTerms);
        layoutSelectCountry = (LinearLayout) view.findViewById(R.id.layoutSelectCountry);
        imageViewSelectedCountry = (ImageView) layoutSelectCountry.findViewById(R.id.imageViewSelectedImage);

        layoutSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountriesDialog();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        txtShowRegConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtConfirmPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    //password is visible
                    edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txtShowRegConfirmPassword.setText(getResources().getString(R.string.string_hide_password));
                } else if (edtConfirmPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    //password is hidden
                    edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtShowRegConfirmPassword.setText(getResources().getString(R.string.string_show_password));
                }
            }
        });

        txtShowRegPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    //password is visible
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    txtShowRegPassword.setText(getResources().getString(R.string.string_hide_password));
                } else if (edtPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                    //password is hidden
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    txtShowRegPassword.setText(getResources().getString(R.string.string_show_password));
                }
            }
        });

        return view;
    }

    private void showCountriesDialog() {
        final AlertDialog dialogCountries = new AlertDialog.Builder(getActivity()).create();
        View view = getLayoutInflater().inflate(R.layout.layout_select_country_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.listViewCountries);
        Btn btnCancel = (Btn) view.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCountries.dismiss();
            }
        });

        final CountriesAdapter countriesAdapter = new CountriesAdapter(getActivity(), data.countryList());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogCountries.dismiss();
                Country country = data.countryList().get(i);
                Glide.with(getActivity()).load(country.getCountry_flag()).into(imageViewSelectedCountry);
            }
        });

        listView.setAdapter(countriesAdapter);

        dialogCountries.setView(view);
        dialogCountries.show();

    }

    private void register() {
        if (TextUtils.isEmpty(edtFullName.getText().toString())) {
            showErrorToast("Please enter your name");
        } else if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            showErrorToast("Please enter your email address");
        } else if (!isValidEmail(edtEmail.getText().toString())) {
            showErrorToast("Please enter a valid email address");
        } else if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            showErrorToast("Please enter password");
        } else if (TextUtils.isEmpty(edtConfirmPassword.getText().toString())) {
            showErrorToast("Please enter confirm password");
        } else if (!checkBoxTerms.isChecked()) {
            showErrorToast("Please accept our terms and conditions");
        } else {
            if (isOnline()) {
                User user = new User();
                user.setName(edtFullName.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                user.setPhone(edtMobileNumber.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                user.setPassword_confirmation(edtConfirmPassword.getText().toString());

                registerCommand(user);

            } else {
                showErrorToast("No network connection");
            }
        }
    }

    private void registerCommand(User user) {
        showProgressDialog("Creating account. Please wait...");
        AndroidNetworking.post(Constants.REGISTRATION_URL)
                .addBodyParameter(user)
                .setTag("registration")
                .addHeaders("Accept", "application/json")
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String access_token = jsonObject.getString("access_token");

                            if (!access_token.isEmpty()) {

                                getUserDetails();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        dismissProgressDialog();
                        String response_string = anError.getErrorBody();
                        Log.e("registration", "==" + response_string);

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


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dismissProgressDialog();

                        Intent intent_main = new Intent(getActivity(), DashboardActivity.class);
                        getActivity().startActivity(intent_main);
                        getActivity().finish();

                        preferenceHelper.putIsLoggedIn(true);
                    }

                    @Override
                    public void onError(ANError error) {
                        String response_string = error.getErrorBody();
                        Log.e("registration", "==" + response_string);

                        dismissProgressDialog();

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

    private boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
