package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityPurchasePlanBinding;

public class PurchasePlanAct extends AppCompatActivity {

    Context mContext = PurchasePlanAct.this;
    ActivityPurchasePlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_plan);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.tv25.setOnClickListener(v -> {
            finish();
        });

        binding.tv50.setOnClickListener(v -> {
            finish();
        });

        binding.tv100.setOnClickListener(v -> {
            finish();
        });

    }


}