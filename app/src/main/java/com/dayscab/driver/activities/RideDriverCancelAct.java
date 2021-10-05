package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityRideDriverCancelBinding;

public class RideDriverCancelAct extends AppCompatActivity {

    Context mContext = RideDriverCancelAct.this;
    ActivityRideDriverCancelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_driver_cancel);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            startActivity(new Intent(mContext,DriverHomeAct.class));
            finish();
        });

    }

}