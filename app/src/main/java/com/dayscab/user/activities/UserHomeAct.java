package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.databinding.ActivityUserHomeBinding;

public class UserHomeAct extends AppCompatActivity {

    Context mContext = UserHomeAct.this;
    ActivityUserHomeBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_home);
        itit();
    }

    private void itit() {

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            navmenu();
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, RideHistoryAct.class));
        });

        binding.chlidDashboard.btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, RideOptionAct.class);
            startActivity(intent);
        });

        binding.childNavDrawer.btnSupport.setOnClickListener(v -> {
            // startActivity(new Intent(this, SupportActivity.class));
        });

    }

    public void navmenu() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

}