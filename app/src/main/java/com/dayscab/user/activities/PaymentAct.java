package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.dayscab.R;
import com.dayscab.databinding.ActivityPaymentBinding;
import com.dayscab.databinding.DialogDriverArrivedDialogBinding;
import com.dayscab.databinding.EndTripDialogBinding;

public class PaymentAct extends AppCompatActivity {

    Context mContext = PaymentAct.this;
    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment);
        itit();
    }

    private void itit() {

        binding.rlPayment.setOnClickListener(v -> {
            endTripDialog();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void endTripDialog() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        EndTripDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.end_trip_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(mContext, TrackAct.class)
                    .putExtra("status", "end")
            );
            finish();
        });

        dialog.show();

    }


}