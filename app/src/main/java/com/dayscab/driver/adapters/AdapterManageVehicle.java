package com.dayscab.driver.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.AdapterManageVehiclesBinding;
import com.dayscab.driver.models.ModelVehicles;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterManageVehicle extends RecyclerView.Adapter<AdapterManageVehicle.MyPlanViewHolder> {

    Context mContext;
    ArrayList<ModelVehicles.Result> plansList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    public AdapterManageVehicle(Context mContext, ArrayList<ModelVehicles.Result> plansList) {
        this.mContext = mContext;
        this.plansList = plansList;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterManageVehicle.MyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterManageVehiclesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.adapter_manage_vehicles, parent, false);
        return new AdapterManageVehicle.MyPlanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManageVehicle.MyPlanViewHolder holder, int position) {

        ModelVehicles.Result data = plansList.get(position);

        holder.binding.carNumber.setText("Car Number\n" + data.getCar_number());
        holder.binding.carManufactured.setText("Car Brand\n" + data.getBrand_name());
        Glide.with(mContext).load(data.getCar_image()).into(holder.binding.ivCarImage);

        if ("Active".equals(data.getStatus())) {
            holder.binding.switch4.setOn(true);
        } else {
            holder.binding.switch4.setOn(false);
        }

        holder.binding.switch4.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    activeDeactiveVehicle("Active", data.getId(), holder.binding.switch4);
                } else {
                    activeDeactiveVehicle("Deactive", data.getId(), holder.binding.switch4);
                }
            }
        });

        holder.binding.ivDelete.setOnClickListener(v -> {
            showDeleteDialog(position, data);
        });

    }

    private void showDeleteDialog(int position, ModelVehicles.Result data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.sure_delete_vehicle_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteVehicel(position, data.getId());
                    }
                }).create().show();
    }

    private void deleteVehicel(int position, String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("vehicle_detail_id", id);

        Log.e("getAllVehicles", "getAllVehicles = " + map);
        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.deleteVehicleApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        plansList.remove(position);
                        notifyDataSetChanged();
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

    private void activeDeactiveVehicle(String status, String carId, LabeledSwitch switch4) {

        HashMap<String, String> map = new HashMap<>();

        map.put("vehicle_detail_id", carId);
        map.put("status", status);

        Log.e("getAllVehicles", "getAllVehicles = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.activeDeactiveVehicleApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").equals("2")) {
                        switch4.setOn(false);
                        MyApplication.showAlert(mContext, mContext.getString(R.string.online_vehicle_text));
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
        return plansList == null ? 0 : plansList.size();
    }

    public class MyPlanViewHolder extends RecyclerView.ViewHolder {

        AdapterManageVehiclesBinding binding;

        public MyPlanViewHolder(AdapterManageVehiclesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
