package com.dayscab.utils;

import android.app.Application;
import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.dayscab.R;
import com.google.android.libraries.places.api.Places;

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

    public static void showToast(Context mContext, String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static MyApplication get() {
        return new MyApplication();
    }

}
