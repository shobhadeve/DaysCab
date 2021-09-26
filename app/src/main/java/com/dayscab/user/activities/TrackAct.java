package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;
import com.dayscab.databinding.ActivityTrackBinding;

public class TrackAct extends AppCompatActivity {

    Context mContext = TrackAct.this;
    ActivityTrackBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_track);
        itit();
    }

    private void itit() {

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

    }

}