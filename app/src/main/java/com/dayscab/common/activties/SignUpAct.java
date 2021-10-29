package com.dayscab.common.activties;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivitySignUpBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignUpBinding binding;
    String type = "", registerId = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        type = getIntent().getStringExtra(AppConstant.TYPE);
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
        }).addOnCompleteListener(task -> Log.e("tokentoken", "This is the token : " + task.getResult()));


        itit();
    }

    private void itit() {

        binding.btnSignUp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_name_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_phone_text), Toast.LENGTH_SHORT).show();
            } else if (!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if (!(binding.etPassword.getText().toString().trim().length() > 4)) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String, String> params = new HashMap<>();

                params.put("user_name", binding.etName.getText().toString().trim());
                params.put("email", binding.etEmail.getText().toString().trim());
                params.put("mobile", binding.etPhone.getText().toString().trim());
                params.put("register_id", registerId);
                params.put("lat", "");
                params.put("lon", "");
                params.put("password", binding.etPassword.getText().toString().trim());
                params.put("type", "USER");

                String mobileNumber = "+233" + binding.etPhone.getText().toString().trim();

                startActivity(new Intent(mContext, VerifyAct.class)
                        .putExtra("resgisterHashmap", params)
                        .putExtra("mobile", mobileNumber)
                        .putExtra(AppConstant.TYPE, AppConstant.USER)
                );

            }

//            startActivity(new Intent(mContext, VerifyAct.class)
//                    .putExtra(AppContant.TYPE, AppContant.USER)
//            );

        });

        binding.ivLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext, LoginAct.class)
                    .putExtra(AppConstant.TYPE, AppConstant.USER)
            );
            finish();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}