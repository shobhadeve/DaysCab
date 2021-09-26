package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivitySignUpBinding;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up);
        itit();
    }

    private void itit() {

        binding.btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext,VerifyAct.class));
        });

        binding.ivLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext,LoginAct.class));
            finish();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}