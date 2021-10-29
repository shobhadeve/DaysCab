package com.dayscab.common.activties;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityVerifyBinding;
import com.dayscab.driver.activities.UploadVehicleAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.InternetConnection;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAct extends AppCompatActivity {

    Context mContext = VerifyAct.this;
    ActivityVerifyBinding binding;
    String type = "";
    String mobile = "";
    String id;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private FirebaseAuth mAuth;
    HashMap<String, File> fileHashMap = new HashMap<>();
    HashMap<String, String> paramHash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        sharedPref = SharedPref.getInstance(mContext);

        mAuth = FirebaseAuth.getInstance();

        type = getIntent().getStringExtra(AppConstant.TYPE);

        if (AppConstant.USER.equals(type)) {
            paramHash = (HashMap<String, String>) getIntent().getSerializableExtra("resgisterHashmap");
        } else {
            paramHash = (HashMap<String, String>) getIntent().getSerializableExtra("resgisterHashmap");
            fileHashMap = (HashMap<String, File>) getIntent().getSerializableExtra("fileHashMap");
        }

        mobile = getIntent().getStringExtra("mobile");

        itit();

        if (InternetConnection.checkConnection(mContext)) sendVerificationCode();
        else Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_SHORT).show();

    }

    private void itit() {

        binding.et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.et3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.et4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.et5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.et6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnVerify.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.et1.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.et2.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.et3.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.et4.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.et5.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.et6.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
            } else {

                String otpFull = binding.et1.getText().toString().trim() +
                        binding.et2.getText().toString().trim() +
                        binding.et3.getText().toString().trim() +
                        binding.et4.getText().toString().trim() +
                        binding.et5.getText().toString().trim() +
                        binding.et6.getText().toString().trim();

                ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otpFull);
                signInWithPhoneAuthCredential(credential);

            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void sendVerificationCode() {

        binding.tvVerifyText.setText("We have send you an SMS on " + mobile + " with 6 digit verification code.");

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResend.setText("" + millisUntilFinished / 1000);
                binding.tvResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.tvResend.setText(mContext.getString(R.string.resend));
                binding.tvResend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ", ""), // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerifyAct.this.id = id;
                        Toast.makeText(mContext, getString(R.string.enter_6_digit_code), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        ProjectUtil.pauseProgressDialog();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        ProjectUtil.pauseProgressDialog();
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProjectUtil.pauseProgressDialog();
                            // Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();

                            if (AppConstant.USER.equals(type)) {
                                signUpApiCall();
                            } else {
                                signupDriverCallApi();
                            }

//                            if(type.equals("request")) {
//                                providerRequestApi();
//                            } else {
//                            }

                        } else {
                            ProjectUtil.pauseProgressDialog();
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {}

                        }
                    }
                });

    }

    private void signupDriverCallApi() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part profileFilePart;

        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("first_name"));
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("last_name"));
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("email"));
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("mobile"));
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("city"));
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("address"));
        RequestBody register_id = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("register_id"));
        RequestBody lat = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("lat"));
        RequestBody lon = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("lon"));
        RequestBody password = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("password"));
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), paramHash.get("type"));
        RequestBody step = RequestBody.create(MediaType.parse("text/plain"), "1");

        profileFilePart = MultipartBody.Part.createFormData("image", fileHashMap.get("image").getName(), RequestBody.create(MediaType.parse("car_document/*"), fileHashMap.get("image")));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.signUpDriverCallApi(first_name,
                last_name, email, mobile, city, address, register_id, lat,
                lon, password, type, step, profileFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("driversignup", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        modelLogin.getResult().setStep("1");
                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        startActivity(new Intent(mContext, UploadVehicleAct.class));
                        finish();

                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });

    }

    private void signUpApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.signUpApiCall(paramHash);

        Log.e("adasdasdas", "paramHash = " + paramHash);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("adasdasdas", "response = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        startActivity(new Intent(mContext, UserHomeAct.class));

                        finish();

                    } else {
                        Toast.makeText(mContext, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
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
