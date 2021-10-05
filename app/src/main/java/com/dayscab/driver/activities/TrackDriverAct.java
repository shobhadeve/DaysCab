package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.dayscab.R;
import com.dayscab.databinding.ActivityTrackBinding;
import com.dayscab.databinding.ActivityTrackDriverBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.TripStatusDailogBinding;
import com.dayscab.user.activities.PaymentAct;

public class TrackDriverAct extends AppCompatActivity {

    Context mContext = TrackDriverAct.this;
    ActivityTrackDriverBinding binding;
    String status = "Start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track_driver);
        itit();
    }

    private void itit() {

        binding.ivCancelTrip.setOnClickListener(v -> {
            startActivity(new Intent(mContext,RideDriverCancelAct.class));
        });

        binding.icCall.setOnClickListener(v -> {
          //  finish();
        });

        binding.btnStatus.setOnClickListener(v -> {
            if(status.equals("Start")) {
                startEndTripDialog("Are you sure you want to start the trip?");
            } else {
                startEndTripDialog("Are you sure you want to finish the trip?");
            }
        });

    }

    private void startEndTripDialog(String text) {
        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        TripStatusDailogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.trip_status_dailog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvText.setText(text);

        dialogBinding.btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            if(status.equals("Start")) {
                status = "finish";
                binding.btnStatus.setText("Finish");
            } else {
                startActivity(new Intent(mContext,EndTripDriverAct.class));
                finish();
            }
        });

        dialogBinding.btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}