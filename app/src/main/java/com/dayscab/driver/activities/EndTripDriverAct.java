package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.dayscab.R;
import com.dayscab.databinding.ActivityEndTripDriverBinding;

public class EndTripDriverAct extends AppCompatActivity {

    Context mContext = EndTripDriverAct.this;
    ActivityEndTripDriverBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_trip_driver);
        itit();
    }

    private void itit() {

        binding.btnCollectPay.setOnClickListener(v -> {
            startActivity(new Intent(mContext, DriverFeedbackAct.class));
            finish();
        });

    }


}


