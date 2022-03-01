package com.dayscab.user.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.databinding.ActivityRideDetailsBinding;
import com.dayscab.driver.models.ModelHistory;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.google.gson.Gson;

public class RideDetailsAct extends AppCompatActivity {

    Context mContext = RideDetailsAct.this;
    ActivityRideDetailsBinding binding;
    ModelHistory.Result data;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_details);
        MyApplication.checkToken(mContext);
        data = (ModelHistory.Result) getIntent().getSerializableExtra("data");
        Log.e("asdasdasdasd", new Gson().toJson(data));
        type = getIntent().getStringExtra("type");
        setData();
        itit();
    }

    private void setData() {

        if (type.equals(AppConstant.DRIVER)) {
            binding.tvDriverOrUser.setText("User");
            binding.tvDriverName.setText(data.getUser_details().get(0).getUser_name());
            binding.tvDriverName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
            binding.tvPayType.setText(data.getPayment_type());
            Glide.with(mContext)
                    .load(data.getUser_details().get(0).getProfile_image())
                    .placeholder(R.drawable.user_ic).error(R.drawable.user_ic)
                    .into(binding.ivDriverPic);
        } else {
            binding.tvDriverOrUser.setText("Driver");
            binding.tvMobile.setText("Car Type : " + data.getCar_name());
            binding.tvEmail.setText("Car Number : " + data.getCar_number());
            binding.tvDriverName.setText(data.getDriver_details().get(0).getUser_name());
            binding.tvPayType.setText(data.getPayment_type());
            Glide.with(mContext)
                    .load(data.getDriver_details().get(0).getProfile_image())
                    .placeholder(R.drawable.user_ic).error(R.drawable.user_ic)
                    .into(binding.ivDriverPic);
        }

        binding.tvDateTime.setText(data.getAccept_time());
        binding.tvPayType.setText(data.getPayment_type());
        binding.tvStatus.setText(data.getStatus());
        binding.tvFrom.setText(data.getPicuplocation());
        binding.etDestination.setText(data.getDropofflocation());

        binding.tvTotalPay.setText(data.getTotal_trip_cost() + " " + AppConstant.CURRENCY);
      //  binding.tvWaitingCharge.setText(data.getPer_minute_charge() + " " + AppConstant.CURRENCY);
        binding.tvWaitingTime.setText(data.getWaitng_time() + " Min");
       // binding.tvPerMintueWaiting.setText(data.getPer_minute() + " " + AppConstant.CURRENCY);
        binding.tvDistance.setText(data.getDistance() + " Km");
        binding.tvDistanceCost.setText(data.getAmount() + " " + " " + AppConstant.CURRENCY);
        binding.tvPayType.setText(data.getPayment_type().toUpperCase());

        if(data.getPer_minute() == null) {
            binding.tvPerMintueWaiting.setText("0 " + AppConstant.CURRENCY);
        } else {
            binding.tvPerMintueWaiting.setText(data.getPer_minute() + " " + AppConstant.CURRENCY);
        }

        if(data.getPer_minute_charge() == null) {
            binding.tvWaitingCharge.setText("0 " + AppConstant.CURRENCY);
        } else {
            binding.tvWaitingCharge.setText(data.getPer_minute_charge() + " " + AppConstant.CURRENCY);
        }

    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

}