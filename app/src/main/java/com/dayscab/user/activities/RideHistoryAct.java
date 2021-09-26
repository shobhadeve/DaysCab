package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityRideHistoryBinding;
import com.dayscab.user.adapters.AdapterRideHistory;

public class RideHistoryAct extends AppCompatActivity {

    Context mContext = RideHistoryAct.this;
    ActivityRideHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ride_history);
        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.rvRideHistory.setAdapter(new AdapterRideHistory(mContext));

    }

}

