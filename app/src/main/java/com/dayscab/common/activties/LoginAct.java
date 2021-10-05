package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityLoginBinding;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.driver.activities.SignUpDriverAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppContant;

public class LoginAct extends AppCompatActivity {

    Context mContext = LoginAct.this;
    ActivityLoginBinding binding;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        type = getIntent().getStringExtra(AppContant.TYPE);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSignin.setOnClickListener(v -> {
            if (type.equals(AppContant.USER)) startActivity(new Intent(mContext, UserHomeAct.class));
            else startActivity(new Intent(mContext, DriverHomeAct.class));
        });

        binding.ivForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ForgotPasswordAct.class));
        });

        binding.btSignup.setOnClickListener(v -> {
            if (type.equals(AppContant.USER)) startActivity(new Intent(mContext, SignUpAct.class).putExtra(AppContant.TYPE, AppContant.USER));
            else startActivity(new Intent(mContext, SignUpDriverAct.class));
        });

    }

}