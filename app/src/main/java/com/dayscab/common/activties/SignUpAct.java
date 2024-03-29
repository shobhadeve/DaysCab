package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivitySignUpBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;
    String type = "", registerId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        type = getIntent().getStringExtra(AppConstant.TYPE);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        });

        itit();

    }

    private void itit() {

        binding.btnSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_name_text));
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_email_text));
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_phone_text));
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.enter_valid_email));
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                MyApplication.showAlert(mContext, getString(R.string.please_enter_pass));
            } else if (!(binding.etPassword.getText().toString().trim().length() > 4)) {
                MyApplication.showAlert(mContext, getString(R.string.password_validation_text));
            } else {
                checkEmailOrMobile();
            }

//          startActivity(new Intent(mContext, VerifyAct.class)
//                .putExtra(AppContant.TYPE, AppContant.USER)
//          );

        });

        binding.ivLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginAct.class)
                    .putExtra(AppConstant.TYPE, AppConstant.USER)
            );
            finish();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void checkEmailOrMobile() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("mobile", binding.etPhone.getText().toString().trim());

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.verifyMobileEmailApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        HashMap<String, String> params = new HashMap<>();

                        params.put("user_name", binding.etName.getText().toString().trim());
                        params.put("email", binding.etEmail.getText().toString().trim());
                        params.put("mobile", binding.etPhone.getText().toString().trim());
                        params.put("register_id", registerId);
                        params.put("lat", "");
                        params.put("lon", "");
                        params.put("password", binding.etPassword.getText().toString().trim());
                        params.put("type", "USER");

                        String mobileNumber = "+233" + binding.etPhone.getText().toString().trim();
                        // String mobileNumber = "+91" + binding.etPhone.getText().toString().trim();

                        startActivity(new Intent(mContext, VerifyAct.class)
                                .putExtra("resgisterHashmap", params)
                                .putExtra("mobile", mobileNumber)
                                .putExtra(AppConstant.TYPE, AppConstant.USER)
                        );

                    } else if (jsonObject.getString("status").equals("4")) {
                        MyApplication.showAlert(mContext, "Email and mobile number already exist please try with different credentials");
                    } else if (jsonObject.getString("status").equals("3")) {
                        MyApplication.showAlert(mContext, "Email already exist please try with different email");
                    } else if (jsonObject.getString("status").equals("2")) {
                        MyApplication.showAlert(mContext, "Mobile number already exist please try with different mobile number");
                    }
                } catch (Exception e) {
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