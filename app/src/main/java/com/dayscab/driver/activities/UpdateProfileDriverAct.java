package com.dayscab.driver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.activties.VerifyAct;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUpdateProfileDriverBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.Compress;
import com.dayscab.utils.MyService;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.RealPathUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileDriverAct extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE_CITY = 102;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 101;
    Context mContext = UpdateProfileDriverAct.this;
    ActivityUpdateProfileDriverBinding binding;
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile_driver);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {


        try {
            latLng = new LatLng(Double.parseDouble(modelLogin.getResult().getLat()),
                    Double.parseDouble(modelLogin.getResult().getLon()));
        } catch (Exception e) {
        }

        binding.etFirstName.setText(modelLogin.getResult().getFirst_name());
        binding.etLastName.setText(modelLogin.getResult().getLast_name());
        binding.etPhone.setText(modelLogin.getResult().getMobile());
        binding.etEmail.setText(modelLogin.getResult().getEmail());
        binding.etCityName.setText(modelLogin.getResult().getCity());
        binding.etAdd1.setText(modelLogin.getResult().getAddress());

        Log.e("modelLogin", "modelLogin = " + new Gson().toJson(modelLogin));

        Glide.with(mContext).load(modelLogin.getResult().getImage())
                .into(binding.profileImage);

        binding.etCityName.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_CITY);
        });

        binding.addIcon.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                showPictureDialog();
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnUpodate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etFirstName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_firsttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etLastName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_lasttext), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etCityName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_city_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etAdd1.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_address1_text), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            } else {
                updateDriverProfile();
            }
        });

    }

    private void updateDriverProfile() {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        MultipartBody.Part profileFilePart;
        RequestBody lat, lon;

        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), binding.etFirstName.getText().toString().trim());
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), binding.etLastName.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString().trim());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.etPhone.getText().toString().trim());
        RequestBody city = RequestBody.create(MediaType.parse("text/plain"), binding.etCityName.getText().toString().trim());
        RequestBody address = RequestBody.create(MediaType.parse("text/plain"), binding.etAdd1.getText().toString().trim());

        try {
            lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.latitude));
            lon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(latLng.longitude));
        } catch (Exception e) {
            lat = RequestBody.create(MediaType.parse("text/plain"), "");
            lon = RequestBody.create(MediaType.parse("text/plain"), "");
        }

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "DRIVER");
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody attachmentEmpty;

        if (profileImage == null) {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            profileFilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        } else {
            profileFilePart = MultipartBody.Part.createFormData("image", profileImage.getName(), RequestBody.create(MediaType.parse("car_document/*"), profileImage));
        }

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateDriverCallApi(first_name, last_name, email, mobile, city,
                address, lat, lon, type, id, profileFilePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("driversignup", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
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
                Log.e("sfasfdsfd", "GALLERY = ");
                String path = RealPathUtil.getRealPath(mContext, data.getData());
                Compress.get(mContext).setQuality(80).execute(new Compress.onSuccessListener() {
                    @Override
                    public void response(boolean status, String message, File file) {
                        profileImage = file;
                        Log.e("sfasfdsfd", "GALLERY Image = " + profileImage);
                        binding.profileImage.setImageURI(data.getData());
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