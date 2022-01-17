package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.databinding.ActivityRideDetailsBinding;
import com.dayscab.driver.models.ModelHistory;
import com.dayscab.utils.AppConstant;

public class RideDetailsAct extends AppCompatActivity {

    Context mContext = RideDetailsAct.this;
    ActivityRideDetailsBinding binding;
    ModelHistory.Result data;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_details);
        data = (ModelHistory.Result) getIntent().getSerializableExtra("data");
        type = getIntent().getStringExtra("type");
        setData();
        itit();
    }

    private void setData() {

        if (type.equals(AppConstant.DRIVER)) {
            binding.tvDriverOrUser.setText("User");
            binding.tvMobile.setText(data.getUser_details().get(0).getMobile());
            binding.tvEmail.setText(data.getUser_details().get(0).getEmail());
            binding.tvDriverName.setText(data.getUser_details().get(0).getUser_name());
            Glide.with(mContext)
                    .load(data.getUser_details().get(0).getProfile_image())
                    .placeholder(R.drawable.user_ic).error(R.drawable.user_ic).into(binding.ivDriverPic);
        } else {
            binding.tvDriverOrUser.setText("Driver");
            binding.tvMobile.setText(data.getDriver_details().get(0).getMobile());
            binding.tvEmail.setText(data.getDriver_details().get(0).getEmail());
            binding.tvDriverName.setText(data.getDriver_details().get(0).getUser_name());
            Glide.with(mContext)
                    .load(data.getDriver_details().get(0).getProfile_image())
                    .placeholder(R.drawable.user_ic).error(R.drawable.user_ic).into(binding.ivDriverPic);
        }

        binding.tvDateTime.setText(data.getAccept_time());
        binding.tvPayType.setText(data.getPayment_type());
        binding.tvAmount.setText(data.getAmount() + " " + AppConstant.CURRENCY);
        binding.tvStatus.setText(data.getStatus());
        binding.tvFrom.setText(data.getPicuplocation());
        binding.etDestination.setText(data.getDropofflocation());

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}