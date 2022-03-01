package com.dayscab.user.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.dayscab.R;
import com.dayscab.databinding.ActivityAvailablePoolDriversBinding;
import com.dayscab.driver.adapters.AdapterOfferPool;
import com.dayscab.driver.models.ModelPoolList;
import com.dayscab.user.adapters.AdapterOfferPoolUser;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvailablePoolDriversAct extends AppCompatActivity {

    Context mContext = AvailablePoolDriversAct.this;
    ActivityAvailablePoolDriversBinding binding;
    private Location currentLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 3000;  /* 5 secs */
    private long FASTEST_INTERVAL = 3000; /* 2 sec */
    private boolean isApiCalled;
    String lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_available_pool_drivers);
        MyApplication.checkToken(mContext);
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        Log.e("latlatlat", "lat = " + lat + "  lon = " + lon);
        startLocationUpdates();
        itit();
    }

    private void itit() {

        getPoolOffers();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPoolOffers();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getPoolOffers() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();

        try {
            map.put("lat", lat);
            map.put("lon", lon);
        } catch (Exception e) {
            map.put("lat", "");
            map.put("lon", "");
        }

        Log.e("asdasdasdas", "mapmap = " + map);

        Call<ResponseBody> call = api.getAvailablePoolDriver(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        ModelPoolList modelPoolList = new Gson().fromJson(stringResponse, ModelPoolList.class);
                        AdapterOfferPoolUser adapterOfferPool = new AdapterOfferPoolUser(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        AdapterOfferPoolUser adapterOfferPool = new AdapterOfferPoolUser(mContext, null);
                        binding.rvPools.setAdapter(adapterOfferPool);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(AvailablePoolDriversAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(AvailablePoolDriversAct.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AvailablePoolDriversAct.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(AvailablePoolDriversAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                        }
                    }
                }, Looper.myLooper());

    }

}