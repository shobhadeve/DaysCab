package com.dayscab.user.activities;

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

import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityRideCancelationBinding;
import com.dayscab.databinding.AddMoneyDialogBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideCancelationAct extends AppCompatActivity {

    private Context mContext = RideCancelationAct.this;
    private ActivityRideCancelationBinding binding;
    private String requestId = "";
    private SharedPref sharedPref;
    private ModelLogin modelLogin;
    private double walletTmpAmt = 0.0;
    private ModelCurrentBookingResult result;
    private ModelCurrentBooking data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ride_cancelation);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        requestId = getIntent().getStringExtra("id");
        data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
        result = data.getResult().get(0);

        Log.e("sfasfasfasdfas", "data = " + result.getAmount());

        MyApplication.showAlert(mContext, "When you cancel the ride "
                + result.getAmount() + " " + AppConstant.CURRENCY + " will be deducted from your wallet");

        itit();

    }

    private void itit() {

        binding.cb1.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb2.setOnClickListener(v -> {
            binding.cb1.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb3.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb1.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb4.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb1.setChecked(false);
            binding.cb5.setChecked(false);
        });

        binding.cb5.setOnClickListener(v -> {
            binding.cb2.setChecked(false);
            binding.cb3.setChecked(false);
            binding.cb4.setChecked(false);
            binding.cb1.setChecked(false);
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (binding.cb1.isChecked()) {
                AcceptCancel("Don't want to share");
            } else if (binding.cb2.isChecked()) {
                AcceptCancel("Can't contact the driver");
            } else if (binding.cb3.isChecked()) {
                AcceptCancel("Driver is late");
            } else if (binding.cb4.isChecked()) {
                AcceptCancel("The price is not reasonable");
            } else if (binding.cb5.isChecked()) {
                AcceptCancel("Pickup address is incorrect");
            } else {
                Toast.makeText(mContext, getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.wallet_deduction_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                addMoneyDialog();
                            }
                        }).create().show();
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
                            .putExtra("isridecancel", "True")
                    );

                    // finish();

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

    public void AcceptCancel(String reason) {

        HashMap<String, String> map = new HashMap<>();
        map.put("request_id", requestId);
        map.put("amount", result.getAmount());
        map.put("cancel_reaison", reason);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.cancelRideAmountApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        finishAffinity();
                        startActivity(new Intent(mContext, UserHomeAct.class));
                    } else if (jsonObject.getString("status").equals("2")) {
                        showAlert();
                    }
                    Toast.makeText(mContext, getString(R.string.ride_cancel_success), Toast.LENGTH_LONG).show();
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