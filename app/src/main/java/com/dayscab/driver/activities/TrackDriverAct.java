package com.dayscab.driver.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityTrackBinding;
import com.dayscab.databinding.ActivityTrackDriverBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.TripStatusDailogBinding;
import com.dayscab.user.activities.PaymentAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.MusicManager;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.directionclasses.DrawPollyLine;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location currentLocation;
    Vibrator vibrator;
    GoogleMap mMap;
    private LatLng currentlocation;
    private LatLng pickLocation, droplocation;
    SupportMapFragment mapFragment;
    private String status;
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
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_driver);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(TrackDriverAct.this);

        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
        } catch (Exception e) {}

        itit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(statusBroadCast, new IntentFilter("job_status"));
        startLocationUpdates();
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

        binding.tvFrom.setText(result.getPicuplocation());
        binding.tvName.setText(result.getUser_details().get(0).getUser_name());

        try {
            droplocation = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
        } catch (Exception e) {}

        if (result.getStatus().equalsIgnoreCase("Accept")) {
            binding.btnStatus.setText("Arriving");
        } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
            binding.btnStatus.setText("Start");
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            binding.btnStatus.setText("End");
        } else if (result.getStatus().equalsIgnoreCase("End")) {
            binding.btnStatus.setText("Finish");
        } else if (result.getStatus().equalsIgnoreCase("Cancel")) {
            finish();
        }

        binding.ivCancelTrip.setOnClickListener(v -> {

        });

    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(15).build();
    }

    private void showMarkerCurrentLocation(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (currentLocationMarker == null) {
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(currentlocation).title("My Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top)));
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


    private void startEndTripDialog(String text) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TripStatusDailogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.trip_status_dailog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText(text);

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            if(status.equals("Start")) {
                status = "finish";
                binding.btnStatus.setText("Finish");
            } else {
                startActivity(new Intent(mContext,EndTripDriverAct.class));
                finish();
            }
        });

        dialogBinding.btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

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
                        Polyline line = mMap.addPolyline(options);
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
