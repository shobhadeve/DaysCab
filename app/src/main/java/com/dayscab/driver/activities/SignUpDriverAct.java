package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.utils.AppContant;
import com.dayscab.common.activties.VerifyAct;
import com.dayscab.R;
import com.dayscab.databinding.ActivitySignUpDriverBinding;

public class SignUpDriverAct extends AppCompatActivity {

    Context mContext = SignUpDriverAct.this;
    ActivitySignUpDriverBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up_driver);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(mContext, VerifyAct.class)
                    .putExtra(AppContant.TYPE, AppContant.DRIVER)
            );
        });

    }

}