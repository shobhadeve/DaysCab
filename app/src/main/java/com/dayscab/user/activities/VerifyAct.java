package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityVerifyBinding;

public class VerifyAct extends AppCompatActivity {

    Context mContext = VerifyAct.this;
    ActivityVerifyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify);
        itit();
    }

    private void itit() {

        binding.btnVerify.setOnClickListener(v -> {
            startActivity(new Intent(mContext,UserHomeAct.class));
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}
