package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.dayscab.R;
import com.dayscab.common.models.ModelEarnings;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityStatisticsBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsAct extends AppCompatActivity {

    Context mContext = StatisticsAct.this;
    ActivityStatisticsBinding binding;
    // Variable for our bar data.
    BarData barData;
    // Variable for our bar data set.
    BarDataSet barDataSet;
    // Array list for storing entries.
    ArrayList barEntriesArrayList;
    ArrayList<String> monthsList;
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        monthsList = new ArrayList<>();
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        itit();
    }

    private void itit() {

        initializeMonthList();

        Log.e("asfasasdasda", "getAllEarnings = year = " + ProjectUtil.getCurrentDate().split("-")[2].trim());

        binding.spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getAllEarnings(binding.spDate.getItemAtPosition(position).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void initializeMonthList() {

        monthsList.add("Jan");
        monthsList.add("Feb");
        monthsList.add("Mar");
        monthsList.add("Apr");
        monthsList.add("May");
        monthsList.add("June");
        monthsList.add("July");
        monthsList.add("Aug");
        monthsList.add("Sep");
        monthsList.add("Oct");
        monthsList.add("Nov");
        monthsList.add("Dec");

    }

    private void getBarEntries(ModelEarnings modelEarnings) {

        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        for (int i = 0; i < modelEarnings.getResult().size(); i++)
            barEntriesArrayList.add(new BarEntry(Float.parseFloat(modelEarnings.getResult().get(i).getKey()), i));

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Earnings");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(monthsList, barDataSet);

        // below line is to set data
        // to our bar chart.
        binding.chart.setData(barData);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(14f);

        binding.chart.invalidate();
        binding.chart.refreshDrawableState();

    }

    private void getAllEarnings(String year) {

        HashMap<String, String> map = new HashMap<>();

        map.put("user_id", modelLogin.getResult().getId());
        map.put("year", year);

        Log.e("getAllEarnings", "getAllEarnings param = " + map);

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getAllEarningsApiCall(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "stringResponse = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);
                    if (jsonObject.getString("status").equals("1")) {
                        ModelEarnings modelEarnings = new Gson().fromJson(stringResponse, ModelEarnings.class);
                        binding.tvNoOfTrip.setText(modelEarnings.getTrip());
                        binding.tvTotalEarnings.setText(AppConstant.CURRENCY + modelEarnings.getYear());
                        getBarEntries(modelEarnings);
                    } else {
                        binding.tvNoOfTrip.setText("0.0");
                        binding.tvTotalEarnings.setText(AppConstant.CURRENCY + "0.0");
                        binding.chart.clear();
                        MyApplication.showAlert(mContext, getString(R.string.no_earnings));
                    }
                } catch (Exception e) {
                    Log.e("sfasfsdfdsf", "Exception catch = " + e.getMessage());
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