package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.WindowManager;
import com.dayscab.R;
import com.dayscab.databinding.ActivityRideOptionBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.DialogSearchDriverBinding;
import com.dayscab.user.adapters.AdapterCarTypes;

public class RideOptionAct extends AppCompatActivity {

    Context mContext = RideOptionAct.this;
    ActivityRideOptionBinding binding;
    Dialog dialogSerach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_option);
        itit();
    }

    private void itit() {

        binding.recycleView.setAdapter(new AdapterCarTypes(mContext));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnBook.setOnClickListener(v -> {
            driverSerachDialog();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                      driverArrivedDialog();
//                    startActivity(new Intent(mContext, TrackAct.class));
//                    finish();
                }
            },3000);
        });

    }

    private void driverSerachDialog() {
        dialogSerach = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogSearchDriverBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_search_driver, null, false);
        dialogSerach.setContentView(dialogBinding.getRoot());
        dialogBinding.ripple.startRippleAnimation();
        dialogBinding.btnCancel.setOnClickListener(v -> {
            dialogSerach.dismiss();
        });
        dialogSerach.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSerach.show();
    }

    private void driverArrivedDialog() {
        dialogSerach.dismiss();
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogDriverArrivedDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_driver_arrived_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnIamComing.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, TrackAct.class));
            finish();
        });

        dialogBinding.btnCall.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}