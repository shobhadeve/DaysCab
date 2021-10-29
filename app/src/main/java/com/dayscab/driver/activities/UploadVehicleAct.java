package com.dayscab.driver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUploadVehicleBinding;
import com.dayscab.driver.models.ModelCarsType;
import com.dayscab.driver.models.ModelMake;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.Compress;
import com.dayscab.utils.InternetConnection;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SplittableRandom;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadVehicleAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 101;
    Context mContext = UploadVehicleAct.this;
    ActivityUploadVehicleBinding binding;
    private final int GALLERY = 0, CAMERA = 1;
    File vehicleImage;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String str_image_path;
    ArrayList<String> taxiNamesList = new ArrayList<>();
    ArrayList<String> taxiIdsList = new ArrayList<>();
    ArrayList<String> makeNameList = new ArrayList<>();
    ArrayList<String> makeIdList = new ArrayList<>();
    ArrayList<String> modelNameList = new ArrayList<>();
    ArrayList<String> modelIdList = new ArrayList<>();
    private String carId = "", makeId = "", modelId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_vehicle);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        getCars();

        binding.spinnerServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    carId = taxiIdsList.get(position);
                    Log.e("getCarsgetCars", "carId = " + carId);
                } catch (Exception e) {
                    Log.e("ExceptionException", "Exception = " + e.getMessage());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}

        });

        binding.ivUploadImage.setOnClickListener(v -> {
            if (checkPermissions()) {
                showPictureDialog();
            } else {
                requestPermissions();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (carId == null) {
                Toast.makeText(mContext, getString(R.string.select_vehicle_type), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etNumberPlate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_number_plate), Toast.LENGTH_SHORT).show();
            } else if (vehicleImage == null) {
                Toast.makeText(mContext, getString(R.string.upload_vehicle_text), Toast.LENGTH_SHORT).show();
            } else if (binding.spYear.getSelectedItemPosition() == 0) {
                Toast.makeText(mContext, getString(R.string.add_year_vehicle_text), Toast.LENGTH_SHORT).show();
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    addVehicle();
                } else {
                    Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getMake() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarMakeList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelMake modelCarsType = new Gson().fromJson(stringResponse, ModelMake.class);
                            for (ModelMake.Result item : modelCarsType.getResult()) {
                                makeNameList.add(item.getTitle());
                                makeIdList.add(item.getId());
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, makeNameList);
                            binding.spMakeType.setAdapter(arrayAdapter);
                            makeId = makeIdList.get(0);

                            binding.spMakeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        makeId = makeIdList.get(position);
                                        getModels(makeId);
                                        Log.e("getMake", "makeId = " + makeId);
                                    } catch (Exception e) {
                                        Log.e("ExceptionException", "Exception = " + e.getMessage());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}

                            });

                            // getModels(makeId);
                            Log.e("getMake", "response getMake = " + stringResponse);
                            Log.e("getMake", "makeId getMake = " + makeId);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
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

    private void getModels(String makeId) {
        // ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String, String> param = new HashMap<>();
        param.put("make_id", makeId);
        Call<ResponseBody> call = api.getCarModelCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelMake modelCarsType = new Gson().fromJson(stringResponse, ModelMake.class);
                            modelNameList = new ArrayList<>();
                            modelIdList = new ArrayList<>();
                            for (ModelMake.Result item : modelCarsType.getResult()) {
                                modelNameList.add(item.getTitle());
                                modelIdList.add(item.getId());
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, modelNameList);
                            binding.spModelType.setAdapter(arrayAdapter);
                            modelId = modelIdList.get(0);

                            binding.spModelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        modelId = modelIdList.get(position);
                                        Log.e("getCarsgetCars", "modelId = " + modelId);
                                    } catch (Exception e) {
                                        Log.e("ExceptionException", "Exception = " + e.getMessage());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}

                            });

                            Log.e("getModels", "response = " + stringResponse);
                            Log.e("getModels", "modelId = " + modelId);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
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

    private void getCars() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getCarList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(stringResponse);
                        if (jsonObject.getString("status").equals("1")) {
                            ModelCarsType modelCarsType = new Gson().fromJson(stringResponse, ModelCarsType.class);
                            for (ModelCarsType.Result item : modelCarsType.getResult()) {
                                taxiNamesList.add(item.getCar_name());
                                taxiIdsList.add(item.getId());
                            }
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                                    R.layout.support_simple_spinner_dropdown_item, taxiNamesList);
                            binding.spinnerServiceType.setAdapter(arrayAdapter);
                            carId = taxiNamesList.get(0);
                            getMake();
                            Log.e("getCarsgetCars", "response = " + stringResponse);
                            Log.e("getCarsgetCars", "carId = " + carId);
                        } else {
                            Toast.makeText(mContext, getString(R.string.no_data_found), Toast.LENGTH_LONG).show();
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

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, PERMISSION_ID);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(mContext);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ProjectUtil.openGallery(mContext, GALLERY);
                                break;
                            case 1:
                                str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        vehicleImage = file;
                        binding.ivUploadImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(path);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        vehicleImage = file;
                        binding.ivUploadImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(str_image_path);
            }
        }

    }

    private void addVehicle() {

        Log.e("gfsfasfasfas", "modelLogin.getResult().getId() = " + modelLogin.getResult().getId());
        Log.e("gfsfasfasfas", "carId = " + carId);
        Log.e("gfsfasfasfas", "makeId = " + makeId);
        Log.e("gfsfasfasfas", "modelId = " + modelId);
        Log.e("gfsfasfasfas", "modelId = " + modelId);
        Log.e("gfsfasfasfas", "binding.spYear.getSelectedItem().toString().trim() = " + binding.spYear.getSelectedItem().toString().trim());

//        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
//
//        MultipartBody.Part vehicleFilePart;
//
//        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
//        RequestBody carType = RequestBody.create(MediaType.parse("text/plain"), carId);
//        RequestBody carBrand = RequestBody.create(MediaType.parse("text/plain"), makeId);
//        RequestBody carModel = RequestBody.create(MediaType.parse("text/plain"), modelId);
//        RequestBody year = RequestBody.create(MediaType.parse("text/plain"), binding.spYear.getSelectedItem().toString().trim());
//        RequestBody carNumber = RequestBody.create(MediaType.parse("text/plain"), binding.etNumberPlate.getText().toString().trim());
//
//        vehicleFilePart = MultipartBody.Part.createFormData("car_image", vehicleImage.getName(), RequestBody.create(MediaType.parse("car_document/*"), vehicleImage));
//
//        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
//        Call<ResponseBody> call = api.addDriverVehicle(userId, carType, carBrand, carModel, carNumber, year, vehicleFilePart);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                ProjectUtil.pauseProgressDialog();
//                try {
//                    String responseString = response.body().string();
//                    JSONObject jsonObject = new JSONObject(responseString);
//
//                    Log.e("vehicleData", "responseString = " + responseString);
//
//                    if (jsonObject.getString("status").equals("1")) {
//
//                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
//                        modelLogin.getResult().setStep("2");
//                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
//
//                        startActivity(new Intent(mContext, UploadDriverDocumentsAct.class));
//                        finish();
//
////                      startActivity(new Intent(mContext, AddBankAccAct.class));
////                      finish();
//
//                    }
//
//                } catch (Exception e) {
//                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    Log.e("Exception", "Exception = " + e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                ProjectUtil.pauseProgressDialog();
//                Log.e("Exception", "Throwable = " + t.getMessage());
//            }
//
//        });
//    }

    }
}