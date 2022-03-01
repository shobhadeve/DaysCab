package com.dayscab.common.activties;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityEmergencyContactBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyContactAct extends AppCompatActivity {

    Context mContext = EmergencyContactAct.this;
    ActivityEmergencyContactBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_emergency_contact);

        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        getProfileNew();
        itit();
    }

    private void itit() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLocation = location;
                        }
                    }
                });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSave.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_name_text));
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_email_text));
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.invalid_email));
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_email_text));
            } else if (!ProjectUtil.isValidPhoneNumber(binding.etPhone.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.invalid_phone));
            } else {
                savedetails();
            }
        });

        startLocationUpdates();

    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(EmergencyContactAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(EmergencyContactAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EmergencyContactAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(EmergencyContactAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            if (TextUtils.isEmpty(binding.etCurrentAddress.getText().toString().trim())) {
                                binding.etCurrentAddress.setText(
                                        ProjectUtil.getCompleteAddressString(mContext,
                                                currentLocation.getLatitude(),
                                                currentLocation.getLongitude()));
                            }
                        }
                    }
                }, Looper.myLooper());
    }

    private void savedetails() {

        HashMap<String, String> map = new HashMap<>();

        map.put("user_id", modelLogin.getResult().getId());
        map.put("emergency_name", binding.etName.getText().toString().trim());
        map.put("emergency_mobile", binding.etPhone.getText().toString().trim());
        map.put("emergency_email", binding.etEmail.getText().toString().trim());

        Log.e("managerSoundApi", "managerSoundApi = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateEmergencyDetailsApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "managerSoundApi = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").equals("2")) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });
    }

    private void getProfileNew() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getProfileCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            if(modelLogin.getResult().getEmergency_name() == null ||
                                    modelLogin.getResult().getEmergency_name().equals("")) {
                                if (modelLogin.getResult().getUser_name() == null ||
                                        modelLogin.getResult().getUser_name().equals("")) {
                                    binding.etName.setText(modelLogin.getResult().getFirst_name() + " " + modelLogin.getResult().getLast_name());
                                } else {
                                    binding.etName.setText(modelLogin.getResult().getUser_name());
                                }

                                binding.etEmail.setText(modelLogin.getResult().getEmail());
                                binding.etPhone.setText(modelLogin.getResult().getMobile());
                            } else {
                                binding.etName.setText(modelLogin.getResult().getEmergency_name());
                                binding.etEmail.setText(modelLogin.getResult().getEmergency_email());
                                binding.etPhone.setText(modelLogin.getResult().getEmergency_mobile());
                            }

                            Log.e("getProfileResponse", "response = " + response);
                            Log.e("getProfileResponse", "response = " + stringResponse);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }


}

