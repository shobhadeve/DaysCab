package com.dayscab.user.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dayscab.R;
import com.dayscab.common.activties.LoginAct;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityRideHistoryBinding;
import com.dayscab.driver.models.ModelHistory;
import com.dayscab.user.adapters.AdapterRideHistory;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideHistoryAct extends AppCompatActivity {

    Context mContext = RideHistoryAct.this;
    ActivityRideHistoryBinding binding;
    String type;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String registerId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_history);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        type = getIntent().getStringExtra("type");

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));

        itit();
    }

    private void itit() {

        getHistory();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void getHistory() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("type", type);

        Log.e("sadasddasd", "paramHash = " + paramHash);
        Log.e("sadasddasd", "type = " + type);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getHistoryApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("asfddasfasdf", "response = " + response);
                            Log.e("asfddasfasdf", "stringResponse = " + stringResponse);

                            ModelHistory modelHistory = new Gson().fromJson(stringResponse, ModelHistory.class);

                            if (!registerId.equals(modelHistory.getRegister_id())) {
                                logoutAlertDialog();
                            }

                            AdapterRideHistory adapterRideHistory = new AdapterRideHistory(mContext, modelHistory.getResult(), type);
                            binding.rvRideHistory.setAdapter(adapterRideHistory);

                        } else {
                            Log.e("stringResponsestringResponse", "response = " + stringResponse);
                            if (!registerId.equals(jsonObject.getString("register_id"))) {
                                logoutAlertDialog();
                            }
                            AdapterRideHistory adapterRideHistory = new AdapterRideHistory(mContext, null, type);
                            binding.rvRideHistory.setAdapter(adapterRideHistory);
                            Toast.makeText(mContext, getString(R.string.no_history_found), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Your session is expired Please login Again!")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sharedPref.clearAllPreferences();
                                finishAffinity();
                                startActivity(new Intent(mContext, StartAct.class));
                                dialog.dismiss();
                            }
                        }).create().show();
    }

}

