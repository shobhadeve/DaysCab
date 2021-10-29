package com.dayscab.driver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUploadDriverDocumentsBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.Compress;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDriverDocumentsAct extends AppCompatActivity {

    Context mContext = UploadDriverDocumentsAct.this;
    ActivityUploadDriverDocumentsBinding binding;
    int imageCapturedCode;
    private final int GALLERY = 0, CAMERA = 1;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    File lisenceFile, carRegistrationFile, vehicleRegistrationFile;
    private String str_image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_upload_driver_documents);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.btnSubmit.setOnClickListener(v -> {
            if (lisenceFile == null) {
                Toast.makeText(mContext, getString(R.string.driving_licesne_text), Toast.LENGTH_SHORT).show();
            } else if (carRegistrationFile == null) {
                Toast.makeText(mContext, getString(R.string.upload_registration_car), Toast.LENGTH_LONG).show();
            } else if (vehicleRegistrationFile == null) {
                Toast.makeText(mContext, getString(R.string.vehicle_reg_book), Toast.LENGTH_LONG).show();
            } else {
                addDocumentApiCall();
            }
        });

        binding.ivDriverLicense.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivRegistrationCertify.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivVehicleBook.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 3;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

    }

    private void addDocumentApiCall() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part lisenceFilePart;
        MultipartBody.Part responsibleFilePart;
        MultipartBody.Part identityFilePart;

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        lisenceFilePart = MultipartBody.Part.createFormData("driver_lisence_img", lisenceFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceFile));
        responsibleFilePart = MultipartBody.Part.createFormData("car_regist_img", carRegistrationFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), carRegistrationFile));
        identityFilePart = MultipartBody.Part.createFormData("vehicle_regist_img", vehicleRegistrationFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), vehicleRegistrationFile));

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addDriverDocumentApiCall(user_id, lisenceFilePart, responsibleFilePart, identityFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("documentsdriver", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        startActivity(new Intent(mContext, DriverHomeAct.class));
                        finish();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                Log.e("Exception", "Throwable = " + t.getMessage());
            }

        });


    }

    private void setImageFromCameraGallery(File file) {
        if (imageCapturedCode == 1) {
            lisenceFile = file;
            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    lisenceFile = file;
                    binding.ivDriverLicense.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            carRegistrationFile = file;
            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    carRegistrationFile = file;
                    binding.ivRegistrationCertify.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 3) {
            vehicleRegistrationFile = file;
            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                @Override
                public void response(boolean status, String message, File file) {
                    vehicleRegistrationFile = file;
                    binding.ivVehicleBook.setImageURI(Uri.parse(file.getPath()));
                }
            }).CompressedImage(file.getPath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                setImageFromCameraGallery(new File(str_image_path));
            }
        }

    }

    public void showPictureDialog() {
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



}