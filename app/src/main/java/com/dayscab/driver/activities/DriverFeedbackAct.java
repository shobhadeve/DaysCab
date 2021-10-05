package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityDriverFeedbackBinding;

public class DriverFeedbackAct extends AppCompatActivity {

    Context mContext = DriverFeedbackAct.this;
    ActivityDriverFeedbackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_driver_feedback);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            startActivity(new Intent(mContext,DriverHomeAct.class));
            finish();
        });

    }

}