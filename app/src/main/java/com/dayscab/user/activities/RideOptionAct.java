package com.dayscab.user.activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.activties.LoginAct;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.AcceptDriverDialogBinding;
import com.dayscab.databinding.ActivityRideOptionBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.DialogSearchDriverBinding;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.user.adapters.AdapterCarTypes;
import com.dayscab.user.models.ModelAvailableDriver;
import com.dayscab.user.models.ModelCar;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.directionclasses.BaseClass;
import com.dayscab.utils.onSearchingDialogListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
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

public class RideOptionAct extends AppCompatActivity implements OnMapReadyCallback,
        onSearchingDialogListener {

    Context mContext = RideOptionAct.this;
    ActivityRideOptionBinding binding;
    ModelLogin modelLogin;
    SharedPref sharedPref;
    Dialog dialogSerach;
    GoogleMap mMap;
    String paymentType = "", pickadd = "", dropadd = "";
    private PolylineOptions lineOptions;
    private LatLng PickUpLatLng, DropOffLatLng;
    private MarkerOptions PicUpMarker, DropOffMarker;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2000;  /* 5 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    Location currentLocation;
    private String CarTypeID = "";
    private String carAmount = "";
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_option);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        PicUpMarker = new MarkerOptions().title("Pick Up Location " + pickadd)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));
        DropOffMarker = new MarkerOptions().title("Drop Off Location " + dropadd)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocationUpdates();

        getData();

        itit();

    }

    private void getData() {

        if (getIntent().getExtras() != null) {
            lineOptions = (PolylineOptions) getIntent().getExtras().get("pollyLine");
            PickUpLatLng = (LatLng) getIntent().getExtras().get("PickUp");
            DropOffLatLng = (LatLng) getIntent().getExtras().get("DropOff");
            pickadd = getIntent().getStringExtra("picadd");
            dropadd = getIntent().getStringExtra("dropadd");
        }

    }

    private void itit() {

        getCar();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnBook.setOnClickListener(v -> {
            driverSerachDialog();
        });

        binding.btnBook.setOnClickListener(v -> {
            if (Validation()) {
                bookingRequest();
            }
        });

    }

    private void bookingRequest() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.bookingRequestApi(getBookingParam());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("GetCar", "==>" + response);
                    Log.e("bookingRequest", "==>" + responseString);
                    try {
                        JSONObject object = new JSONObject(responseString);
                        if (object.getString("status").equals("1")) {
                            if (!object.getString("message").equals("already in ride")) {
                                String request_id = object.getString("request_id");
                                sharedPref.setlanguage(AppConstant.LAST, request_id);
                            }
                            driverSerachDialog();
                        } else {
                            onDriverNotFound();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void getCar() {

        HashMap<String, String> param = new HashMap<>();
        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);
        param.put("droplat", "" + DropOffLatLng.latitude);
        param.put("droplon", "" + DropOffLatLng.longitude);
        param.put("user_id", modelLogin.getResult().getId());

        Log.e("fsdafsfadsf", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarTypeListApi(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("GetCar", "==>" + responseString);
                    try {
                        JSONObject object = new JSONObject(responseString);
                        if (object.getString("status").equals("1")) {
                            Type listType = new TypeToken<ArrayList<ModelCar>>() {
                            }.getType();
                            ArrayList<ModelCar> cars = new GsonBuilder().create().fromJson(object.getString("result"), listType);
                            cars.get(0).setSelected(true);
                            binding.recycleView.setAdapter(new AdapterCarTypes(mContext, cars).Callback(RideOptionAct.this::onSelectCar));
                            onSelectCar(cars.get(0));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void driverSerachDialog() {
        dialogSerach = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogSearchDriverBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_search_driver, null, false);
        dialogSerach.setContentView(dialogBinding.getRoot());
        dialogBinding.ripple.startRippleAnimation();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getCurrentBookingApi();
            }
        }, 0, 4000);

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialogSerach.dismiss();
        });

        dialogSerach.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSerach.show();
    }

    private void driverArrivedDialog(ModelCurrentBooking data) {

        dialogSerach.dismiss();

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        AcceptDriverDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.accept_driver_dialog,
                        null, false);
        dialog.setContentView(dialogBinding.getRoot());

        timer.cancel();

        dialogBinding.btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            Intent k = new Intent(mContext, TrackAct.class);
            k.putExtra("data", data);
            startActivity(k);
        });

        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

    }

    private void getNearDriver(String id) {

        HashMap<String, String> param = new HashMap<>();
        param.put("latitude", "" + PickUpLatLng.latitude);
        param.put("longitude", "" + PickUpLatLng.longitude);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("timezone", TimeZone.getDefault().getID());
        param.put("car_type_id", id);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAvailableDrivers(param);
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
                                MarkerOptions car = new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(driver.getLat()), Double.valueOf(driver.getLon()))).title(driver.getUser_name())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_top));
                                mMap.addMarker(car);
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
    }

    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    private void onSelectCar(ModelCar car) {
        CarTypeID = car.getId();
        carAmount = car.getCharge();
        getNearDriver(car.getId());
        binding.tvRideDistance.setText(car.getDistance());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        AddDefaultMarker();
    }

    @Override
    public void onRequestAccepted(ModelCurrentBooking data) {
        driverArrivedDialog(data);
    }

    @Override
    public void onRequestCancel() {

    }

    @Override
    public void onDriverNotFound() {

    }

    private void customAlertDialog(String status, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status.equals("nodata")) {

                }
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    private HashMap<String, String> getBookingParam() {
        HashMap<String, String> param = new HashMap<>();
        param.put("car_type_id", CarTypeID);
        param.put("user_id", modelLogin.getResult().getId());
        param.put("picuplocation", pickadd);
        param.put("dropofflocation", dropadd);
        param.put("picuplat", "" + PickUpLatLng.latitude);
        param.put("pickuplon", "" + PickUpLatLng.longitude);
        param.put("droplat", "" + DropOffLatLng.latitude);
        param.put("droplon", "" + DropOffLatLng.longitude);
        param.put("shareride_type", "");
        param.put("booktype", "Now");
        param.put("status", "Now");
        param.put("passenger", "1");
        param.put("current_time", "" + ProjectUtil.getCurrentDateTime());
        param.put("timezone", "" + TimeZone.getDefault().getID());
        param.put("apply_code", "");
        param.put("payment_type", paymentType);
        param.put("vehical_type", "Reqular");
        param.put("picklatertime", "");
        param.put("picklaterdate", "");
        param.put("route_img", "");
        param.put("amount", carAmount);
        Log.e("param", param.toString().replace(", ", "&"));
        return param;
    }

    private boolean Validation() {
        if (CarTypeID.equals("")) {
            Toast.makeText(mContext, getString(R.string.select_car_type), Toast.LENGTH_SHORT).show();
            return false;
        } else if (binding.rbCash.isChecked()) {
            paymentType = "Cash";
            return true;
        } else if (binding.rbCard.isChecked()) {
            paymentType = "Card";
            return true;
        } else if (binding.rbWallet.isChecked()) {
            paymentType = "Wallet";
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.select_pay_text), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void getCurrentBookingApi() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("type", "USER");
        param.put("timezone", TimeZone.getDefault().getID());

        Log.e("asdfasdfasf", "paramHash = " + getBookingParam());

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
                            if (result.getStatus().equalsIgnoreCase("Pending")) {

                            } else if (result.getStatus().equalsIgnoreCase("Accept")) {
                                onRequestAccepted(data);
//                                Intent k = new Intent(mContext, TrackAct.class);
//                                k.putExtra("data", data);
//                                startActivity(k);
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(RideOptionAct.this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(RideOptionAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RideOptionAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(RideOptionAct.this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Log.e("hdasfkjhksdf", "StartLocationUpdate = " + locationResult.getLastLocation());
                    currentLocation = locationResult.getLastLocation();
                    // currentlocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    // showMarkerCurrentLocation(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                }
            }
        }, Looper.myLooper());

    }

}