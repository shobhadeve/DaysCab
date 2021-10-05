package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityRideCancelationBinding;

public class RideCancelationAct extends AppCompatActivity {

    Context mContext = RideCancelationAct.this;
    ActivityRideCancelationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_cancelation);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            finish();
        });

    }

}