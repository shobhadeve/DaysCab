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
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.ChatingAct;
import com.dayscab.common.activties.LoginAct;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityTrackDriverBinding;
import com.dayscab.databinding.DriverChangeStatusDialogBinding;
import com.dayscab.databinding.NavigateDialogBinding;
import com.dayscab.databinding.OtpDialogForDriverBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.MusicManager;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackDriverAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackDriverAct.this;
    ActivityTrackDriverBinding binding;
    SharedPref sharedPref;
    private ModelCurrentBooking data;
    ModelLogin modelLogin;
    private Marker currentLocationMarker;
    private ModelLogin.Result UserDetails;
    private ModelCurrentBookingResult result;
    private String requestId, userMobile, userId, userName;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 4000;  /* 5 secs */
    private long FASTEST_INTERVAL = 4000; /* 2 sec */
    Location currentLocation;
    Vibrator vibrator;
    private String registerId = "";
    GoogleMap mMap;
    Polyline line;
    private LatLng currentlocation;
    private LatLng pickLocation, droplocation;
    SupportMapFragment mapFragment;
    private String status;
    String type = "";
    private Marker pCurrentLocationMarker;
    private Marker dcurrentLocationMarker;

    BroadcastReceiver statusBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("job_status")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    MusicManager.getInstance().initalizeMediaPlayer(TrackDriverAct.this, Uri.parse
                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                    MusicManager.getInstance().stopPlaying();
                    Toast.makeText(context, "Ride cancelled by user", Toast.LENGTH_LONG).show();
                    finishAffinity();
                    startActivity(new Intent(TrackDriverAct.this, DriverHomeAct.class));
                } else if (intent.getStringExtra("status").equals("changed_route")) {
                    MusicManager.getInstance().initalizeMediaPlayer(TrackDriverAct.this, Uri.parse
                            (ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.doogee_ringtone));
                    MusicManager.getInstance().stopPlaying();
                    Toast.makeText(context, getString(R.string.user_changed_the_destination), Toast.LENGTH_LONG).show();
                    double dropLat = Double.parseDouble(intent.getStringExtra("lat"));
                    double dropLon = Double.parseDouble(intent.getStringExtra("lon"));
                    droplocation = new LatLng(dropLat, dropLon);
                    showAlert(intent.getStringExtra("dropofflocation"));
                }
            }
        }
    };

    public void showAlert(String dropLocationAdd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.user_changed_the_destination))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (line != null) {
                                    line.remove();
                                }

                                binding.tvDestination.setText(dropLocationAdd);
                                DrawPollyLine.get(mContext)
                                        .setOrigin(pickLocation)
                                        .setDestination(droplocation)
                                        .execute(new DrawPollyLine.onPolyLineResponse() {
                                            @Override
                                            public void Success(ArrayList<LatLng> latLngs) {
                                                PolylineOptions options = new PolylineOptions();
                                                options.addAll(latLngs);
                                                options.color(Color.BLUE);
                                                options.width(10);
                                                options.startCap(new SquareCap());
                                                options.endCap(new SquareCap());
                                                line = mMap.addPolyline(options);
                                            }
                                        });
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track_driver);
        MyApplication.checkToken(mContext);
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

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

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackDriverAct.this);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        type = getIntent().getStringExtra("type");
        startLocationUpdates();

        try {
            if (getIntent() != null) {
                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                result = data.getResult().get(0);
                requestId = result.getId();
                userMobile = data.getResult().get(0).getUser_details().get(0).getMobile();
                userId = data.getResult().get(0).getUser_details().get(0).getId();
                userName = data.getResult().get(0).getUser_details().get(0).getUser_name();
                UserDetails = result.getUser_details().get(0);
                if (UserDetails.getImage() != null) {
                    Glide.with(mContext)
                            .load(UserDetails.getImage())
                            .placeholder(R.drawable.user_ic)
                            .error(R.drawable.user_ic)
                            .into(binding.driverImage);
                }
            }
        } catch (Exception e) {
        }

        itit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfileApiCall();
        registerReceiver(statusBroadCast, new IntentFilter("job_status"));
        startLocationUpdates();
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

    private void getCurrentBooking() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", AppConstant.DRIVER);
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
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        startActivity(new Intent(mContext, EndTripDriverAct.class).putExtra("data", data));
                        result.setStatus("End");
                        finish();
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

    private void logoutAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusBroadCast);
    }

    private void itit() {

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        pickLocation = new LatLng(Double.parseDouble(result.getPicuplat()),
                Double.parseDouble(result.getPickuplon()));

        pickLocation = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));
        droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));

        binding.pickUp.setText(result.getPicuplocation());
        binding.tvDestination.setText(result.getDropofflocation());
        binding.tvName.setText(result.getUser_details().get(0).getUser_name());

        try {
            droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
        } catch (Exception e) {
        }

        if (result.getStatus().equalsIgnoreCase("Accept")) {
            binding.btnStatus.setText(R.string.update_when_you_arrived);
        } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
            binding.tvFrom.setText(R.string.desti_loc);
            binding.tvFrom.setText(result.getDropofflocation());
            binding.btnStatus.setText(R.string.start_the_trip);
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            binding.tvFrom.setText(R.string.desti_loc);
            binding.tvFrom.setText(result.getDropofflocation());
            binding.btnStatus.setText(R.string.end_the_trip);
        } else if (result.getStatus().equalsIgnoreCase("End")) {
            binding.btnStatus.setText("Finish");
        } else if (result.getStatus().equalsIgnoreCase("Cancel")) {
            finish();
        }

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, userMobile);
        });

        binding.ivNavigate.setOnClickListener(v -> {
            openNavigateionDialog();
        });

        binding.icChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChatingAct.class)
                    .putExtra("request_id", result.getId())
                    .putExtra("receiver_id", result.getUserId())
                    .putExtra("name", userName)
            );
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideCancelDriverAct1.class)
                    .putExtra("data", data)
            );
        });

        binding.btnStatus.setOnClickListener(v -> {
            startEndTripDialog(result.getStatus());
        });

    }

    public void AcceptCancel(String status) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("status", status);
        map.put("cancel_reaison", "");
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.pauseProgressDialog();
                        ProjectUtil.clearNortifications(mContext);
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        if (status.equalsIgnoreCase("Arrived")) {
                            result.setStatus("Arrived");
                            binding.btnStatus.setText(R.string.start_the_trip);
                        } else if (status.equalsIgnoreCase("Start")) {
                            result.setStatus("Start");
                            binding.btnStatus.setText(R.string.end_the_trip);
                        } else if (status.equalsIgnoreCase("End")) {
                            getCurrentBooking();
//                            startActivity(new Intent(mContext, EndTripDriverAct.class)
//                                    .putExtra("data", data)
//                            );
//                            result.setStatus("End");
//                            finish();
                        }
                    } else {
                        MyApplication.showToast(mContext, getString(R.string.req_cancelled));
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

    private void openNavigateionDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        NavigateDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.navigate_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_back_color);

        dialogBinding.btPickup.setOnClickListener(v -> {
            naigateToMap(pickLocation.latitude, pickLocation.longitude);
            // startActivity(new Intent(mContext, WebviewNavAct.class));
        });

        dialogBinding.btDropOff.setOnClickListener(v -> {
            naigateToMap(droplocation.latitude, droplocation.longitude);
            // startActivity(new Intent(mContext, WebviewNavAct.class));
        });

        dialog.show();

    }

    private void naigateToMap(double start, double end) {

        String url = "google.navigation:q=" + start + "," + end;
        Log.e("zsdfasdasdas", "url = " + url);

        try {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            Uri navigationIntentUri = Uri.parse("google.navigation:q=" + start + "," + end);//creating intent with latlng
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
            startActivity(mapIntent);
        }

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(19).build();
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                int height = 95;
                int width = 65;
                Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentlocation).title("My Location")
                        .icon(smallMarkerIcon));
                animateCamera(currentLocation);
            } else {
                Log.e("sdfdsfdsfds", "Hello Marker Anuimation");
                MarkerAnimation.animateMarkerToGB(currentLocationMarker, currentLocation, new LatLngInterpolator.Spherical());
            }
        }
    }

    private void showMarkerPickUp(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (pCurrentLocationMarker == null) {
                pCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("PickUp Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
                animateCamera(currentLocation);
            }
        }
    }

    private void showDestinationMarker(@NonNull LatLng dcurrentLocation) {
        Log.e("TAG", "showDestinationMarker: " + dcurrentLocation);
        if (dcurrentLocation != null) {
            if (dcurrentLocationMarker == null) {
                dcurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(droplocation).title("Destination Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
            }
        }
    }

    private void animateCamera(@NonNull LatLng location) {
        // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(TrackDriverAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(TrackDriverAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TrackDriverAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(TrackDriverAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    currentlocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

    private void startEndDialog(String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        if (status.equalsIgnoreCase("Accept")) {
            builder.setTitle(getString(R.string.arrived_text));
        } else if (status.equalsIgnoreCase("Arrived")) {
            builder.setTitle(getString(R.string.start_the_trip_text));
        } else if (status.equalsIgnoreCase("Start")) {
            builder.setTitle(getString(R.string.end_the_trip_text));
        }

        builder.setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (status.equalsIgnoreCase("Accept")) {
                            AcceptCancel("Arrived");
                        } else if (status.equalsIgnoreCase("Arrived")) {
                            AcceptCancel("Start");
                        } else if (status.equalsIgnoreCase("Start")) {
                            AcceptCancel("End");
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void startEndTripDialog(String status) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DriverChangeStatusDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.driver_change_status_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        if (status.equalsIgnoreCase("Accept")) {
            dialogBinding.tvMessage.setText(getString(R.string.arrived_text));
        } else if (status.equalsIgnoreCase("Arrived")) {
            dialogBinding.tvMessage.setText(getString(R.string.start_the_trip_text));
        } else if (status.equalsIgnoreCase("Start")) {
            dialogBinding.tvMessage.setText(getString(R.string.end_the_trip_text));
        }

        // dialogBinding.tvMessage.setText(text);

        dialogBinding.tvOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (status.equalsIgnoreCase("Accept")) {
                AcceptCancel("Arrived");
            } else if (status.equalsIgnoreCase("Arrived")) {
                enterOtpDialog();
            } else if (status.equalsIgnoreCase("Start")) {
                AcceptCancel("End");
            }
        });

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void enterOtpDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        OtpDialogForDriverBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),
                        R.layout.otp_dialog_for_driver, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btSubmit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etOtp.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_otp), Toast.LENGTH_SHORT).show();
            } else {
                checkIsOtpCorrect(dialogBinding.etOtp.getText().toString().trim(), dialog);
            }
        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void checkIsOtpCorrect(String otp, Dialog dialog) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("otp", otp);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.checkOtpApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.pauseProgressDialog();
                        dialog.dismiss();
                        AcceptCancel("Start");
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                    } else {
                        MyApplication.showToast(mContext, getString(R.string.wrong_otp));
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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.e("markermarker", "marker position = " + latLng);
            }
        });

        mMap.getUiSettings().setMapToolbarEnabled(false);

        showMarkerPickUp(pickLocation);
        showDestinationMarker(droplocation);

        DrawPollyLine.get(this)
                .setOrigin(pickLocation)
                .setDestination(droplocation)
                .execute(new DrawPollyLine.onPolyLineResponse() {
                    @Override
                    public void Success(ArrayList<LatLng> latLngs) {
                        PolylineOptions options = new PolylineOptions();
                        options.addAll(latLngs);
                        options.color(Color.BLUE);
                        options.width(10);
                        options.startCap(new SquareCap());
                        options.endCap(new SquareCap());
                        line = mMap.addPolyline(options);
                    }
                });

        showMarkerCurrentLocation(currentlocation);
    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng currenLoc, LatLng pickup) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(currenLoc);
            bc.include(pickup);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
