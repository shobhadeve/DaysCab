package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityVerifyBinding;
import com.dayscab.driver.activities.UploadVehicleAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppContant;

public class VerifyAct extends AppCompatActivity {

    Context mContext = VerifyAct.this;
    ActivityVerifyBinding binding;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify);
        type = getIntent().getStringExtra(AppContant.TYPE);
        itit();
    }

    private void itit() {

        binding.btnVerify.setOnClickListener(v -> {
            if(type.equals(AppContant.USER)) startActivity(new Intent(mContext, UserHomeAct.class));
            else startActivity(new Intent(mContext, UploadVehicleAct.class));
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}
