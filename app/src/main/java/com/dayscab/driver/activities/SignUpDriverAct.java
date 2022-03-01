package com.dayscab.driver.activities;

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
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.VerifyAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivitySignUpDriverBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.Compress;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.RealPathUtil;
import com.dayscab.utils.SharedPref;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignUpDriverAct extends AppCompatActivity {

    private static final int PERMISSION_ID = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE_CITY = 102;
    Context mContext = SignUpDriverAct.this;
    ActivitySignUpDriverBinding binding;
    private final int GALLERY = 0, CAMERA = 1;
    String type = "", registerId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    File profileImage;
    private String str_image_path;
    private LatLng latLng;
    String driverEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_driver);

        // setting up the flag programmatically so that the
        // device screen should be always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sharedPref = SharedPref.getInstance(mContext);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            if (!TextUtils.isEmpty(token)) {
                registerId = token;
                Log.e("tokentoken", "retrieve token successful : " + token);
            } else {
                Log.e("tokentoken", "token should not be null...");
            }
        }).addOnFailureListener(e -> {
        }).addOnCanceledListener(() -> {
        });

        driverEmail = getIntent().getStringExtra("email");

        if (!Places.isInitialized()) {
            Places.initialize(mContext, getString(R.string.places_api_key));
        }

        if (driverEmail != null) {
            binding.etEmail.setText(driverEmail);
        }

        itit();

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

    private void itit() {

//        binding.etAdd1.setOnClickListener(v -> {
//            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
//            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                    .build(this);
//            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
//        });

        binding.etCityName.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .setCountry(AppConstant.COUNTRY)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_CITY);
        });

        binding.addIcon.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
//          if (ProjectUtil.checkPermissions(mContext)) {
//              showPictureDialog();
//          } else {
//              ProjectUtil.requestPermissions(mContext);
//          }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etFirstName.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_name_firsttext));
            } else if (TextUtils.isEmpty(binding.etLastName.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_name_lasttext));
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_email_text));
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_phone_text));
            } else if (TextUtils.isEmpty(binding.etCityName.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_city_text));
            } else if (TextUtils.isEmpty(binding.etAdd1.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_address1_text));
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.enter_valid_email));
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                MyApplication.showAlert(mContext,getString(R.string.please_enter_pass));
            } else if (!(binding.etPassword.getText().toString().trim().length() > 8)) {
                MyApplication.showAlert(mContext,getString(R.string.password_validation_text));
            } else if (profileImage == null) {
                MyApplication.showAlert(mContext,getString(R.string.please_upload_profile));
            } else {
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, File> fileHashMap = new HashMap<>();

                params.put("first_name", binding.etFirstName.getText().toString().trim());
                params.put("last_name", binding.etLastName.getText().toString().trim());
                params.put("email", binding.etEmail.getText().toString().trim());
                params.put("mobile", binding.etPhone.getText().toString().trim());
                params.put("city", binding.etCityName.getText().toString().trim());
                params.put("address", binding.etAdd1.getText().toString().trim());
                params.put("register_id", registerId);
                params.put("lat", String.valueOf(latLng.latitude));
                params.put("lon", String.valueOf(latLng.longitude));
                params.put("password", binding.etPassword.getText().toString().trim());
                params.put("type", "DRIVER");

                fileHashMap.put("image", profileImage);


                Log.e("signupDriver", "signupDriver = " + params);
                Log.e("signupDriver", "fileHashMap = " + fileHashMap);

                 String mobileNumber = "+233" + binding.etPhone.getText().toString().trim();
                 // String mobileNumber = "+91" + binding.etPhone.getText().toString().trim();

                startActivity(new Intent(mContext, VerifyAct.class)
                        .putExtra("resgisterHashmap", params)
                        .putExtra("mobile", mobileNumber)
                        .putExtra("fileHashMap", fileHashMap)
                        .putExtra(AppConstant.TYPE, AppConstant.DRIVER)
                );

            }

        });

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

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profileImage = new File(RealPathUtil.getRealPath(mContext, resultUri));
                Glide.with(mContext).load(resultUri).into(binding.profileImage);
                Log.e("asfasdasdad", "resultUri = " + resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, place.getLatLng().latitude, place.getLatLng().longitude);
                    binding.etAdd1.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_CITY) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                latLng = place.getLatLng();
                try {
                    String addresses = place.getName();

                    Log.e("addresses", "addresses = " + addresses);
                    Log.e("addresses", "addresses = " + place.getName());

                    binding.etCityName.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == GALLERY) {
            if (resultCode == RESULT_OK) {
                String path = ProjectUtil.getRealPathFromURI(mContext, data.getData());
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage = file;
                        binding.profileImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(path);
            }
        } else if (requestCode == CAMERA) {
            if (resultCode == RESULT_OK) {
                profileImage = new File(str_image_path);
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage = file;
                        binding.profileImage.setImageURI(Uri.parse(file.getPath()));
                    }
                }).CompressedImage(str_image_path);
            }
        }

    }


}