package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityUpdateProfileBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.RealPathUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileUserAct extends AppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE_HOME = 101;
    private static final int AUTOCOMPLETE_REQUEST_CODE_WORK = 102;
    Context mContext = UpdateProfileUserAct.this;
    ActivityUpdateProfileBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    File profileImageFile = null;
    LatLng homeLatLng, workLatlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        itit();
    }

    private void itit() {

        try {
            Glide.with(mContext).load(modelLogin.getResult().getImage())
                    .placeholder(R.drawable.user_ic)
                    .error(R.drawable.user_ic)
                    .into(binding.profileImage);
        } catch (Exception e) {
        }

        try {
            homeLatLng = new LatLng(Double.parseDouble(modelLogin.getResult().getLat())
                    , Double.parseDouble(modelLogin.getResult().getLon()));
        } catch (Exception e) {
        }

        try {
            workLatlong = new LatLng(Double.parseDouble(modelLogin.getResult().getWork_lat())
                    , Double.parseDouble(modelLogin.getResult().getWork_lon()));
        } catch (Exception e) {
        }

        binding.etWorkAddress.setText(modelLogin.getResult().getWork_address());
        binding.etHomeAddress.setText(modelLogin.getResult().getAddress());
        binding.etEmail.setText(modelLogin.getResult().getEmail());
        binding.etName.setText(modelLogin.getResult().getUser_name());

        binding.etHomeAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_HOME);
        });

        binding.etWorkAddress.setOnClickListener(v -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_WORK);
        });

        if (modelLogin.getResult().getMobile() == null || modelLogin.getResult().getMobile().length() < 4) {
            binding.etPhone.setText("");
        } else {
            binding.etPhone.setText(modelLogin.getResult().getMobile());
        }

        binding.addIcon.setOnClickListener(v -> {
            if (ProjectUtil.checkPermissions(mContext)) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                ProjectUtil.requestPermissions(mContext);
            }
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnUpdate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else {
                updateProfile();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profileImageFile = new File(RealPathUtil.getRealPath(mContext, resultUri));
                Glide.with(mContext).load(resultUri).into(binding.profileImage);
                Log.e("asfasdasdad", "resultUri = " + resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_HOME) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                homeLatLng = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, homeLatLng.latitude, homeLatLng.longitude);

                    Log.e("addresses", "addresses = " + addresses);
                    Log.e("addresses", "addresses = " + place.getName());

                    binding.etHomeAddress.setText(addresses);
                } catch (Exception e) {
                }
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE_WORK) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                workLatlong = place.getLatLng();
                try {
                    String addresses = ProjectUtil.getCompleteAddressString(mContext, workLatlong.latitude, workLatlong.longitude);

                    Log.e("addresses", "addresses = " + addresses);
                    Log.e("addresses", "addresses = " + place.getName());

                    binding.etWorkAddress.setText(addresses);
                } catch (Exception e) {
                }
            }
        }
    }

    private void updateProfile() {

        MultipartBody.Part profileFilePart;
        RequestBody lat, lon, workLat, workLon;
        RequestBody attachmentEmpty;

        if (homeLatLng == null) {
            lat = RequestBody.create(MediaType.parse("text/plain"), "");
            lon = RequestBody.create(MediaType.parse("text/plain"), "");
        } else {
            lat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(homeLatLng.latitude));
            lon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(homeLatLng.longitude));
        }

        if (workLatlong == null) {
            workLat = RequestBody.create(MediaType.parse("text/plain"), "");
            workLon = RequestBody.create(MediaType.parse("text/plain"), "");
        } else {
            workLat = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLatlong.latitude));
            workLon = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(workLatlong.longitude));
        }

        RequestBody user_name = RequestBody.create(MediaType.parse("text/plain"), binding.etName.getText().toString().trim());
        RequestBody workAddress = RequestBody.create(MediaType.parse("text/plain"), binding.etWorkAddress.getText().toString().trim());
        RequestBody homeAddress = RequestBody.create(MediaType.parse("text/plain"), binding.etHomeAddress.getText().toString().trim());
        RequestBody mobile = RequestBody.create(MediaType.parse("text/plain"), binding.etPhone.getText().toString().trim());
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), binding.etEmail.getText().toString().trim());
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), modelLogin.getResult().getId());
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "USER");

        if (profileImageFile == null) {
            attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "");
            profileFilePart = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
        } else {
            profileFilePart = MultipartBody.Part.createFormData("image", profileImageFile.getName(), RequestBody.create(MediaType.parse("car_document/*"), profileImageFile));
        }

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.updateUserProfile(user_name, mobile, email, id, type,
                workAddress, workLon, workLat, homeAddress, lat, lon, profileFilePart);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("adasdasdas", "response = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);
                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);
                        finish();
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toast.makeText(mContext, getString(R.string.email_exist), Toast.LENGTH_SHORT).show();
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

}