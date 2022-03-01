package com.dayscab.driver.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityWalletBinding;
import com.dayscab.databinding.AddMoneyDialogBinding;
import com.dayscab.databinding.SendMoneyDialogNewBinding;
import com.dayscab.driver.adapters.AdapterTransactions;
import com.dayscab.driver.models.ModelTransactions;
import com.dayscab.user.activities.PaymentWebviewAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAct extends AppCompatActivity {

    Context mContext = WalletAct.this;
    ActivityWalletBinding binding;
    private double walletTmpAmt;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        itit();
    }

    private void itit() {

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransactionApi();
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvAddMoney.setOnClickListener(v -> {
            addMoneyDialog();
        });

        binding.cvTransfer.setOnClickListener(v -> {
            tranferMOneyDialog();
        });

    }

    private void addMoneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AddMoneyDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.add_money_dialog, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    dialog.dismiss();
                return false;
            }
        });

        dialogBinding.etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!(s == null || s.equals(""))) {

                    try {
                        walletTmpAmt = Double.parseDouble(s.toString());
                    } catch (Exception e) {
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogBinding.ivMinus.setOnClickListener(v -> {
            if (!(dialogBinding.etMoney.getText().toString().trim().equals("") || dialogBinding.etMoney.getText().toString().trim().equals("0"))) {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) - 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.ivPlus.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                dialogBinding.etMoney.setText("0");
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            } else {
                walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim()) + 1;
                dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.tv699.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("699");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv799.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("799");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.tv899.setOnClickListener(v -> {
            dialogBinding.etMoney.setText("899");
            walletTmpAmt = Double.parseDouble(dialogBinding.etMoney.getText().toString().trim());
            dialogBinding.etMoney.setText(String.valueOf(walletTmpAmt));
        });

        dialogBinding.btDone.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
                Toast.makeText(mContext, "Please enter amount", Toast.LENGTH_SHORT).show();
            } else if (walletTmpAmt == 0.0) {
                Toast.makeText(mContext, "Please enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                getWebviewForPayment(String.valueOf(walletTmpAmt));
            }
        });

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void getWebviewForPayment(String amount) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", modelLogin.getResult().getEmail());
        map.put("amount", amount);

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
                            .putExtra("type", "wallet")
                            .putExtra("amount", amount)
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

    @Override
    protected void onResume() {
        super.onResume();
        getProfileApiCall();
        getTransactionApi();
    }

    private void getTransactionApi() {

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getTransactionApiCall(paramHash);
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

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            ModelTransactions modelTransactions = new Gson().fromJson(stringResponse, ModelTransactions.class);

                            AdapterTransactions adapterTransactions = new AdapterTransactions(mContext, modelTransactions.getResult());
                            binding.rvTransactions.setAdapter(adapterTransactions);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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

    private void getProfileApiCall() {
        // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Log.e("getProfileApiCall", "getProfileApiCall = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);

                            binding.tvWalletAmount.setText(AppConstant.CURRENCY + " " + modelLogin.getResult().getWallet());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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

    private void tranferMOneyDialog() {

        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        SendMoneyDialogNewBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.send_money_dialog_new, null, false);

        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.btDone.setOnClickListener(v -> {

            if (TextUtils.isEmpty(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(dialogBinding.etEnterAmount.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_amount), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(dialogBinding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if (dialogBinding.etEnterAmount.getText().toString().equals("0")) {
                Toast.makeText(mContext, getString(R.string.please_enter_valid_amount), Toast.LENGTH_SHORT).show();
            } else {
                if (modelLogin.getResult().getEmail().equals(dialogBinding.etEmail.getText().toString().trim())) {
                    Toast.makeText(mContext, getString(R.string.you_enter_your_own_email), Toast.LENGTH_SHORT).show();
                } else {
                    sendMoneyAPiCall(dialog, dialogBinding.etEmail.getText().toString().trim()
                            , dialogBinding.etEnterAmount.getText().toString().trim());
                }
            }

        });

        dialogBinding.tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.show();

    }

    private void sendMoneyAPiCall(Dialog dialog, String email, String amount) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("amount", amount);
        paramHash.put("email", email);

        Log.e("paramHashparamHash", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.walletTrasferApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            getTransactionApi();
                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);
                            getProfileApiCall();
                            dialog.dismiss();

                        } else {
                            Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
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