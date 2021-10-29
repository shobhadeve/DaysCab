package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.databinding.ActivityStartBinding;
import com.dayscab.utils.AppConstant;

public class StartAct extends AppCompatActivity {

    Context mContext = StartAct.this;
    ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start);
        itit();
    }

    private void itit() {

        binding.btUser.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginAct.class)
                    .putExtra(AppConstant.TYPE, AppConstant.USER)
            );
        });

        binding.btDriver.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginAct.class)
                    .putExtra(AppConstant.TYPE, AppConstant.DRIVER)
            );
        });

    }

}

