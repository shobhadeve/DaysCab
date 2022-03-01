package com.dayscab.driver.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.LoginAct;
import com.dayscab.common.activties.StartAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUploadDriverDocumentsBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.Compress;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.RealPathUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

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
    File lisenceFile, lisenceFileBack, carRegistrationFile, vehicleRegistrationFile, vehicleInspectionFile;
    private String str_image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_driver_documents);
        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        binding.btnSubmit.setOnClickListener(v -> {
            if (lisenceFile == null) {
                Toast.makeText(mContext, getString(R.string.driving_licesne_text), Toast.LENGTH_SHORT).show();
            } else if (lisenceFileBack == null) {
                Toast.makeText(mContext, getString(R.string.driving_licesne_back), Toast.LENGTH_LONG).show();
            } else if (carRegistrationFile == null) {
                Toast.makeText(mContext, getString(R.string.upload_registration_car), Toast.LENGTH_LONG).show();
            } else if (vehicleRegistrationFile == null) {
                Toast.makeText(mContext, getString(R.string.vehicle_reg_book), Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.etExpDriveLicense.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_exipry_for_license), Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.etExpInsurance.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_exipry_for_insurance), Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.etExpDVIA.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_exipry_for_roadworhtiness), Toast.LENGTH_LONG).show();
            } else {
                addDocumentApiCall();
            }
        });

        binding.etExpDriveLicense.setOnClickListener(v -> {

            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String month = String.valueOf((monthOfYear + 1));
                    String day = String.valueOf((dayOfMonth));

                    if (month.length() == 1) month = "0" + month;

                    if (day.length() == 1) day = "0" + day;
                    binding.etExpDriveLicense.setText(year + "-" + month + "-" + day);
                }
            }, mYear, mMonth, mDay);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();
        });

        binding.etExpInsurance.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String month = String.valueOf((monthOfYear + 1));
                    String day = String.valueOf((dayOfMonth));

                    if (month.length() == 1) month = "0" + month;

                    if (day.length() == 1) day = "0" + day;

                    binding.etExpInsurance.setText(year + "-" + month + "-" + day);
                }
            }, mYear, mMonth, mDay);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();
        });

        binding.etExpDVIA.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            // Launch Date Picker Dialog
            DatePickerDialog dpd = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    String month = String.valueOf((monthOfYear + 1));
                    String day = String.valueOf((dayOfMonth));

                    if (month.length() == 1) month = "0" + month;

                    if (day.length() == 1) day = "0" + day;


                    binding.etExpDVIA.setText(year + "-" + month + "-" + day);
                }
            }, mYear, mMonth, mDay);
            dpd.getDatePicker().setMinDate(new Date().getTime());
            dpd.show();
        });

        binding.ivDriverLicense.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 1;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                // showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivVehicleInspection.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 5;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                // showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivDriverLicenseBack.setOnClickListener(v -> {
            Log.e("ImageCapture", "ivDriverLisenceImg");
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 4;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                // showPictureDialog();
            } else {
                Log.e("ImageCapture", "requestPermissions");
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivRegistrationCertify.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 2;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                // showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivVehicleBook.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                imageCapturedCode = 3;
                Log.e("ImageCapture", "imageCapturedCode = " + imageCapturedCode);
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
                // showPictureDialog();
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
        MultipartBody.Part lisenceFilePartBack;
        MultipartBody.Part vehicleInspectionPart;
        RequestBody attachmentEmpty;

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody lisence_date = RequestBody.create(MediaType.parse("text/plain"), binding.etExpDriveLicense.getText().toString().trim());
        RequestBody carregiterDate = RequestBody.create(MediaType.parse("text/plain"), binding.etExpInsurance.getText().toString().trim());
        RequestBody vehicleRegisterDate = RequestBody.create(MediaType.parse("text/plain"), binding.etExpDVIA.getText().toString().trim());
        lisenceFilePart = MultipartBody.Part.createFormData("driver_lisence_img", lisenceFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceFile));
        responsibleFilePart = MultipartBody.Part.createFormData("car_regist_img", carRegistrationFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), carRegistrationFile));
        identityFilePart = MultipartBody.Part.createFormData("vehicle_regist_img", vehicleRegistrationFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), vehicleRegistrationFile));
        lisenceFilePartBack = MultipartBody.Part.createFormData("driver_lisence_img_back", lisenceFileBack.getName(), RequestBody.create(MediaType.parse("car_document/*"), lisenceFileBack));

        if (vehicleInspectionFile == null) {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            vehicleInspectionPart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        } else {
            vehicleInspectionPart = MultipartBody.Part.createFormData("vehicle_inspection_img", vehicleInspectionFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), vehicleInspectionFile));
        }

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.addDriverDocumentApiCall(user_id, lisence_date, carregiterDate,
                vehicleRegisterDate, lisenceFilePart, responsibleFilePart,
                identityFilePart, lisenceFilePartBack, vehicleInspectionPart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("documentsdriver", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
//                      modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
//                      sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        approvalAdmin();
                    }

                } catch (Exception e) {
                   // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void approvalAdmin() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.deactivate_account_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finishAffinity();
                                startActivity(new Intent(mContext, StartAct.class)
                                        .putExtra(AppConstant.TYPE, AppConstant.DRIVER)
                                );
                            }
                        }).create().show();
    }

    private void setImageFromCameraGallery(File file) {
        Log.e("setImageFromCameraGallery","file = " + file.getPath());
        if (imageCapturedCode == 1) {
            lisenceFile = file;
            binding.ivDriverLicense.setImageURI(Uri.parse(file.getPath()));
//            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
//                @Override
//                public void response(boolean status, String message, File file) {
//                    lisenceFile = file;
//
//                }
//            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 2) {
            carRegistrationFile = file;
            binding.ivRegistrationCertify.setImageURI(Uri.parse(file.getPath()));
//            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
//                @Override
//                public void response(boolean status, String message, File file) {
//                    carRegistrationFile = file;
//                    binding.ivRegistrationCertify.setImageURI(Uri.parse(file.getPath()));
//                }
//            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 3) {
            vehicleRegistrationFile = file;
            binding.ivVehicleBook.setImageURI(Uri.parse(file.getPath()));
//            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
//                @Override
//                public void response(boolean status, String message, File file) {
//                    vehicleRegistrationFile = file;
//                    binding.ivVehicleBook.setImageURI(Uri.parse(file.getPath()));
//                }
//            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 4) {
            lisenceFileBack = file;
            binding.ivDriverLicenseBack.setImageURI(Uri.parse(file.getPath()));
//            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
//                @Override
//                public void response(boolean status, String message, File file) {
//                    lisenceFileBack = file;
//                    binding.ivDriverLicenseBack.setImageURI(Uri.parse(file.getPath()));
//                }
//            }).CompressedImage(file.getPath());
        } else if (imageCapturedCode == 5) {
            vehicleInspectionFile = file;
            binding.ivVehicleInspection.setImageURI(Uri.parse(file.getPath()));
//            Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
//                @Override
//                public void response(boolean status, String message, File file) {
//                    vehicleInspectionFile = file;
//                    binding.ivVehicleInspection.setImageURI(Uri.parse(file.getPath()));
//                }
//            }).CompressedImage(file.getPath());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(RealPathUtil.getRealPath(mContext, resultUri));
                setImageFromCameraGallery(file);
                Log.e("asfasdasdad", "resultUri = " + resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = RealPathUtil.getRealPath(mContext, data.getData());
                setImageFromCameraGallery(new File(path));
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                try {

                    if (data != null) {

                        Bundle extras = data.getExtras();
                        Bitmap bitmapNew = (Bitmap) extras.get("data");
                        Bitmap imageBitmap = BITMAP_RE_SIZER(bitmapNew, bitmapNew.getWidth(), bitmapNew.getHeight());

                        Uri tempUri = ProjectUtil.getImageUri(mContext, imageBitmap);

                        String image = RealPathUtil.getRealPath(mContext, tempUri);
                        // Toast.makeText(mContext, "Camera path = " + image, Toast.LENGTH_SHORT).show();
                        setImageFromCameraGallery(new File(image));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // setImageFromCameraGallery(new File(str_image_path));
            }
        }

    }

    public Bitmap BITMAP_RE_SIZER(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) bitmap.getWidth();
        float ratioY = newHeight / (float) bitmap.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

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
                                //ProjectUtil.openGallery(mContext, GALLERY);
                                Intent pickPhoto = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                pickPhoto.setType("image/*");
                                startActivityForResult(pickPhoto, GALLERY);
                                break;
                            case 1:
                                // str_image_path = ProjectUtil.openCamera(mContext, CAMERA);
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null)
                                    startActivityForResult(cameraIntent, CAMERA);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


}