package com.dayscab.common.activties;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.driver.activities.PurchasePlanAct;
import com.dayscab.driver.activities.UploadDriverDocumentsAct;
import com.dayscab.driver.activities.UploadVehicleAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.InternetConnection;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.MyService;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    Context mContext = SplashActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int PERMISSION_ID = 44;
    boolean isDialogEnable = true;
    String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        });
        sharedPref = SharedPref.getInstance(mContext);
    }

    @Override
    protected void onResume() {

        Log.e("versionosos", "version code = " + android.os.Build.VERSION.SDK_INT);

        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (backgroundLocationPermissionApproved != 0) {
                if (isDialogEnable) {
                    isDialogEnable = false;
                    showLocationDialog();
                } else {
                    if (checkPermissions()) {
                        if (isLocationEnabled()) {
                            processNextActivity();
                        } else {
                            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        requestPermissions();
                    }
                }
            } else {
                if (checkPermissions()) {
                    if (isLocationEnabled()) {
                        processNextActivity();
                    } else {
                        Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                } else {
                    requestPermissions();
                }
            }
        } else {
            if (checkPermissions()) {
                Log.e("afdsfasfasfasd","checkPermissions ke andar");
                if (isLocationEnabled()) {
                    Log.e("afdsfasfasfasd","isLocationEnabled ke andar");
                    processNextActivity();
                } else {
                    Log.e("afdsfasfasfasd","ACTION_LOCATION_SOURCE_SETTINGS ke andar");
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            } else {
                Log.e("afdsfasfasfasd","requestPermissions ke andar");
                requestPermissions();
            }
        }
        super.onResume();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                processNextActivity();
            }
        }
    }

    private void processNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    Log.e("afdsfasfasfasd","StartAct ke andar");
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                    Log.e("afdsfasfasfasd","modelLogin = " + modelLogin);
                    Log.e("afdsfasfasfasd","modelLogin gson = " + new Gson().toJson(modelLogin));
                    if (AppConstant.DRIVER.equalsIgnoreCase(modelLogin.getResult().getType())) {
                        if (InternetConnection.checkConnection(mContext)) {
                            Log.e("afdsfasfasfasd","IF AppConstant.DRIVER");
                            getProfileApiCall();
                        } else {
                            MyApplication.showConnectionDialog(mContext);
                        }
                    } else if (AppConstant.USER.equalsIgnoreCase(modelLogin.getResult().getType())) {
                        Log.e("afdsfasfasfasd","ELSE AppConstant.USER");
                        startActivity(new Intent(mContext, UserHomeAct.class));
                        finish();
                    }
                } else {
                    Log.e("afdsfasfasfasd","StartAct ke andar else ");
                    Intent i = new Intent(SplashActivity.this, StartAct.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);

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

                            ContextCompat.startForegroundService(SplashActivity.this, new Intent(SplashActivity.this, MyService.class));

                            Log.e("adfasdfss", "getDriver_lisence_img = " + modelLogin.getResult().getDriver_lisence_img());
                            if (modelLogin.getResult().getStep().equals("1")) {
                                startActivity(new Intent(mContext, UploadVehicleAct.class));
                                finish();
                            } else if (modelLogin.getResult().getStep().equals("2")) {
                                if (modelLogin.getResult().getDriver_lisence_img() == null ||
                                        "".equals(modelLogin.getResult().getDriver_lisence_img()) ||
                                        modelLogin.getResult().getDriver_lisence_img()
                                                .equals("https://dayscab.com/dayscab_taxi/uploads/images/")) {
                                    startActivity(new Intent(mContext, UploadDriverDocumentsAct.class));
                                    finish();
                                } else {
                                    if (modelLogin.getResult().getRide_count() != null &&
                                            !modelLogin.getResult().getRide_count().equals("0")) {
                                        if (registerId.equals(modelLogin.getResult().getRegister_id())) {
                                            startActivity(new Intent(mContext, DriverHomeAct.class));
                                            finish();
                                        } else {
                                            sharedPref.clearAllPreferences();
                                            startActivity(new Intent(mContext, LoginAct.class));
                                            finish();
                                        }
                                    } else {
                                        startActivity(new Intent(mContext, PurchasePlanAct.class)
                                                .putExtra("which", "splash")
                                        );
                                        finish();
                                        // Toast.makeText(mContext, mContext.getString(R.string.please_purchase_plan), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        } else {
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

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle("Need Important Permission");
        builder.setMessage("Click on Ok than -> \n\n Choose Location Permission ->" +
                "\n Allow all the time \n\n Than come back to Application");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isDialogEnable = true;
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).create().show();
    }

}