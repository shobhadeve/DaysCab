package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordAct extends AppCompatActivity {

    Context mContext = ForgotPasswordAct.this;
    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot_password);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSend.setOnClickListener(v -> {
            finish();
        });

    }

}