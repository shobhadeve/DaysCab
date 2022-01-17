package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityDriverFeedbackBinding;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverFeedbackAct extends AppCompatActivity {

    Context mContext = DriverFeedbackAct.this;
    ActivityDriverFeedbackBinding binding;
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;
    String pickUp, dropOff;
    private ModelLogin.Result driverDetails;
    private String driverId, payType;
    ModelLogin modelLogin;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_feedback);
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        if (getIntent() != null) {

            data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
            result = data.getResult().get(0);
            pickUp = result.getPicuplocation();
            dropOff = result.getDropofflocation();
            payType = result.getPaymentType();
            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);

            binding.tvFrom1.setText(pickUp);
            binding.tvDestination.setText(dropOff);
            binding.tvName.setText(driverDetails.getUser_name());

            Log.e("zddsasdfdasf", "pickUp = " + pickUp);
            Log.e("zddsasdfdasf", "dropOff = " + dropOff);
            Log.e("zddsasdfdasf", "payType = " + payType);

            Glide.with(mContext).load(driverDetails.getProfile_image()).into(binding.ivDriverPropic3);

        }

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etComment.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_comments), Toast.LENGTH_SHORT).show();
            } else {
                callFeedbackApi();
            }
        });

    }

    private void callFeedbackApi() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("request_id", result.getId());
        param.put("driver_id", driverDetails.getId());
        param.put("user_id", modelLogin.getResult().getId());
        param.put("review", binding.etComment.getText().toString().trim());
        param.put("rating", String.valueOf(binding.ratingBar.getRating()));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addReviewsAndRating(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        startActivity(new Intent(mContext, UserHomeAct.class));
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

}