package com.dayscab.driver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.AdapterUserPoolRequestsBinding;
import com.dayscab.driver.activities.EndTripDriverAct;
import com.dayscab.driver.activities.TrackDriverAct;
import com.dayscab.driver.models.ModelUserPools;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MusicManager;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUserPoolRequests extends RecyclerView.Adapter<AdapterUserPoolRequests.MyRideHolder> {

    Context mContext;
    ArrayList<ModelUserPools.Result> poolList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterUserPoolRequests(Context mContext, ArrayList<ModelUserPools.Result> poolList) {
        this.mContext = mContext;
        this.poolList = poolList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        Log.e("adadasdasda", "poolList = " + new Gson().toJson(poolList));
    }

    @NonNull
    @Override
    public AdapterUserPoolRequests.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterUserPoolRequestsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.adapter_user_pool_requests, parent, false);
        return new AdapterUserPoolRequests.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserPoolRequests.MyRideHolder holder, int position) {

        ModelUserPools.Result data = poolList.get(position);

        holder.binding.tvPickup.setText(data.getPicuplocation());
        holder.binding.tvDrop.setText(data.getDropofflocation());

        if (data.getStatus().equals("Accept")) {
            holder.binding.btnCancel.setVisibility(View.GONE);
            holder.binding.btnAccept.setText("Go To Details");
        }

        holder.binding.tvSeats.setText("Pool Request For " + data.getNo_of_passenger() + " Passenger");

        holder.binding.btnAccept.setOnClickListener(v -> {
            if (data.getStatus().equals("Accept")) {
                getBookingDetails(data.getId(), "DRIVER");
            } else {
                showAlert("Accept", "Are you sure you want to accept these request?", data.getId(), data, position);
            }
        });

        holder.binding.btnCancel.setOnClickListener(v -> {
            showAlert("Cancel", "Are you sure you want to reject these request?", data.getId(), data, position);
        });

    }

    private void getBookingDetails(String requestId, String type) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> param = new HashMap<>();
        param.put("request_id", requestId);
        param.put("type", type);

        Log.e("paramparam", "param = " + param);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCurrentBookingDetails(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Log.e("getCurrentBooking", "getCurrentBooking = " + responseString);
                        Type listType = new TypeToken<ModelCurrentBooking>() {
                        }.getType();
                        ModelCurrentBooking data = new GsonBuilder().create().fromJson(responseString, listType);
                        if (data.getStatus().equals(1)) {
                            ModelCurrentBookingResult result = data.getResult().get(0);
                            Log.e("getUserRatingStatus", "getUserRatingStatus = " + result.getUserRatingStatus());
                            Log.e("getUserRatingStatus", "ModelCurrentBookingResult = " + result.getPayment_status());
                            if (result.getStatus().equalsIgnoreCase("Accept")) {
                                Intent k = new Intent(mContext, TrackDriverAct.class);
                                k.putExtra("data", data);
                                mContext.startActivity(k);
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                Intent j = new Intent(mContext, TrackDriverAct.class);
                                j.putExtra("data", data);
                                mContext.startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                Intent j = new Intent(mContext, TrackDriverAct.class);
                                j.putExtra("data", data);
                                mContext.startActivity(j);
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                mContext.startActivity(new Intent(mContext, EndTripDriverAct.class)
                                        .putExtra("data", data)
                                );
                            }
                        }
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

    public void showAlert(String status, String text, String id, ModelUserPools.Result data, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(text)
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (status.equals("Accept")) {
                                    AcceptCancel(id, "Accept", data, position);
                                } else {
                                    AcceptCancel(id, "Cancel", data, position);
                                }
                            }
                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public void AcceptCancel(String request_id, String status, ModelUserPools.Result data, int position) {

        SharedPref sharedPref = SharedPref.getInstance(mContext);
        ModelLogin modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", request_id);
        map.put("status", status);
        map.put("timezone", TimeZone.getDefault().getID());

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.acceptCancelOrderCallTaxi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                        if (status.equals("Accept")) {
                            data.setStatus("Accept");
                            notifyDataSetChanged();
                        } else {
                            poolList.remove(position);
                            notifyDataSetChanged();
                            storeDriverId(data.getId());
                        }
                    } else {
                        MusicManager.getInstance().stopPlaying();
                        // MyApplication.showToast(mContext, mContext.getString(R.string.req_cancelled));
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

    private void storeDriverId(String request_id) {

        HashMap<String, String> map = new HashMap<>();
        map.put("driver_id", modelLogin.getResult().getId());
        map.put("request_id", request_id);

        Log.e("AcceptCancel", "AcceptCancel = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.cancelDriverPoolApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("AcceptCancel", "stringResponse = " + stringResponse);
                    } else {
                        MusicManager.getInstance().stopPlaying();
                        // MyApplication.showToast(mContext, mContext.getString(R.string.req_cancelled));
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
    public int getItemCount() {
        return poolList == null ? 0 : poolList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        AdapterUserPoolRequestsBinding binding;

        public MyRideHolder(@NonNull AdapterUserPoolRequestsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}