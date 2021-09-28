package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivitySignUpBinding;
import com.dayscab.utils.AppContant;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        type = getIntent().getStringExtra(AppContant.TYPE);
        itit();
    }

    private void itit() {

        binding.btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext, VerifyAct.class)
                    .putExtra(AppContant.TYPE, type)
            );
        });

        binding.ivLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginAct.class));
            finish();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}