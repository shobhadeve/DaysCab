package com.dayscab.user.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityPaymentWebviewBinding;
import com.dayscab.driver.activities.DriverFeedbackAct;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentWebviewAct extends AppCompatActivity {

    Context mContext = PaymentWebviewAct.this;
    ActivityPaymentWebviewBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String type, planId, amount;
    String loadPaymentURL = "";
    private ModelCurrentBooking data;
    private ModelCurrentBookingResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_webview);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        loadPaymentURL = getIntent().getStringExtra("url");

        type = getIntent().getStringExtra("type");
        planId = getIntent().getStringExtra("planid");
        amount = getIntent().getStringExtra("amount");

        if (getIntent() != null) {
            if (type == null || type.equals("")) {
                data = (ModelCurrentBooking) getIntent().getSerializableExtra("data");
                result = data.getResult().get(0);
            }
        }

        itit();

    }

    private void itit() {

        binding.webView.getSettings().setJavaScriptEnabled(true);// enable javascript
        binding.webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(PaymentWebviewAct.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Toast.makeText(mContext, "On Page Finished", Toast.LENGTH_SHORT).show();
                Log.e("paystack1_web_url", url);
                String title = binding.webView.getTitle();
                Log.d("title", title);
                String completed = "PayPal checkout - Payment complete.";
                if (url.contains("success")) {

                }
                if (title.contains("cancel")) {
                    Toast.makeText(mContext, "Payment Cancel", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
                if ("https://standard.paystack.co/charge/".equals(url.trim())) {
                    if (type == null || type.equals("")) {
                        doPaymentApi();
                    } else if (type.equals("wallet")) {
                        addWalletAmountApi();
                    } else {
                        purchasePlanApi();
                    }
                }
                Log.e("paystack1_web_url", "onLoadResource = " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("paystack1_web_url", "shouldOverrideUrlLoading " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

        });

        binding.webView.loadUrl(loadPaymentURL);

    }

    private void addWalletAmountApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("wallet", amount);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addWalletAmount(map);
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

    public void purchasePlanApi() {

        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", modelLogin.getResult().getId());
        map.put("plan_id", planId);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.purchasePlanApiCall(map);
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
                        startActivity(new Intent(mContext, DriverHomeAct.class));
                        MyApplication.showToast(mContext, getString(R.string.success));
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

    @Override
    public void onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            finish();
        }
    }

}