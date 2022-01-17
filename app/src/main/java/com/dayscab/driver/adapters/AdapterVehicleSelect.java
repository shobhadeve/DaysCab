package com.dayscab.driver.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.driver.models.ModelVehicles;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterVehicleSelect extends BaseAdapter {

    Context mContext;
    ArrayList<ModelVehicles.Result> vehicleList;
    Dialog dialog;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    String carId;
    UpdateVehiclInterface updateVehiclInterface;

    public AdapterVehicleSelect(Context mContext, ArrayList<ModelVehicles.Result> vehicleList, Dialog dialog, UpdateVehiclInterface updateVehiclInterface, String carId) {
        this.mContext = mContext;
        this.vehicleList = vehicleList;
        this.dialog = dialog;
        this.carId = carId;
        this.updateVehiclInterface = updateVehiclInterface;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        Log.e("afsfdsgdfgs", "modellogin car Id = " + modelLogin.getResult().getCar_type_id());
    }

    @Override
    public int getCount() {
        return vehicleList == null ? 0 : vehicleList.size();
    }

    @Override
    public Object getItem(int position) {
        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_select_car, parent, false);

        RadioButton rbCarName = view.findViewById(R.id.rbCarName);

        rbCarName.setText(vehicleList.get(position).getBrand_name() + " (" +
                vehicleList.get(position).getCar_number() + ")");

        Log.e("afsfdsgdfgs", "vehicleList.get(position).getId() = " + vehicleList.get(position).getId());

        if ("Active".equals(vehicleList.get(position).getStatus())) {
            rbCarName.setVisibility(View.VISIBLE);
        } else {
            rbCarName.setVisibility(View.GONE);
        }

        if (vehicleList.get(position).getId().equals(carId)) {
            rbCarName.setChecked(true);
        }

        rbCarName.setOnClickListener(v -> {
            updateVehicle(vehicleList.get(position).getCar_type_id(), position, vehicleList.get(position).getId());
        });

        return view;

    }

    public interface UpdateVehiclInterface {
        void onSuccess(ModelVehicles.Result data);
    }

    private void updateVehicle(String id, int position, String topId) {

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());
        paramHash.put("car_type_id", id);
        paramHash.put("car_detail_id", topId);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateOnlineVehicle(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);
                        Log.e("updateOnlineVehicle", "response = " + stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            updateVehiclInterface.onSuccess(vehicleList.get(position));
                            dialog.dismiss();
                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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