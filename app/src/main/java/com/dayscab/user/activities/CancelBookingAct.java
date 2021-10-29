package com.dayscab.user.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityCancelBookingBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.directionclasses.DrawPollyLine;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
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

public class CancelBookingAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = CancelBookingAct.this;
    ActivityCancelBookingBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private ModelLogin.Result driverDetails;
    private LatLng PicLatLon, DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private Marker driverMarkerCar;
    String driverId = "", usermobile = "";
    private Marker pCurrentLocationMarker;
    private Marker dcurrentLocationMarker;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cancel_booking);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);
            usermobile = driverDetails.getMobile();
            PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));

            try {
                DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
            } catch (Exception e) {}

        }

        drawRoute();

        itit();

    }

    private void animateCamera(@NonNull LatLng location) {
        // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(location)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(15).build();
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
                dcurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(DropLatLon).title("Destination Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_marker)));
            }
        }
    }

    private void drawRoute() {

        showMarkerPickUp(PicLatLon);
        showDestinationMarker(DropLatLon);

        DrawPollyLine.get(this)
                .setOrigin(PicLatLon)
                .setDestination(DropLatLon)
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

        zoomMapInitial(PicLatLon,DropLatLon);

    }

    protected void zoomMapInitial(LatLng finalPlace, LatLng pickup) {
        try {
            int padding = 200;
            LatLngBounds.Builder bc = new LatLngBounds.Builder();
            bc.include(finalPlace);
            bc.include(pickup);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), padding));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void itit() {

        binding.tvFrom.setText(result.getPicuplocation());
        binding.etDestination.setText(result.getDropofflocation());

        binding.btnCancel.setOnClickListener(v -> {
            finish();
        });

        binding.btCancelBooking.setOnClickListener(v -> {
           startActivity(new Intent(mContext, RideCancelationAct.class));
           finish();
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

}