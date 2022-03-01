package com.dayscab.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.libraries.places.api.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApplication extends Application {

    private onRefreshSchedule schedule;
    private CountDownTimer downTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Places.initialize(getApplicationContext(), getResources().getString(R.string.places_api_key));
    }

    public MyApplication update(onRefreshSchedule schedule) {
        this.schedule = schedule;
        downTimer = new CountDownTimer(5000, 50000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("Running....");
                if (schedule != null) {
                    schedule.run();
                    System.out.println("schedule Running....");
                }
            }

            @Override
            public void onFinish() {
                downTimer.start();
            }
        };
        downTimer.start();
        return this;
    }

    public static void showConnectionDialog(Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.please_check_internet))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public static void showAlert(Context mContext, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    public static void showToast(Context mContext, String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static MyApplication get() {
        return new MyApplication();
    }

    public static void checkToken(Context mContext) {

        Log.e("checkToken", "stringResponse = Ayyaa");

        SharedPref sharedPref = SharedPref.getInstance(mContext);
        ModelLogin modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("token", modelLogin.getResult().getToken());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.chechTokenApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("0")) {
                            Log.e("checkToken", "stringResponse = " + stringResponse);
                            sessionExipredAlert(mContext, sharedPref);
                        } else {
                            Log.e("checkToken", "stringResponse = " + stringResponse);
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

    public static void sessionExipredAlert(Context mContext, SharedPref sharedPref) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.session_expire_alert_text))
                .setCancelable(false)
                .setIcon(R.drawable.ic_logo)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, StartAct.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        mContext.startActivity(loginscreen);
                        ((Activity) mContext).finishAffinity();
                        dialog.dismiss();
                    }
                }).create().show();
    }

}
