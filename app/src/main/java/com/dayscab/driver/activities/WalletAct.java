package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityWalletBinding;

public class WalletAct extends AppCompatActivity {

    Context mContext = WalletAct.this;
    ActivityWalletBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_wallet);
        itit();
    }

    private void itit() {
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
    }

}