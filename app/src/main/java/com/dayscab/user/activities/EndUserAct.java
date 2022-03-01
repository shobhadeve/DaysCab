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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityEndUserBinding;
import com.dayscab.databinding.AddMoneyDialogBinding;
import com.dayscab.driver.activities.DriverFeedbackAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

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
    double finalPayment = 0.0;
    private double walletTmpAmt = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_end_user);
        MyApplication.checkToken(mContext);

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
            binding.tvTotalPay.setText(result.getTotal_trip_cost() + " " + AppConstant.CURRENCY);

            binding.tvWaitingTime.setText(result.getWaitng_time() + " Min");

            if(result.getPer_minute() == null) {
                binding.tvPerMintueWaiting.setText("0 " + AppConstant.CURRENCY);
            } else {
                binding.tvPerMintueWaiting.setText(result.getPer_minute() + " " + AppConstant.CURRENCY);
            }

            if(result.getPer_minute_charge() == null) {
                binding.tvWaitingCharge.setText("0 " + AppConstant.CURRENCY);
            } else {
                binding.tvWaitingCharge.setText(result.getPer_minute_charge() + " " + AppConstant.CURRENCY);
            }

            binding.tvDistance.setText(result.getDistance() + " Km");
            binding.tvDistanceCost.setText(result.getAmount() + " " + AppConstant.CURRENCY);
            // binding.tvPay.setText("Payment Amount : " + result.getAmount() + " " + AppConstant.CURRENCY);
            binding.tvPayType.setText(result.getPaymentType().toUpperCase());

            finalPayment = Double.parseDouble(result.getTotal_trip_cost());

            if (result.getBooktype().equals("POOL")) {
                binding.rlPool.setVisibility(View.VISIBLE);
                binding.tvPassenger.setText(result.getNo_of_passenger() + " Passenger");
                double passengerTotal = Double.parseDouble(result.getTotal_trip_cost()) * Double.parseDouble(result.getNo_of_passenger());
                finalPayment = passengerTotal;
                binding.tvTotalPay.setText(passengerTotal + " " + AppConstant.CURRENCY);
            } else {
                binding.rlPool.setVisibility(View.GONE);
            }

            Log.e("zddsasdfdasf", "pickUp = " + pickUp);
            Log.e("zddsasdfdasf", "dropOff = " + dropOff);
            Log.e("zddsasdfdasf", "payType = " + payType);

            driverId = result.getDriverId();
            driverDetails = result.getDriver_details().get(0);

        }

        binding.btnPay.setOnClickListener(v -> {
            if (result.getPaymentType().equalsIgnoreCase("Online")) {
                getWebviewForPayment("");
            } else if(result.getPaymentType().equalsIgnoreCase("Cash")) {
                doPaymentApi();
            } else if(result.getPaymentType().equalsIgnoreCase("Wallet")) {
                doPaymentApi();
            }
        });

    }

    private void getWebviewForPaymentWallet(String amount) {

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

    private void getWebviewForPayment(String type) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", modelLogin.getResult().getEmail());
        map.put("amount", String.valueOf(finalPayment));

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

                    if(type.equals("wallet")) {
                        startActivity(new Intent(mContext, PaymentWebviewAct.class)
                                .putExtra("url", webUrl.trim())
                                .putExtra("data", data)
                                .putExtra("type", "wallet")
                        );
                    } else {
                        startActivity(new Intent(mContext, PaymentWebviewAct.class)
                                .putExtra("url", webUrl.trim())
                                .putExtra("data", data)
                                .putExtra("type", "online")
                        );
                    }

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
                getWebviewForPaymentWallet(String.valueOf(walletTmpAmt));
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

    public void doPaymentApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("payment_type", result.getPaymentType());
        map.put("amount", result.getAmount());
        map.put("user_id", modelLogin.getResult().getId());
        map.put("request_id", result.getId());
        map.put("tip", "0");
        map.put("timezone", TimeZone.getDefault().getID());
        map.put("car_charge", "0");

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
                    } else if(jsonObject.getString("status").equals("2")) {
                        showAmountDialog();
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

    private void showAmountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("You have insufficient wallet amount. Please add amount to your wallet!")
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

}