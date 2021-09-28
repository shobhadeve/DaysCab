package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityUploadVehicleBinding;

public class UploadVehicleAct extends AppCompatActivity {

    Context mContext = UploadVehicleAct.this;
    ActivityUploadVehicleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_upload_vehicle);
        itit();
    }

    private void itit() {

        binding.btnNext.setOnClickListener(v -> {
            startActivity(new Intent(mContext,AddBankAccAct.class));
        });

    }

}