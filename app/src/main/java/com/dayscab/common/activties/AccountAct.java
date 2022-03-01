package com.dayscab.common.activties;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.adapters.AdapterAccounts;
import com.dayscab.common.models.AccountModel;
import com.dayscab.databinding.ActivityAccountBinding;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAct extends AppCompatActivity {

    Context mContext = AccountAct.this;
    ActivityAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);

        MyApplication.checkToken(mContext);

        itit();
    }

    private void itit() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        getAllAccountInfo();

    }

    private void getAllAccountInfo() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllAccountInformation();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {
                            AccountModel accountModel = new Gson().fromJson(stringResponse, AccountModel.class);
                            AdapterAccounts adapterAccounts = new AdapterAccounts(mContext, accountModel.getResult());
                            binding.listOptions.setAdapter(adapterAccounts);
                        } else {
                            AdapterAccounts adapterAccounts = new AdapterAccounts(mContext, null);
                            binding.listOptions.setAdapter(adapterAccounts);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
                    }

                    //  Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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