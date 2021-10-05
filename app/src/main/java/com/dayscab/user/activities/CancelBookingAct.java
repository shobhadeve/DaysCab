package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityCancelBookingBinding;

public class CancelBookingAct extends AppCompatActivity {

    Context mContext = CancelBookingAct.this;
    ActivityCancelBookingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cancel_booking);
        itit();
    }

    private void itit() {



        binding.btnCancel.setOnClickListener(v -> {
            finish();
        });

        binding.btCancelBooking.setOnClickListener(v -> {
           startActivity(new Intent(mContext, RideCancelationAct.class));
           finish();
        });

    }

}