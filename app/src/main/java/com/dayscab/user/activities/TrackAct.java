package com.dayscab.user.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.driver.activities.TrackDriverAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackAct extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    private ModelLogin.Result driverDetails;
    private LatLng PicLatLon, DropLatLon;
    private GoogleMap mMap;
    private MarkerOptions DriverMarker;
    private MarkerOptions DropOffMarker;
    private Marker driverMarkerCar;
    Timer timer = null;
    String driverId = "", usermobile = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDriverLocation();
            }
        }, 0, 4000);

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);
            usermobile = driverDetails.getMobile();
            PicLatLon = new LatLng(Double.parseDouble(result.getPicuplat()), Double.parseDouble(result.getPickuplon()));

            try {
                DropLatLon = new LatLng(Double.parseDouble(result.getDroplat()), Double.parseDouble(result.getDroplon()));
            } catch (Exception e) {
            }

            if (driverDetails.getProfile_image() != null) {
                Glide.with(mContext).load(driverDetails.getProfile_image())
                        .placeholder(R.drawable.user_ic).into(binding.driverImage);
            }

        }


        //        if ("end".equals(status)) {
//            binding.rlFeedback.setVisibility(View.VISIBLE);
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    tripFinishDialog();
//                }
//            }, 8000);
//        }

        itit();

    }

    private void getDriverLocation() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", driverId);

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getLatLonDriver(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        double lat = Double.parseDouble(jsonObject.getString("lat"));
                        double lon = Double.parseDouble(jsonObject.getString("lon"));
                        Location location = new Location("");
                        location.setLatitude(lat);
                        location.setLongitude(lon);
                        location.getBearing();
                        location.getAccuracy();
                        if (driverMarkerCar == null) {
                            DriverMarker = new MarkerOptions().title("Driver Here")
                                    .position(new LatLng(lat, lon))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
                            driverMarkerCar = mMap.addMarker(DriverMarker);
                            zoomCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        } else {
                            driverMarkerCar.setRotation(location.getBearing());
                            Log.e("LatlonDriver = ", " driver Location = " + new LatLng(lat, lon));
                            Log.e("LatlonDriver = ", " driver Marker = " + driverMarkerCar);
                            MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
                            // zoomCameraToLocation(new LatLng(location.getLatitude(),location.getLongitude()));
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

    private void zoomCameraToLocation(LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void itit() {

        Log.e("asfasfasfdasf", "driverDetails.getCar_number() = " + driverDetails.getCar_number());
        Log.e("asfasfasfdasf", "driverDetails.getCar_name() = " + data.getCar_name());
        Log.e("asfasfasfdasf", "driverDetails.getAmount() = " + driverDetails.getAmount());
        Log.e("asfasfasfdasf", "driverDetails.getEstimate_time() = " + data.getEstimate_time() + " Min");

        binding.tvCarNumber.setText(driverDetails.getCar_number());
        binding.tvName.setText(driverDetails.getUser_name());
        binding.tvCarName.setText(result.getCar_name());
        binding.tvPrice.setText("$" + (int) Double.parseDouble(result.getAmount()));
        binding.tvTime.setText(result.getEstimateTime() + " Min");

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            finish();
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
//            startActivity(new Intent(mContext, CancelBookingAct.class)
//                    .putExtra("data", data)
//            );
        });

    }

    private void tripFinishDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogDriverArrivedDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_driver_arrived_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText("Your trip is finished! Please do the payment");
        dialogBinding.btnIamComing.setText("Ok");
        dialogBinding.btnIamComing.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, PaymentAct.class));
            finish();
        });

        dialogBinding.btnCall.setVisibility(View.GONE);

        dialogBinding.btnCall.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

}