package com.dayscab.driver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.AdapterPlansBinding;
import com.dayscab.driver.models.ModelPlans;
import com.dayscab.user.activities.PaymentWebviewAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPlans extends RecyclerView.Adapter<AdapterPlans.MyPlanViewHolder> {

    Context mContext;
    ArrayList<ModelPlans.Result> plansList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isPlanPurchased = false;

    public AdapterPlans(Context mContext, ArrayList<ModelPlans.Result> plansList) {
        this.mContext = mContext;
        this.plansList = plansList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public MyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPlansBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_plans, parent, false);
        return new MyPlanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPlanViewHolder holder, int position) {

        ModelPlans.Result data = plansList.get(position);

        holder.binding.tvdetailText.setText(AppConstant.CURRENCY + data.getAmount() + " / " + data.getTotal_ride() + " Trips");

        if (data.getPlan_status().equals("Active")) {
            if (!isPlanPurchased) isPlanPurchased = true;
            holder.binding.llback.setBackgroundResource(R.drawable.purchase_red_icon);
            holder.binding.tvPurchase.setBackgroundResource(R.drawable.purchase_red_back);
            holder.binding.tvStdDetails.setBackgroundResource(R.drawable.purchase_red_back);
            holder.binding.tvPurchase.setText("Active Plan");
        }

        holder.binding.tvPurchase.setOnClickListener(v -> {
            if (isPlanPurchased) {
                MyApplication.showAlert(mContext, "You aleardy have a plan firstly current plan is expired and than you will able to purchase another plan");
            } else {
                // planDetailsDialog(data);
                if ("free".equals(data.getPlan_name().toLowerCase())) {
                    MyApplication.showAlert(mContext, "You cannot purchase free plan");
                } else {
                    getWebviewForPayment(data.getAmount(), data.getId());
                }
            }
        });

        holder.binding.tvStdDetails.setOnClickListener(v -> {
            planDetailsDialog(data);
        });

    }

    private void getWebviewForPayment(String amount, String planId) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", modelLogin.getResult().getEmail());
        map.put("amount", amount);

        Log.e("getWebviewForPayment", "getWebviewForPayment = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
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

                    mContext.startActivity(new Intent(mContext, PaymentWebviewAct.class)
                            .putExtra("url", webUrl.trim())
                            .putExtra("planid", planId)
                            .putExtra("type", "plan")
                    );

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

    private void planDetailsDialog(ModelPlans.Result data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.plan_details));

        String textDetails = "Plan Name :      " + data.getPlan_name().toUpperCase() + "\n" +
                "Total Trip :      " + data.getTotal_ride() + " Trips" + "\n" +
                "Ride Fees :      " + data.getRide_fees() + "%" + "\n" +
                "Plan Amount :      " + AppConstant.CURRENCY + " " + data.getAmount() + "\n" +
                "Bonus Trip :      " + data.getBonus_trip() + " Trips" + "\n";

        if (data.getPlan_exp_date() != null && !(data.getPlan_exp_date().equals(""))) {
            textDetails = textDetails + "Expiry Date :      " + data.getPlan_exp_date().split(" ")[0];
        }

        builder.setMessage(textDetails);
        builder.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

    }

    @Override
    public int getItemCount() {
        return plansList == null ? 0 : plansList.size();
    }

    public class MyPlanViewHolder extends RecyclerView.ViewHolder {

        AdapterPlansBinding binding;

        public MyPlanViewHolder(AdapterPlansBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
