package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityAddBankAccBinding;
import com.dayscab.databinding.ActivityUploadVehicleBinding;

public class AddBankAccAct extends AppCompatActivity {

    Context mContext = AddBankAccAct.this;
    ActivityAddBankAccBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank_acc);
        itit();
    }

    private void itit() {

        binding.btnAddAccount.setOnClickListener(v -> {
            startActivity(new Intent(mContext, DriverHomeAct.class));
        });

    }


}