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

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityAddBankAccBinding;
import com.dayscab.databinding.ActivityUploadVehicleBinding;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBankAccAct extends AppCompatActivity {

    Context mContext = AddBankAccAct.this;
    ActivityAddBankAccBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_bank_acc);
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        itit();
    }

    private void itit() {

        binding.btnAddAccount.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etHolderName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.mandatory_fields_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etBankName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.mandatory_fields_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etBIC.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.mandatory_fields_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etIBAN.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.mandatory_fields_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etAccountNumber.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.mandatory_fields_text), Toast.LENGTH_SHORT).show();
            } else {
                addBankAccountApiCall();
            }

            // startActivity(new Intent(mContext, DriverHomeAct.class));

        });

    }

    private void addBankAccountApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("account_holder_name", binding.etHolderName.getText().toString().trim());
        paramHash.put("bank_name", binding.etBankName.getText().toString().trim());
        paramHash.put("bic_no", binding.etBIC.getText().toString().trim());
        paramHash.put("ibn_no", binding.etIBAN.getText().toString().trim());
        paramHash.put("account_number", binding.etAccountNumber.getText().toString().trim());

        Call<ResponseBody> call = api.addBankAccount(paramHash);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringRes = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringRes);

                    if (jsonObject.getString("status").equals("1")) {
                        startActivity(new Intent(mContext, DriverHomeAct.class));
                        finish();
                    }

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