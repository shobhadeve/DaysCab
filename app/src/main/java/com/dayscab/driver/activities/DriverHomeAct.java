package com.dayscab.driver.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityDriverHomeBinding;
import com.dayscab.driver.dialogs.NewRequestDialogTaxiNew;
import com.dayscab.driver.models.ModelCarsType;
import com.dayscab.user.activities.RideHistoryAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.MusicManager;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.RequestDialogCallBackInterface;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeAct extends AppCompatActivity
        implements OnMapReadyCallback, RequestDialogCallBackInterface {

    Context mContext = DriverHomeAct.this;
    ActivityDriverHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isOnREsumeCalled = true;
    GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentLocationMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    private Location mLocation;
    SupportMapFragment mapFragment;
    private Location currentLocation;

    BroadcastReceiver JobStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("object") != null) {
                JSONObject object = null;
                try {
                    object = new JSONObject(intent.getStringExtra("object"));
                    if ("Pending".equals(object.getString("status"))) {
                        Log.e("DialogChala123", "BroadcastReceiver Dialog = " + object.getString("status"));
                        NewRequestDialogTaxiNew.getInstance().Request(DriverHomeAct.this, intent.getStringExtra("object"));
                    } else getCurrentBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (intent.getAction().equals("data_update_location")) {
                double lat = intent.getDoubleExtra("latitude", 0);
                double lng = intent.getDoubleExtra("longitude", 0);
                float bearing = intent.getFloatExtra("bearing", 0);
//                if (carMarker != null) {
//                    Log.e("locationResult", "" + bearing);
//                    carMarker.position(new LatLng(lat, lng));
//                    ProjectUtil.rotateMarker(carMarker, bearing);
//                }
            } else {
                getCurrentBooking();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DriverHomeAct.this);

        if (getIntent().getStringExtra("object") != null) {
            Log.e("DialogChala123", "Object = " + getIntent().getStringExtra("object"));
            MusicManager.getInstance().initalizeMediaPlayer(DriverHomeAct.this, Uri.parse
                    (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
            MusicManager.getInstance().stopPlaying();
            Log.e("DialogChala====", "DialogChala Neeche" + getIntent().getStringExtra("object"));
            NewRequestDialogTaxiNew.getInstance().Request(DriverHomeAct.this, getIntent().getStringExtra("object"));
        }

        itit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnREsumeCalled = true;
        unregisterReceiver(JobStatusReceiver);
    }

    private void getCurrentBooking() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "DRIVER");
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBooking(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            isOnREsumeCalled = false;
                            if (result.getStatus().equalsIgnoreCase("Pending")) {

                            } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackDriverAct.class);
                                k.putExtra("data", data);
                                startActivity(k);
                            }

//                            else if (result.getStatus().equalsIgnoreCase("Arrived")) {
//                                Intent j = new Intent(mContext, TrackAct.class);
//                                j.putExtra("data", data);
//                                startActivity(j);
//                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
//                                Intent j = new Intent(mContext, TrackAct.class);
//                                j.putExtra("data", data);
//                                startActivity(j);
//                            } else if (result.getStatus().equalsIgnoreCase("End")) {
////                                      Intent j = new Intent(TaxiHomeAct.this, PaymentSummary.class);
////                                      j.putExtra("data",data);
////                                      startActivity(j);
//                            }
                        }
                    } else {

                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }


    @SuppressLint("MissingPermission")
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

        binding.chlidDashboard.switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e("SwitchState", "isChecked = " + isChecked);

                if (isChecked) {
                    onlinOfflineApi("ONLINE");
                    binding.chlidDashboard.switchOnOff.setText("ONLINE");
                } else {
                    onlinOfflineApi("OFFLINE");
                    binding.chlidDashboard.switchOnOff.setText("OFFLINE");
                }

            }
        });

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic).into(binding.childNavDrawer.userImg);

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getFirst_name() + "" +
                modelLogin.getResult().getLast_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic).into(binding.chlidDashboard.cvImg);

        binding.chlidDashboard.tvName.setText(modelLogin.getResult().getFirst_name() + "" +
                modelLogin.getResult().getLast_name());
        binding.chlidDashboard.tvCarNuumber.setText(modelLogin.getResult().getCar_number());

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideHistoryAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnEarning.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EarningAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnPurchase.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PurchasePlanAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            ProjectUtil.logoutAppDialog(mContext);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("fdasfasfasf","isOnREsumeCalled Before = " + isOnREsumeCalled);

        MusicManager.getInstance().initalizeMediaPlayer(DriverHomeAct.this, Uri.parse
                (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
        MusicManager.getInstance().stopPlaying();
        registerReceiver(JobStatusReceiver, new IntentFilter("Job_Status_Action_Taxi"));
        if (ProjectUtil.checkPermissions(mContext)) {
            if (ProjectUtil.isLocationEnabled(mContext)) {
                setCurrentLoc();
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            ProjectUtil.requestPermissions(mContext);
        }
        if (isOnREsumeCalled) {
            Log.e("fdasfasfasf","isOnREsumeCalled After = " + isOnREsumeCalled);
            getCurrentBooking();
            getProfile();
        }
        startLocationUpdates();
    }

    private void setCurrentLoc() {
        // gpsTracker = new GPSTracker(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(DriverHomeAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(DriverHomeAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverHomeAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(DriverHomeAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        }
                    }
                }, Looper.myLooper());
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {

        Log.e("asdfasfasf", "Location Akash = " + currentLocation);
        Log.e("asdfasfasf", "currentLocation = " + currentLocation);
        Log.e("asdfasfasf", "currentLocationMarker = " + currentLocationMarker);

        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    Log.e("gdfgdfsdfdf", "Map Andar = " + currentLocation);
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("gdfgdfsdfdf", "Map Andar else = " + currentLocation);
                Log.e("gdfgdfsdfdf", "Hello Marker Anuimation");
                animateCamera(currentLocation);
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    private void onlinOfflineApi(String status) {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("status", status);
        Call<ResponseBody> call = api.updateOnOffApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                Log.e("xjgxkjdgvxsd", "response = " + response);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("xjgxkjdgvxsd", "response = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        if (status.equals("ONLINE")) {
                            modelLogin.getResult().setOnline_status("ONLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.chlidDashboard.switchOnOff.setChecked(true);
                        } else {
                            modelLogin.getResult().setOnline_status("OFFLINE");
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                            binding.chlidDashboard.switchOnOff.setChecked(false);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure = " + t.getMessage());
            }
        });

    }

    private void getProfile() {
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

                            if ("ONLINE".equals(modelLogin.getResult().getOnline_status())) {
                                binding.chlidDashboard.switchOnOff.setText("ONLINE");
                                binding.chlidDashboard.switchOnOff.setChecked(true);
                            } else {
                                binding.chlidDashboard.switchOnOff.setText("OFFLINE");
                                binding.chlidDashboard.switchOnOff.setChecked(false);
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

    @Override
    public void bookingApiCalled() {
        getCurrentBooking();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

}