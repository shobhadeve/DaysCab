package com.dayscab.user.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.ChatingAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityTrackBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.TripStatusDialogNewBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.LatLngInterpolator;
import com.dayscab.utils.MarkerAnimation;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.directionclasses.DrawPollyLine;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
    double bearing = 0.0;
    String driverId = "", usermobile = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isMarkerZoom = false;
    private Marker pCurrentLocationMarker;
    private Marker dcurrentLocationMarker;
    Location previousLocation;

    BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("gdsfdsfdsf", "statusReceiver");
            if (intent.getAction().equals("Job_Status_Action")) {
                if (intent.getStringExtra("status").equals("Cancel")) {
                    finish();
                    Toast.makeText(mContext, "Your ride has been cancelled by driver", Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentBooking();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getDriverLocation();
            }
        }, 0, 5000);

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

        itit();

    }

    @Override
    protected void onResume() {
        getCurrentBooking();
        registerReceiver(statusReceiver, new IntentFilter("Job_Status_Action"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);
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
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                binding.driverOtp.setVisibility(View.VISIBLE);
                                binding.driverOtp.setText("Give this Otp " + result.getOtp() + " to driver ");
                                binding.titler.setText("Driver is arrived");
                                tripStatusDialog("Driver is arrived at pickup location", result.getStatus(), data);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                binding.titler.setText("Trip is started");
                                tripStatusDialog("Trip is started", result.getStatus(), data);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                binding.titler.setText("Trip is ended");
                                tripStatusDialog("Trip is ended", result.getStatus(), data);
                            } else if (result.getStatus().equalsIgnoreCase("Finish")) {
                                binding.titler.setText("Trip is Finished");
                                tripStatusDialog("Trip is Finished", result.getStatus(), data);
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

    private void getCurrentBookingForCode() {

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

                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();

                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);

                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                binding.driverOtp.setVisibility(View.VISIBLE);
                                binding.driverOtp.setText("Give this Otp " + result.getOtp() + " to driver ");
                                binding.titler.setText("Driver is arrived");
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

    private void tripStatusDialog(String text, String status, ModelCurrentBooking data) {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);

        dialog.setCancelable(false);
        TripStatusDialogNewBinding dialogNewBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.trip_status_dialog_new, null, false);
        dialogNewBinding.tvMessage.setText(text);

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialogNewBinding.tvOk.setOnClickListener(v -> {
            getCurrentBookingForCode();
            if ("End".equals(status)) {
                Intent j = new Intent(mContext, EndUserAct.class);
                j.putExtra("data", data);
                startActivity(j);
                finish();
            } else if ("Finish".equals(status)) {
                finishAffinity();
                startActivity(new Intent(mContext, UserHomeAct.class));
            }
            dialog.dismiss();
        });

        dialog.setContentView(dialogNewBinding.getRoot());

        dialog.show();

        //        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setCancelable(false);
//        builder.setTitle(text);
//        builder.setCancelable(false)
//                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if ("End".equals(status)) {
//                            Intent j = new Intent(mContext, EndUserAct.class);
//                            j.putExtra("data", data);
//                            startActivity(j);
//                            finish();
//                        } else if ("Finish".equals(status)) {
//                            finishAffinity();
//                            startActivity(new Intent(mContext, UserHomeAct.class));
//                        }
//                        dialog.dismiss();
//                    }
//                }).create().show();

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
                        if (driverMarkerCar == null) {
                            int height = 95;
                            int width = 65;
                            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.car_top);
                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                            BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                            DriverMarker = new MarkerOptions().title("Driver Here")
                                    .position(new LatLng(lat, lon))
                                    .icon(smallMarkerIcon);
                            driverMarkerCar = mMap.addMarker(DriverMarker);
                            if (!isMarkerZoom) {
                            }
                            // zoomCameraToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                        } else {
                            driverMarkerCar.setRotation(location.getBearing());

                            Log.e("LatlonDriver = ", " driver Location = " + new LatLng(lat, lon));
                            Log.e("LatlonDriver = ", " driver Marker = " + driverMarkerCar);
                            MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());

                            // MarkerAnimation.animateMarkerToGB(driverMarkerCar, new LatLng(location.getLatitude(), location.getLongitude()), new LatLngInterpolator.Spherical());
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

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private void animatedMarker(final LatLng startPosition, final LatLng nextPosition, final Marker mMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {

            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + nextPosition.latitude * t,
                        startPosition.longitude * (1 - t) + nextPosition.longitude * t);

                mMarker.setRotation((float) bearing);
                mMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        mMarker.setVisible(false);
                    } else {
                        mMarker.setVisible(true);
                    }
                }
            }
        });

    }

    private void zoomCameraToLocation(LatLng latLng) {
        isMarkerZoom = true;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void itit() {

        Log.e("asfasfasfdasf", "driverDetails.getCar_number() = " + driverDetails.getCar_number());
        Log.e("asfasfasfdasf", "driverDetails.getCar_name() = " + data.getCar_name());
        Log.e("asfasfasfdasf", "driverDetails.getAmount() = " + driverDetails.getAmount());
        Log.e("asfasfasfdasf", "driverDetails.getEstimate_time() = " + data.getEstimate_time() + " Min");

        if (result.getStatus().equalsIgnoreCase("Arrived")) {
            tripStatusDialog("Driver is arrived at pickup location", result.getStatus(), data);
        } else if (result.getStatus().equalsIgnoreCase("Start")) {
            tripStatusDialog("Trip is started", result.getStatus(), data);
        } else if (result.getStatus().equalsIgnoreCase("End")) {
            tripStatusDialog("Trip is ended", result.getStatus(), data);
        } else if (result.getStatus().equalsIgnoreCase("Finish")) {
            tripStatusDialog("Trip is Finished", result.getStatus(), data);
        }

        binding.tvCarNumber.setText(driverDetails.getCar_number());
        binding.tvName.setText(driverDetails.getUser_name());
        binding.tvCarName.setText(result.getCar_name());
        binding.tvPrice.setText(AppConstant.CURRENCY + (int) Double.parseDouble(result.getAmount()));
        binding.tvTime.setText(result.getEstimateTime() + " Min");

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            finish();
        });

        binding.icCall.setOnClickListener(v -> {
            ProjectUtil.call(mContext, usermobile);
        });

        binding.icChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChatingAct.class)
                    .putExtra("request_id", result.getId())
                    .putExtra("receiver_id", result.getDriverId())
                    .putExtra("name", driverDetails.getUser_name())
            );
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CancelBookingAct.class)
                    .putExtra("data", data)
            );
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

    private void showMarkerPickUp(@NonNull LatLng currentLocation) {
        if (currentLocation != null) {
            if (pCurrentLocationMarker == null) {
                pCurrentLocationMarker = mMap.addMarker(new MarkerOptions().position(PicLatLon).title("PickUp Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
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
        zoomMapAccordingToLatLng();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

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

    }

    private void zoomMapAccordingToLatLng() {

        Log.e("pCurrentLocationMarker", "pCurrentLocationMarker = " + pCurrentLocationMarker);
        Log.e("pCurrentLocationMarker", "dcurrentLocationMarker = " + dcurrentLocationMarker);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // the include method will calculate the min and max bound.
        builder.include(pCurrentLocationMarker.getPosition());
        builder.include(dcurrentLocationMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10) + 30; // offset from edges of the map 10% of screen

        Log.e("pCurrentLocationMarker", "padding = " + padding);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);

    }

}