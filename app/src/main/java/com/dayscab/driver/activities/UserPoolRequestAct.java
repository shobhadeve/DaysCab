package com.dayscab.driver.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUserPoolRequestBinding;
import com.dayscab.driver.adapters.AdapterUserPoolRequests;
import com.dayscab.driver.models.ModelUserPools;
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

public class UserPoolRequestAct extends AppCompatActivity {

    Context mContext = UserPoolRequestAct.this;
    ActivityUserPoolRequestBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_pool_request);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getAllUserPoolRequestApi();

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPoolRequestNew();
            }
        });

    }

    private void getAllUserPoolRequestApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getPoolRequestApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {

                        ModelUserPools modelPoolList = new Gson().fromJson(stringResponse, ModelUserPools.class);

                        for (int i = 0; i < modelPoolList.getResult().size(); i++) {
                            if (modelPoolList.getResult().get(i).getCancel_driver_id() != null ||
                                    !(modelPoolList.getResult().get(i).getCancel_driver_id().equals(""))) {
                                String[] strArr = modelPoolList.getResult().get(i).getCancel_driver_id().split(",");
                                for (int j = 0; j < strArr.length; j++) {
                                    if (strArr[i].equals(modelLogin.getResult().getId())) {
                                        modelPoolList.getResult().remove(i);
                                        i--; break;
                                    }
                                    Log.e("asdfadfafsa", "strArrstrArr = " + strArr[j]);
                                }
                            }
                        }

                        AdapterUserPoolRequests adapterOfferPool = new AdapterUserPoolRequests(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        // Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();

                    } else {
                        AdapterUserPoolRequests adapterOfferPool = new AdapterUserPoolRequests(mContext, null);
                        binding.rvPools.setAdapter(adapterOfferPool);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }

        });

    }

    private void getAllPoolRequestNew() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getPoolRequestApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("offerPoolApi", "offerPoolApi = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelUserPools modelPoolList = new Gson().fromJson(stringResponse, ModelUserPools.class);
                        AdapterUserPoolRequests adapterOfferPool = new AdapterUserPoolRequests(mContext, modelPoolList.getResult());
                        binding.rvPools.setAdapter(adapterOfferPool);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                    } else {
                        AdapterUserPoolRequests adapterOfferPool = new AdapterUserPoolRequests(mContext, null);
                        binding.rvPools.setAdapter(adapterOfferPool);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                binding.swipLayout.setRefreshing(false);
                ProjectUtil.pauseProgressDialog();
                Log.e("sfasfsdfdsf", "Exception = " + t.getMessage());
            }

        });
    }


}