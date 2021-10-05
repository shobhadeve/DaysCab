package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dayscab.R;
import com.dayscab.databinding.ActivityDriverHomeBinding;
import com.dayscab.driver.dialogs.NewRequestDialogTaxi;
import com.dayscab.driver.dialogs.NewRequestDialogTaxiNew;
import com.dayscab.user.activities.RideHistoryAct;
import com.dayscab.utils.RequestDialogCallBackInterface;

public class DriverHomeAct extends AppCompatActivity implements RequestDialogCallBackInterface {

    Context mContext = DriverHomeAct.this;
    ActivityDriverHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_home);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NewRequestDialogTaxiNew.getInstance().Request(mContext,"");
                // NewRequestDialogTaxi.getInstance().Request(mContext,"");
            }
        }, 3000);

        itit();
    }

    private void itit() {

        binding.chlidDashboard.navbar.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHome.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(mContext, RideHistoryAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnWallet.setOnClickListener(v -> {
            startActivity(new Intent(mContext, WalletAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnEarning.setOnClickListener(v -> {
            startActivity(new Intent(mContext, EarningAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.childNavDrawer.btnPurchase.setOnClickListener(v -> {
            startActivity(new Intent(mContext, PurchasePlanAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

    }

    @Override
    public void bookingApiCalled() {

    }

}