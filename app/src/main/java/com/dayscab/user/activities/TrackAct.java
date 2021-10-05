package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.dayscab.R;
import com.dayscab.databinding.ActivityTrackBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;

public class TrackAct extends AppCompatActivity {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_track);
        status = getIntent().getStringExtra("status");
        if ("end".equals(status)) {
            binding.rlFeedback.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tripFinishDialog();
                }
            }, 8000);
        }
        itit();
    }

    private void itit() {

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            finish();
        });

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext, CancelBookingAct.class));
        });

    }

    private void tripFinishDialog() {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        DialogDriverArrivedDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.dialog_driver_arrived_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText("Your trip is finished! Please do the payment");
        dialogBinding.btnIamComing.setText("Ok");
        dialogBinding.btnIamComing.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, PaymentAct.class));
            finish();
        });

        dialogBinding.btnCall.setVisibility(View.GONE);

        dialogBinding.btnCall.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}