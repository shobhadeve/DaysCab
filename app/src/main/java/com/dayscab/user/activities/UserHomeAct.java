package com.dayscab.user.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.activties.AccountAct;
import com.dayscab.common.activties.AppSettingsActUser;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUserHomeBinding;
import com.dayscab.driver.activities.ActiveBookingAct;
import com.dayscab.driver.activities.DriverFeedbackAct;
import com.dayscab.driver.activities.WalletAct;
import com.dayscab.user.models.ModelAvailableDriver;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.directionclasses.DrawPollyLine;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = UserHomeAct.this;
    ActivityUserHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private boolean isAddedMarker2 = false, isAddedMarker1 = false;
    private Location currentLocation;
    private MarkerOptions PicUpMarker, DropOffMarker;
    private LatLng PickUpLatLng, DropOffLatLng;
    private Marker currentLocationMarker;
    private ScheduledExecutorService scheduleTaskExecutor;
    private PolylineOptions lineOptions;
    SupportMapFragment mapFragment;
    String registerId = "";
    ArrayList<Marker> makresList = new ArrayList<>();

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("action_change")) {
                Log.e("actionchange", "getNearDriver called");
                getNearDriver();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_home);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(UserHomeAct.this);

        MyApplication.checkToken(mContext);

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

        BindExecutor();

        startLocationUpdates();

        itit();

    }

    public static boolean isActivityActive(Activity activity) {
        if (null != activity)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                return !activity.isFinishing() && !activity.isDestroyed();
            else return !activity.isFinishing();
        return false;
    }

    private void BindExecutor() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getNearDriver();
            }
        }, 0, 8, TimeUnit.MINUTES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("action_change"));
        startLocationUpdates();
        BindExecutor();
        getProfileApiCall();
        // getNearDriver();
        // getCurrentBooking();
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

                            Log.e("adfasdfss", "getDriver_lisence_img = " + modelLogin.getResult().getDriver_lisence_img());

                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
                                logoutAlertDialog();
                            }

                        }

                    } catch (Exception e) {
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

    private void getCurrentBooking() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", TimeZone.getDefault().getID());

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
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("getUserRatingStatus", "getUserRatingStatus = " + result.getUserRatingStatus());
                            Log.e("getUserRatingStatus", "ModelCurrentBookingResult = " + result.getPayment_status());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackAct.class);
                                k.putExtra("data", data);
                                startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackAct.class);
                                j.putExtra("data", data);
                                startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                if ("Success".equals(result.getPayment_status())) {
                                    if (null == result.getUserRatingStatus() ||
                                            "".equals(result.getUserRatingStatus())) {
                                        Intent j = new Intent(mContext, DriverFeedbackAct.class);
                                        j.putExtra("data", data);
                                        startActivity(j);
                                    }
                                } else {
                                    Intent j = new Intent(mContext, EndUserAct.class);
                                    j.putExtra("data", data);
                                    startActivity(j);
                                }
                            }
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
            }

        });

    }

    private void getNearByDriverBroadcast() {
        HashMap<String, String> param = new HashMap<>();
        if (PickUpLatLng != null) {
            param.put("latitude", "" + PickUpLatLng.latitude);
            param.put("longitude", "" + PickUpLatLng.longitude);
        } else {
            param.put("latitude", "0.0");
            param.put("longitude", "0.0");
        }
        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("user_iduser_id", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAvailableCarDriversHome(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {
                        }.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(jsonObject.getString("result"), listType);
                        if (mMap != null) {
                            AddDefaultMarker();
                            for (ModelAvailableDriver driver : drivers) {
                                int height = 75;
                                int width = 45;
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                MarkerOptions car = new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getUser_name() + " (" + driver.getCar_name() + ")")
                                        .icon(smallMarkerIcon);
                                mMap.addMarker(car);
                            }
                        }
                    } else {
                        try {
                            mMap.clear();
//                            currentLocationMarker = null;
//                            showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
//                            DrawPolyLine();
                        } catch (Exception e) {
                        }
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void getNearDriver() {

        for (int i = 0; i < makresList.size(); i++) {
            makresList.get(i).remove();
        }
        makresList.clear();

        HashMap<String, String> param = new HashMap<>();

        if (PickUpLatLng != null) {
            param.put("latitude", "" + PickUpLatLng.latitude);
            param.put("longitude", "" + PickUpLatLng.longitude);
        } else {
            param.put("latitude", "0.0");
            param.put("longitude", "0.0");
        }
        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("user_iduser_id", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAvailableCarDriversHome(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Type listType = new TypeToken<ArrayList<ModelAvailableDriver>>() {
                        }.getType();
                        ArrayList<ModelAvailableDriver> drivers = new GsonBuilder().create().fromJson(jsonObject.getString("result"), listType);
                        if (mMap != null) {
                            AddDefaultMarker();
                            for (ModelAvailableDriver driver : drivers) {
                                int height = 75;
                                int width = 45;
                                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                                MarkerOptions car = new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getUser_name() + " (" + driver.getCar_name() + ")")
                                        .icon(smallMarkerIcon);
                                makresList.add(mMap.addMarker(car));
                            }
                        }
                    } else {
                    }
                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void itit() {

        PicUpMarker = new MarkerOptions().title("Pick Up Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        binding.chlidDashboard.btnNext.setOnClickListener(v -> {

            if (PickUpLatLng == null) {
                Toast.makeText(this, getString(R.string.select_pick_loc), Toast.LENGTH_SHORT).show();
                return;
            }

            if (DropOffLatLng == null) {
                Toast.makeText(this, getString(R.string.select_dropoff_loc), Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, RideOptionAct.class);
            intent.putExtra("pollyLine", lineOptions);
            intent.putExtra("PickUp", PickUpLatLng);
            intent.putExtra("DropOff", DropOffLatLng);
            intent.putExtra("picadd", binding.chlidDashboard.tvFrom.getText().toString().trim());
            intent.putExtra("dropadd", binding.chlidDashboard.tvDestination.getText().toString().trim());
            startActivity(intent);

        });

        binding.chlidDashboard.tvFrom.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setCountry(AppConstant.COUNTRY)
                    .build(this);
            startActivityForResult(intent, 1002);
        });

        binding.chlidDashboard.tvAvailableDriversPool.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AvailablePoolDriversAct.class)
                    .putExtra("lat", String.valueOf(currentLocation.getLatitude()))
                    .putExtra("lon", String.valueOf(currentLocation.getLongitude()))
            );
        });

        binding.chlidDashboard.tvDestination.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                    Place.Field.LAT_LNG, Place.Field.ADDRESS);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setCountry(AppConstant.COUNTRY)
                    .build(this);
            startActivityForResult(intent, 1003);
        });

        binding.childNavDrawer.tvUsername.setText(modelLogin.getResult().getUser_name());
        binding.childNavDrawer.tvEmail.setText(modelLogin.getResult().getEmail());

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(this, RideHistoryAct.class)
                    .putExtra("type", AppConstant.USER)
            );
        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(this, AccountAct.class)
                    .putExtra("type", AppConstant.USER)
            );
        });

        binding.childNavDrawer.btnWallet.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(this, WalletAct.class)
                    .putExtra("type", AppConstant.USER)
            );
        });

        binding.childNavDrawer.btnEditProfile.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(this, AppSettingsActUser.class));
            // finish();
        });

        binding.childNavDrawer.tvActiveRides.setOnClickListener(v -> {
            navmenu();
            startActivity(new Intent(this, ActiveBookingAct.class));
        });

        binding.childNavDrawer.signout.setOnClickListener(v -> {
            navmenu();
            logoutAppDialog();
        });

    }

    protected Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;

    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, StartAct.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finish();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void navmenu() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }

    // Trigger new location updates at interval
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(UserHomeAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // New Google API SDK V11 Uses GetFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(UserHomeAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserHomeAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(UserHomeAct.this)
                .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null) {
                            Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                            currentLocation = locationResult.getLastLocation();
                            showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            // getNearDriver();
                        }
                    }
                }, Looper.myLooper());

    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                if (mMap != null) {
                    if (TextUtils.isEmpty(binding.chlidDashboard.tvFrom.getText().toString().trim())) {
                        getNearDriver();
                        if (currentLocation != null) {
                            PickUpLatLng = new LatLng(currentLocation.latitude, currentLocation.longitude);
                            binding.chlidDashboard.tvFrom.setText(ProjectUtil.getCompleteAddressString(this, currentLocation.latitude, currentLocation.longitude));
                        }
                    }
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("PickUp location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
                    animateCamera(currentLocation);
                }
            } else {
                Log.e("sdfdsfdsfds", "Hello Marker Anuimation");
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                PickUpLatLng = place.getLatLng();
                binding.chlidDashboard.tvFrom.setText(ProjectUtil.getCompleteAddressString(mContext, PickUpLatLng.latitude, PickUpLatLng.longitude));
                if (PickUpLatLng != null) {
                    PicUpMarker.position(PickUpLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1 = true;
                    }
                }
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    DrawPolyLine();
                }
            }
        } else if (requestCode == 1003) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                DropOffLatLng = place.getLatLng();
                binding.chlidDashboard.tvDestination.setText(ProjectUtil.getCompleteAddressString(mContext, DropOffLatLng.latitude, DropOffLatLng.longitude));
                if (DropOffLatLng != null) {
                    PicUpMarker.position(DropOffLatLng);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(DropOffLatLng)));
                    if (!isAddedMarker1) {
                        mMap.addMarker(PicUpMarker);
                        isAddedMarker1 = true;
                    }
                }
                if (PickUpLatLng != null & DropOffLatLng != null) {
                    DrawPolyLine();
                }
            }
        }

    }

    private void DrawPolyLine() {
        try {
            DrawPollyLine.get(this).setOrigin(PickUpLatLng)
                    .setDestination(DropOffLatLng)
                    .execute(new DrawPollyLine.onPolyLineResponse() {
                        @Override
                        public void Success(ArrayList<LatLng> latLngs) {
                            mMap.clear();
                            lineOptions = new PolylineOptions();
                            lineOptions.addAll(latLngs);
                            lineOptions.width(10);
                            lineOptions.color(Color.BLUE);
                            AddDefaultMarker();
                        }
                    });
        } catch (Exception e) {

        }
    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc) {
        try {
            int padding = 100;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AddDefaultMarker() {
        if (mMap != null) {
            mMap.clear();
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            if (PickUpLatLng != null) {
                PicUpMarker.position(PickUpLatLng);
                mMap.addMarker(PicUpMarker);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(PickUpLatLng)));
            }
            if (DropOffLatLng != null) {
                DropOffMarker.position(DropOffLatLng);
                mMap.addMarker(DropOffMarker);
            }
        }
        zoomMapInitial(PickUpLatLng, DropOffLatLng);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        scheduleTaskExecutor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdownNow();
    }

}