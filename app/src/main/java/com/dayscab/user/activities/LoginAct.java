package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityLoginBinding;

public class LoginAct extends AppCompatActivity {

    Context mContext = LoginAct.this;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSignin.setOnClickListener(v -> {
            startActivity(new Intent(mContext,UserHomeAct.class));
        });

        binding.ivForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ForgotPasswordAct.class));
        });

        binding.btSignup.setOnClickListener(v -> {
            startActivity(new Intent(mContext,SignUpAct.class));
        });

    }

}