package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUpdateProfileBinding;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileUserAct extends AppCompatActivity {

    Context mContext = UpdateProfileUserAct.this;
    ActivityUpdateProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.etEmail.setText(modelLogin.getResult().getEmail());
        binding.etName.setText(modelLogin.getResult().getUser_name());
        if (modelLogin.getResult().getMobile() == null || modelLogin.getResult().getMobile().length() < 4) {
            binding.etPhone.setText("");
        } else {
            binding.etPhone.setText(modelLogin.getResult().getMobile());
        }

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnUpdate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else {
                updateProfile();
            }
        });

    }

    private void updateProfile() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_name", binding.etName.getText().toString().trim());
        paramHash.put("mobile", binding.etPhone.getText().toString().trim());
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("id", modelLogin.getResult().getId());
        paramHash.put("type", "USER");

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateProfileApiCall(paramHash);

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
                        // Toast.makeText(mContext, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
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