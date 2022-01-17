package com.dayscab.driver.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.dayscab.R;
import com.dayscab.common.models.ModelCurrentBooking;
import com.dayscab.common.models.ModelCurrentBookingResult;
import com.dayscab.databinding.ItemRideHistoryBinding;
import com.dayscab.driver.activities.DriverFeedbackAct;
import com.dayscab.driver.activities.EndTripDriverAct;
import com.dayscab.driver.activities.TrackDriverAct;
import com.dayscab.driver.models.ModelActiveBooking;
import com.dayscab.user.activities.EndUserAct;
import com.dayscab.user.activities.TrackAct;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterActiveBooking extends RecyclerView.Adapter<AdapterActiveBooking.MyRideHolder> {

    Context mContext;
    ArrayList<ModelActiveBooking.Result> historyList;
    String type;

    public AdapterActiveBooking(Context mContext, ArrayList<ModelActiveBooking.Result> historyList, String type) {
        this.mContext = mContext;
        this.historyList = historyList;
        this.type = type;
    }

    @NonNull
    @Override
    public AdapterActiveBooking.MyRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext)
                , R.layout.item_ride_history, parent, false);
        return new AdapterActiveBooking.MyRideHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterActiveBooking.MyRideHolder holder, int position) {

        ModelActiveBooking.Result data = historyList.get(position);

        Log.e("paramparam", "param = " + data.getPicklatertime());
        Log.e("paramparam", "param = " + data.getPicklaterdate());

        holder.binding.tvFrom.setText(data.getPicuplocation());
        holder.binding.etDestination.setText(data.getDropofflocation());

        if (data.getBooktype().equals("NOW")) {
            holder.binding.tvDateTime.setText(data.getAccept_time());
        } else {
            holder.binding.tvDateTime.setText(data.getPicklaterdate() + " " + data.getPicklatertime());
        }

        holder.binding.tvStatus.setText(data.getStatus());

        holder.binding.GoDetail.setOnClickListener(v -> {

            if (data.getStatus().equals("Accept") || data.getStatus().equals("Start") ||
                    data.getStatus().equals("End")) {
                if (type.equals("USER")) {
                    if (data.getBooktype().equals("POOL")) {
                        Date date1 = null, date2 = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date1 = sdf.parse(data.getPicklaterdate());
                            date2 = sdf.parse(ProjectUtil.getCurrentDateNEW());

                            if (date1.equals(date2)) {
                                getBookingDetails(data.getId(), type);
                            } else if (date1.before(date2)) {
                                getBookingDetails(data.getId(), type);
                            } else {
                                Toast.makeText(mContext, "Not allowed to access because your booking date is : " + data.getPicklaterdate(), Toast.LENGTH_LONG).show();
                            }
                        } catch (ParseException e) {
                            Log.e("sdfsfdsffsd", "Date Exception = " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        getBookingDetails(data.getId(), type);
                    }
                } else {
                    if (data.getBooktype().equals("POOL")) {
                        Date date1 = null, date2 = null;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date1 = sdf.parse(data.getPicklaterdate());
                            date2 = sdf.parse(ProjectUtil.getCurrentDateNEW());

                            if (date1.equals(date2)) {
                                getBookingDetails(data.getId(), type);
                            } else if (date1.before(date2)) {
                                getBookingDetails(data.getId(), type);
                            } else {
                                Toast.makeText(mContext, "Not allowed to access because your booking date is : " + data.getPicklaterdate(), Toast.LENGTH_LONG).show();
                            }
                        } catch (ParseException e) {
                            Log.e("sdfsfdsffsd", "Date Exception = " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        getBookingDetails(data.getId(), type);
                    }
                }
            }

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
                                if (type.equals("USER")) {
                                    Intent k = new Intent(mContext, TrackAct.class);
                                    k.putExtra("data", data);
                                    mContext.startActivity(k);
                                } else {
                                    Intent k = new Intent(mContext, TrackDriverAct.class);
                                    k.putExtra("data", data);
                                    mContext.startActivity(k);
                                }
                            } else if (result.getStatus().equalsIgnoreCase("Arrived")) {
                                if (type.equals("USER")) {
                                    Intent j = new Intent(mContext, TrackAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                } else {
                                    Intent j = new Intent(mContext, TrackDriverAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                }
                            } else if (result.getStatus().equalsIgnoreCase("Start")) {
                                if (type.equals("USER")) {
                                    Intent j = new Intent(mContext, TrackAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                } else {
                                    Intent j = new Intent(mContext, TrackDriverAct.class);
                                    j.putExtra("data", data);
                                    mContext.startActivity(j);
                                }
                            } else if (result.getStatus().equalsIgnoreCase("End")) {
                                if (type.equals("USER")) {
                                    if ("Success".equals(result.getPayment_status())) {
                                        if (null == result.getUserRatingStatus() ||
                                                "".equals(result.getUserRatingStatus())) {
                                            Intent j = new Intent(mContext, DriverFeedbackAct.class);
                                            j.putExtra("data", data);
                                            mContext.startActivity(j);
                                        }
                                    } else {
                                        Intent j = new Intent(mContext, EndUserAct.class);
                                        j.putExtra("data", data);
                                        mContext.startActivity(j);
                                    }
                                } else {
                                    mContext.startActivity(new Intent(mContext, EndTripDriverAct.class)
                                            .putExtra("data", data)
                                    );
                                }

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

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }

    public class MyRideHolder extends RecyclerView.ViewHolder {

        ItemRideHistoryBinding binding;

        public MyRideHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}