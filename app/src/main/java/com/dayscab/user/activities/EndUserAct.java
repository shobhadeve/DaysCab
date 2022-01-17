package com.dayscab.user.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityEndUserBinding;
import com.dayscab.driver.activities.DriverFeedbackAct;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndUserAct extends AppCompatActivity {

    Context mContext = EndUserAct.this;
    ActivityEndUserBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    String pickUp, dropOff;
    private ModelLogin.Result driverDetails;
    private String driverId, payType;
    ModelLogin modelLogin;
    SharedPref sharedPref;

    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_user);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        PaystackSdk.initialize(mContext);

        itit();
    }

    private void itit() {

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            pickUp = result.getPicuplocation();
            dropOff = result.getDropofflocation();
            payType = result.getPaymentType();

            binding.tvFrom.setText(pickUp);
            binding.tvDestination.setText(dropOff);
            binding.tvPay.setText("Payment Amount : " + result.getAmount() + " " + AppConstant.CURRENCY);
            binding.tvPayType.setText(result.getPaymentType().toUpperCase());

            Log.e("zddsasdfdasf", "pickUp = " + pickUp);
            Log.e("zddsasdfdasf", "dropOff = " + dropOff);
            Log.e("zddsasdfdasf", "payType = " + payType);

            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);

        }

        binding.btnPay.setOnClickListener(v -> {
            if (result.getPaymentType().equals("Card")) {
                getWebviewForPayment();
            } else {
                doPaymentApi();
            }
        });

    }

    private void getWebviewForPayment() {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", modelLogin.getResult().getEmail());
        map.put("amount", result.getAmount());

        Log.e("getWebviewForPayment", "getWebviewForPayment = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getPaymentApiUrl(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("getWebviewForPayment", "stringResponse = " + stringResponse);

                    JSONObject resultObj = jsonObject.getJSONObject("result");
                    JSONObject dataObj = resultObj.getJSONObject("data");
                    String webUrl = dataObj.getString("authorization_url");

                    startActivity(new Intent(mContext, PaymentWebviewAct.class)
                            .putExtra("url", webUrl.trim())
                            .putExtra("data", data)
                    );
                    finish();

                    Log.e("asdasdasdasd", "Webview Url = " + webUrl);

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

    public void doPaymentApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("payment_type", result.getPaymentType());
        map.put("amount", result.getAmount());
        map.put("user_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("time_zone", TimeZone.getDefault().getID());
        map.put("currency", AppConstant.CURRENCY);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.paymentApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        finish();
                        startActivity(new Intent(mContext, DriverFeedbackAct.class)
                                .putExtra("data", data)
                        );
                        MyApplication.showToast(mContext, getString(R.string.trip_finished));
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