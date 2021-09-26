package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityStartBinding;

public class StartAct extends AppCompatActivity {

    Context mContext = StartAct.this;
    ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_start);
        itit();
    }

    private void itit() {

        binding.btUser.setOnClickListener(v -> {
            startActivity(new Intent(mContext,LoginAct.class));
        });

        binding.btDriver.setOnClickListener(v -> {
            // startActivity(new Intent(mContext,LoginAct.class));
        });

    }

}

