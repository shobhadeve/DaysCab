package com.dayscab.driver.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.AppSettingsAct;
import com.dayscab.common.activties.LoginAct;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityDriverHomeBinding;
import com.dayscab.databinding.CurrentPlanDialogBinding;
import com.dayscab.databinding.TaxiSelectDialogBinding;
import com.dayscab.driver.adapters.AdapterVehicleSelect;
import com.dayscab.driver.dialogs.NewRequestDialogTaxiNew;
import com.dayscab.driver.models.ModelVehicles;
import com.dayscab.user.activities.RideHistoryAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.MusicManager;
import com.dayscab.utils.MyApplication;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
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
    private boolean isDialogShown = true;
    ModelVehicles modelVehicles;
    private String carTypeId;
    String registerId = "";

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
            } else {
                getCurrentBooking();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_home);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));

        // Setting up the flag programmatically so that the
        // Device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

//      getProfileApiCall();

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
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackDriverAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackDriverAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                Intent j = new Intent(mContext, EndTripDriverAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            }
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

        binding.chlidDashboard.llChange.setOnClickListener(v -> {
            getVehicles();
        });

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideHistoryAct.class)
                    .putExtra("type", AppConstant.DRIVER)
            );
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnAppSetting.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AppSettingsAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateProfileDriverAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnCurrentPlan.setOnClickListener(v -> {
            getProfileNew();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnOfferPool.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UserPoolRequestAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

//      binding.childNavDrawer.btnEarning.setOnClickListener(v -> {
//          startActivity(new Intent(mContext, EarningAct.class));
//          binding.drawerLayout.closeDrawer(GravityCompat.START);
//      });

        binding.childNavDrawer.btnPurchase.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PurchasePlanAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnActiveRides.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ActiveScheduleDriverAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            ProjectUtil.logoutAppDialog(mContext);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

    }

    public static void exitAppDialog(Context mContext) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.close_app_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void getVehicles() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllVehicleApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelVehicles = new Gson().fromJson(stringResponse, ModelVehicles.class);

                            for (int i = 0; i < modelVehicles.getResult().size(); i++) {
                                if (!modelVehicles.getResult().get(i).getStatus().equals("Active")) {
                                    modelVehicles.getResult().remove(i);
                                    i--;
                                }
                            }

                            openVehicleDialog();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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

    private void openVehicleDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        TaxiSelectDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.taxi_select_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        AdapterVehicleSelect adapterVehicleSelect = new AdapterVehicleSelect(mContext, modelVehicles.getResult(), dialog, DriverHomeAct.this::setCarDetails, carTypeId);
        dialogBinding.carList.setAdapter(adapterVehicleSelect);

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialogBinding.btManage.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ManageVehicleAct.class));
            dialog.dismiss();
        });

        dialogBinding.btnAddNew.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddVehicleAct.class)
                    .putExtra("type", "manage")
            );
            dialog.dismiss();
        });

        dialog.show();

    }

    private void setCarDetails(ModelVehicles.Result result) {
        Log.e("asdasffdas", "carTypeId = " + result.getId());
        carTypeId = result.getId();
        binding.chlidDashboard.tvCarNuumber.setText(result.getCar_number());
    }

    private void getProfileApiCall() {

        // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);
                            if (!modelLogin.getResult().getStatus().equals("Active")) {
                                showAlert();
                            }

                            // openCurrentPlanDialog(modelLogin);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
                    }

                    //  Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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

    private void showAlert() {
        if (isDialogShown) {
            android.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(getString(R.string.deactivate_account_text))
                    .setCancelable(false)
                    .create().show();
        }
        isDialogShown = false;
    }

    private void openCurrentPlanDialog(ModelLogin modelLogin) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        CurrentPlanDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.current_plan_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvPlanFees.setText(modelLogin.getResult().getPlan_ride_fees() + "%");
        dialogBinding.tvPlansAmount.setText(AppConstant.CURRENCY + " " + modelLogin.getResult().getPlan_amount());
        dialogBinding.tvPlanBonusTrip.setText(modelLogin.getResult().getPlan_bonus_trip() + " Trips");
        dialogBinding.tvPlanTotalTrips.setText(modelLogin.getResult().getPlan_total_ride() + " Trips");
        dialogBinding.tvPlanName.setText(modelLogin.getResult().getPlan_name());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic).into(binding.childNavDrawer.userImg);

        Glide.with(mContext)
                .load(modelLogin.getResult().getImage())
                .placeholder(R.drawable.user_ic)
                .error(R.drawable.user_ic).into(binding.chlidDashboard.cvImg);

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getFirst_name() + "" +
                modelLogin.getResult().getLast_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        Log.e("fdasfasfasf", "isOnREsumeCalled Before = " + isOnREsumeCalled);

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

        Log.e("fdasfasfasf", "isOnREsumeCalled After = " + isOnREsumeCalled);
        // getCurrentBooking();
        getProfile();
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
                    int height = 95;
                    int width = 65;
                    Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location")
                            .icon(smallMarkerIcon));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("gdfgdfsdfdf", "Map Andar else = " + currentLocation);
                Log.e("gdfgdfsdfdf", "Hello Marker Anuimation");
                animateCamera(currentLocation);
                Location location = new Location("");
                location.setLatitude(currentLocation.latitude);
                location.setLongitude(currentLocation.longitude);
                currentLocationMarker.setRotation(location.getBearing());
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(18).build();
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
                            openCurrentPlanDialog(modelLogin);

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
                            sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                            carTypeId = modelLogin.getResult().getCar_detail_id();

                            binding.chlidDashboard.tvCarNuumber.setText(modelLogin.getResult().getCar_number());
                            binding.chlidDashboard.tvCompleteRide.setText("Rides Completed\n" + modelLogin.getResult().getComplete_rides());
                            binding.chlidDashboard.tvNormalRide.setText("Remaining Rides\n" + modelLogin.getResult().getRide_count());

                            Log.e("onPOrfile", "Normal = " + modelLogin.getResult().getCar_id());
                            Log.e("onPOrfile", "remaining = " + modelLogin.getResult().getOn_way_ride());
                            Log.e("onPOrfile", "completed = " + modelLogin.getResult().getComplete_rides());
                            Log.e("onPOrfile", "getCar_number = " + modelLogin.getResult().getCar_number());

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                            if (!modelLogin.getResult().getStatus().equals("Active")) {
                                showAlert();
                            }

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

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                finishAffinity();
                                startActivity(new Intent(mContext, StartAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.close_app_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        exitAppDialog();
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