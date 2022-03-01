package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.databinding.ActivityAppSettingsBinding;
import com.dayscab.driver.activities.ManageVehicleAct;
import com.dayscab.driver.activities.UserFeedbackAct;
import com.dayscab.utils.MyApplication;

public class AppSettingsAct extends AppCompatActivity {

    Context mContext = AppSettingsAct.this;
    ActivityAppSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings);
        MyApplication.checkToken(mContext);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvManageVehicle.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ManageVehicleAct.class));
        });

        binding.cvVolume.setOnClickListener(v -> {
            startActivity(new Intent(mContext, IncDecVolumAct.class));
        });

        binding.cvStatistics.setOnClickListener(v -> {
            startActivity(new Intent(mContext, StatisticsAct.class));
        });

        binding.cvEmergency.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EmergencyContactAct.class));
        });

        binding.cvUserFeedback.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UserFeedbackAct.class));
        });

        binding.cvAccount.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AccountAct.class));
        });

    }


}