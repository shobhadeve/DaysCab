package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityRideCancelationBinding;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideCancelationAct extends AppCompatActivity {

    Context mContext = RideCancelationAct.this;
    ActivityRideCancelationBinding binding;
    String requestId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_cancelation);
        requestId = getIntent().getStringExtra("id");
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.cb1.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb2.setOnClickListener(v -> {
            binding.cb1.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb3.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb1.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb4.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb1.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb5.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb1.setChecked(false);
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.cb1.isChecked()) {
                AcceptCancel("Don't want to share");
            } else if (binding.cb2.isChecked()) {
                AcceptCancel("Can't contact the driver");
            } else if (binding.cb3.isChecked()) {
                AcceptCancel("Driver is late");
            } else if (binding.cb4.isChecked()) {
                AcceptCancel("The price is not reasonable");
            } else if (binding.cb5.isChecked()) {
                AcceptCancel("Pickup address is incorrect");
            } else {
                Toast.makeText(mContext, getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void AcceptCancel(String reason) {

        HashMap<String, String> map = new HashMap<>();
        map.put("request_id", requestId);
        map.put("cancel_reaison", reason);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.cancelRideApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        startActivity(new Intent(mContext, UserHomeAct.class));
                    } else {
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