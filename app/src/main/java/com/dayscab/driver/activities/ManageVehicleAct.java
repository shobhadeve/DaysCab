package com.dayscab.driver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityManageVehicleBinding;
import com.dayscab.driver.adapters.AdapterManageVehicle;
import com.dayscab.driver.models.ModelVehicles;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicleAct extends AppCompatActivity {

    Context mContext = ManageVehicleAct.this;
    ActivityManageVehicleBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_vehicle);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
        getAllVehicles();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.addVehicle.setOnClickListener(v -> {
            startActivity(new Intent(mContext, AddVehicleAct.class));
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllVehicleNew();
    }

    private void getAllVehicles() {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());

        Log.e("getAllVehicles", "getAllVehicles = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllVehicleApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelVehicles modelVehicles = new Gson().fromJson(stringResponse, ModelVehicles.class);
                        AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, modelVehicles.getResult());
                        binding.rvVehicle.setAdapter(adapterManageVehicle);
                    } else {
                        AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, null);
                        binding.rvVehicle.setAdapter(adapterManageVehicle);
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

    private void getAllVehicleNew() {
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());

        Log.e("getAllVehicles", "getAllVehicles = " + map);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllVehicleApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelVehicles modelVehicles = new Gson().fromJson(stringResponse, ModelVehicles.class);
                        AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, modelVehicles.getResult());
                        binding.rvVehicle.setAdapter(adapterManageVehicle);
                    } else {
                        AdapterManageVehicle adapterManageVehicle = new AdapterManageVehicle(mContext, null);
                        binding.rvVehicle.setAdapter(adapterManageVehicle);
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