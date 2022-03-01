package com.dayscab.driver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityDriverFeedbackBinding;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;

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
        MyApplication.checkToken(mContext);
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

        binding.ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (binding.ratingBar.getRating() == 5) {
                    binding.tvFeedback.setText("Excellent");
                } else if (binding.ratingBar.getRating() == 4) {
                    binding.tvFeedback.setText("Very Good");
                } else if (binding.ratingBar.getRating() == 4.5) {
                    binding.tvFeedback.setText("Very Good");
                } else if (binding.ratingBar.getRating() == 3) {
                    binding.tvFeedback.setText("Good");
                } else if (binding.ratingBar.getRating() == 3.5) {
                    binding.tvFeedback.setText("Good");
                } else if (binding.ratingBar.getRating() == 2) {
                    binding.tvFeedback.setText("Poor");
                } else if (binding.ratingBar.getRating() == 2.5) {
                    binding.tvFeedback.setText("Medium");
                } else if (binding.ratingBar.getRating() == 1) {
                    binding.tvFeedback.setText("Poor");
                } else if (binding.ratingBar.getRating() == 1.5) {
                    binding.tvFeedback.setText("Poor");
                } else {
                    binding.tvFeedback.setText("Poor");
                }
                return false;
            }
        });

        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating == 5) {
                    binding.tvFeedback.setText("Excellent");
                } else if (rating == 4) {
                    binding.tvFeedback.setText("Very Good");
                } else if (rating == 4.5) {
                    binding.tvFeedback.setText("Very Good");
                } else if (rating == 3) {
                    binding.tvFeedback.setText("Good");
                } else if (rating == 3.5) {
                    binding.tvFeedback.setText("Good");
                } else if (rating == 2) {
                    binding.tvFeedback.setText("Poor");
                } else if (rating == 2.5) {
                    binding.tvFeedback.setText("Medium");
                } else if (rating == 1) {
                    binding.tvFeedback.setText("Poor");
                } else if (rating == 1.5) {
                    binding.tvFeedback.setText("Poor");
                } else {
                    binding.tvFeedback.setText("Poor");
                }
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnRate.setOnClickListener(v -> {
            Log.e("asfasdasdasd", "binding.ratingBar.getRating() = " + binding.ratingBar.getRating());
            if (binding.ratingBar.getRating() == 0.0) {
                MyApplication.showAlert(mContext, getString(R.string.please_add_rating));
                // Toast.makeText(mContext, getString(R.string.please_add_rating), Toast.LENGTH_SHORT).show();
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