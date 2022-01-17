package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityPurchasePlanBinding;
import com.dayscab.driver.adapters.AdapterPlans;
import com.dayscab.driver.models.ModelPlans;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PurchasePlanAct extends AppCompatActivity {

    Context mContext = PurchasePlanAct.this;
    ActivityPurchasePlanBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String whichScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchase_plan);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        whichScreen = getIntent().getStringExtra("which");

        if ("splash".equals(whichScreen)) {
            binding.ivBack.setVisibility(View.GONE);
        } else {
            binding.ivBack.setVisibility(View.VISIBLE);
        }

        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        itit();
    }

    private void itit() {

        getPlanApiCall();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });


    }

    private void getPlanApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("user_id",modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getPlanApiCall(hashMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {
                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "response = " + stringResponse);

                            ModelPlans modelPlans = new Gson().fromJson(stringResponse, ModelPlans.class);
                            AdapterPlans adapterPlans = new AdapterPlans(mContext, modelPlans.getResult());
                            binding.rvPlans.setAdapter(adapterPlans);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

}