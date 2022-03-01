package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.databinding.ActivityAppSettingsActUserBinding;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;

public class AppSettingsActUser extends AppCompatActivity {

    Context mContext = AppSettingsActUser.this;
    ActivityAppSettingsActUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_settings_act_user);
        MyApplication.checkToken(mContext);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvAccountDetails.setOnClickListener(v -> {
            startActivity(new Intent(mContext, UpdateProfileUserAct.class));
        });

        binding.cvSignOut.setOnClickListener(v -> {
            ProjectUtil.logoutAppDialog(mContext);
        });

        binding.cvAccount.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AccountAct.class));
        });

        binding.cvSecurity.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://dayscab.com/dayscab_taxi/security.html")
            );
        });

        binding.cvPravicy.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WebviewAct.class)
                    .putExtra("url", "https://dayscab.com/dayscab_taxi/privacy-policy.html")
            );
        });

    }

}