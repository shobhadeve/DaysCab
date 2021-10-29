package com.dayscab.common.activties;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.driver.activities.SignUpDriverAct;
import com.dayscab.driver.activities.UploadDriverDocumentsAct;
import com.dayscab.driver.activities.UploadVehicleAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.SharedPref;
import com.google.android.gms.location.LocationRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends AppCompatActivity {

    Context mContext = SplashActivity.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    int PERMISSION_ID = 44;
    private boolean isDialogEnable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
        printHashKey(mContext);
    }

    public static void printHashKey(Context pContext) {
        Log.i("dsadadsdad", "printHashKey() Hash Key: aaya ander");
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("dsadadsdad", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("dsadadsdad", "printHashKey()", e);
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
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

    @Override
    protected void onResume() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int backgroundLocationPermissionApproved = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (backgroundLocationPermissionApproved != 0) {
                if (isDialogEnable) {
                    isDialogEnable = false;
                    showLocationDialog();
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
        super.onResume();
    }

    private void processNextActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getBooleanValue(AppConstant.IS_REGISTER)) {
                    modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
                    if (modelLogin.getResult().getType().equals(AppConstant.USER)) {
                        startActivity(new Intent(mContext, UserHomeAct.class));
                        finish();
                    } else {
                        Log.e("sadasdsd", "modelLogin.getResult().getStep() = " + modelLogin.getResult().getStep());
                        if ("1".equals(modelLogin.getResult().getStep())) {
                            startActivity(new Intent(mContext, UploadVehicleAct.class));
                            finish();
                        } else if ("2".equals(modelLogin.getResult().getStep())) {
                            if (modelLogin.getResult().getDriver_lisence_img() == null ||
                                    modelLogin.getResult().getDriver_lisence_img().equals("")) {
                                startActivity(new Intent(mContext, UploadDriverDocumentsAct.class));
                                finish();
                            } else {
                                startActivity(new Intent(mContext, DriverHomeAct.class));
                                finish();
                            }
                        } else if ("0".equals(modelLogin.getResult().getStep())) {
                            startActivity(new Intent(mContext, SignUpDriverAct.class));
                            finish();
                        }
                    }
                } else {
                    startActivity(new Intent(mContext, StartAct.class));
                    finish();
                }
            }
        }, 2000);

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
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).create().show();
    }

}