package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityEndTripDriver3Binding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndTripDriverAct extends AppCompatActivity {

    Context mContext = EndTripDriverAct.this;
    ActivityEndTripDriver3Binding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private ModelCurrentBooking data;
    ModelCurrentBookingResult result;
    String requestId;
    private String pickUp, dropOff, payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_trip_driver3);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        try {
            if (getIntent() != null) {

                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                Log.e("dsfsdfdsf", "ModelCurrentBooking = " + new Gson().toJson(data));
                result = data.getResult().get(0);
                requestId = result.getId();

                Log.e("dsfsdfdsf", "requestId = " + result.getId());
                pickUp = result.getPicuplocation();
                dropOff = result.getDropofflocation();
                payType = result.getPaymentType();

                binding.tvFrom.setText(pickUp);
                binding.tvDestination.setText(dropOff);
                binding.tvTotalPay.setText(result.getTotal_trip_cost() + " " + AppConstant.CURRENCY);
                binding.tvWaitingCharge.setText(result.getPer_minute_charge() + " " + AppConstant.CURRENCY);
                binding.tvWaitingTime.setText(result.getWaitng_time() + " Min");

                if(result.getPer_minute() == null) {
                    binding.tvPerMintueWaiting.setText("0 " + AppConstant.CURRENCY);
                } else {
                    binding.tvPerMintueWaiting.setText(result.getPer_minute() + " " + AppConstant.CURRENCY);
                }

                if(result.getPer_minute_charge() == null) {
                    binding.tvWaitingCharge.setText("0 " + AppConstant.CURRENCY);
                } else {
                    binding.tvWaitingCharge.setText(result.getPer_minute_charge() + " " + AppConstant.CURRENCY);
                }

                binding.tvDistance.setText(result.getDistance() + " Km");
                binding.tvDistanceCost.setText(result.getAmount() + " " + " " + AppConstant.CURRENCY);
                binding.tvPayType.setText(result.getPaymentType().toUpperCase());

                if (result.getBooktype().equals("POOL")) {
                    binding.rlPool.setVisibility(View.VISIBLE);
                    binding.tvPassenger.setText(result.getNo_of_passenger() + " Passenger");
                    double passengerTotal = Double.parseDouble(result.getTotal_trip_cost()) * Double.parseDouble(result.getNo_of_passenger());
                    binding.tvTotalPay.setText(passengerTotal + " " + AppConstant.CURRENCY);
                } else {
                    binding.rlPool.setVisibility(View.GONE);
                }


            }
        } catch (Exception e) {
        }

        itit();

    }

    private void itit() {

        binding.btnCollectPay.setOnClickListener(view -> {
            AcceptCancel("Finish");
        });

    }

    public void AcceptCancel(String status) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("status", status);
        map.put("cancel_reaison", "");
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ProjectUtil.pauseProgressDialog();
                        ProjectUtil.clearNortifications(mContext);
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        finishAffinity();
                        startActivity(new Intent(mContext, DriverHomeAct.class));
                    } else {
                        MyApplication.showToast(mContext, getString(R.string.req_cancelled));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }
        });

    }

}