package com.dayscab.common.activties;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityLoginBinding;
import com.dayscab.driver.activities.DriverHomeAct;
import com.dayscab.driver.activities.SignUpDriverAct;
import com.dayscab.user.activities.UserHomeAct;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.InternetConnection;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.MyService;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginAct extends AppCompatActivity {

    Context mContext = LoginAct.this;
    ActivityLoginBinding binding;
    String type = "";
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private String registerId = "";
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1234;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPref = SharedPref.getInstance(mContext);

        FirebaseApp.initializeApp(mContext);
        callbackManager = CallbackManager.Factory.create();

        type = getIntent().getStringExtra(AppConstant.TYPE);

        if (AppConstant.DRIVER.equals(type)) {
            binding.loginSocial.setVisibility(View.GONE);
            binding.llSocial.setVisibility(View.GONE);
        } else {
            binding.loginSocial.setVisibility(View.VISIBLE);
            binding.llSocial.setVisibility(View.VISIBLE);
        }

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        itit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // getProfileApiCall();
    }

    private void getProfileApiCall() {
        // ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_id", modelLogin.getResult().getId());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.getProfileCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringResponse = response.body().string();

                    try {

                        JSONObject jsonObject = new JSONObject(stringResponse);

                        if (jsonObject.getString("status").equals("1")) {

                            Log.e("getProfileApiCall", "getProfileApiCall = " + stringResponse);

                            modelLogin = new Gson().fromJson(stringResponse, ModelLogin.class);

                            Log.e("adfasdfss", "getDriver_lisence_img = " + modelLogin.getResult().getDriver_lisence_img());

//                            if (!registerId.equals(modelLogin.getResult().getRegister_id())) {
//                                logoutAlertDialog();
//                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("JSONException", "JSONException = " + e.getMessage());
                    }

                    //  Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();

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

    private void logoutAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("You are logged in with another device do you want to login with these device?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loginApiCall();
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void logoutAlertDialogGoogleFB(FirebaseUser user, String profilePhoto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("You are logged in with another device do you want to login with these device?")
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok)
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                socialLoginCall(user.getDisplayName(),
                                        user.getEmail(), profilePhoto,
                                        user.getUid());
                                dialog.dismiss();
                            }
                        }).create().show();
    }

    private void itit() {

        binding.cvGoogle.setOnClickListener(v -> {
            ProjectUtil.blinkAnimation(binding.cvGoogle);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
        });

        binding.cvFacebook.setOnClickListener(v -> {
            ProjectUtil.blinkAnimation(binding.cvFacebook);
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.e("kjsgdfkjdgsf", "onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("kjsgdfkjdgsf", "error = " + error.getMessage());
                }

            });

        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btSignup.setOnClickListener(v -> {
            if (type.equals(AppConstant.USER)) startActivity(new Intent(mContext, SignUpAct.class));
            else startActivity(new Intent(mContext, SignUpDriverAct.class));
        });

        binding.ivForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ForgotPasswordAct.class)
                    .putExtra("type", type)
            );
        });

        binding.btnSignin.setOnClickListener(v -> {

            if (TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.enter_email_text), Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(binding.etPassword.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else {
                if (InternetConnection.checkConnection(mContext)) {
                    loginApiCall();
                   // loginCheck();
                } else {
                    Toast.makeText(mContext, getString(R.string.check_internet_text), Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            String profilePhoto = "https://graph.facebook.com/" + token.getUserId() + "/picture?height=500";

                            Log.e("kjsgdfkjdgsf", "profilePhoto = " + profilePhoto);
                            Log.e("kjsgdfkjdgsf", "name = " + user.getDisplayName());
                            Log.e("kjsgdfkjdgsf", "email = " + user.getEmail());
                            Log.e("kjsgdfkjdgsf", "Userid = " + user.getUid());

                            // loginCheckGoogleFB(user, profilePhoto);
                            socialLoginCall(user.getDisplayName(),
                                    user.getEmail(), profilePhoto,
                                    user.getUid());

                        } else {
                            Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void socialLoginCall(String username, String email, String image, String socialId) {

        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("user_name", username);
        if (email == null) {
            paramHash.put("email", "");
        } else {
            paramHash.put("email", email);
        }
        paramHash.put("mobile", "");
        paramHash.put("lat", "");
        paramHash.put("lon", "");
        paramHash.put("image", image);
        paramHash.put("type", type);
        paramHash.put("register_id", registerId);
        paramHash.put("social_id", socialId);

        Log.e("socialLogin", "socialLogin = " + paramHash);

        Call<ResponseBody> call = api.socialLogin(paramHash);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {

                    String stringRes = response.body().string();

                    JSONObject jsonObject = new JSONObject(stringRes);

                    if (jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(stringRes, ModelLogin.class);

                        Log.e("jafhkjdf", "stringRes = " + stringRes);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);

                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        if (type.equals(AppConstant.USER))
                            startActivity(new Intent(mContext, UserHomeAct.class));
                        else {
                            startActivity(new Intent(mContext, SignUpDriverAct.class)
                                    .putExtra("email", binding.etEmail.getText().toString().trim())
                            );
                            Toast.makeText(mContext, getString(R.string.please_enter_details), Toast.LENGTH_SHORT).show();
                        }

                        finish();

                        // Toast.makeText(mContext, getString(R.string.successful), Toast.LENGTH_SHORT).show();

                    } else {
                        // Toast.makeText(mContext, getString(R.string.unsuccessful), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        signOut();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.getResult();
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {

                                Log.e("kjsgdfkjdgsf", "profilePhoto = " + user.getPhotoUrl());
                                Log.e("kjsgdfkjdgsf", "name = " + user.getDisplayName());
                                Log.e("kjsgdfkjdgsf", "email = " + user.getEmail());
                                Log.e("kjsgdfkjdgsf", "Userid = " + user.getUid());

                               // loginCheckGoogleFB(user, String.valueOf(user.getPhotoUrl()));

                                socialLoginCall(user.getDisplayName(),
                                        user.getEmail(), String.valueOf(user.getPhotoUrl()),
                                        user.getUid());

                            }

                        } else {
                        }

                    }
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            /* Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }

        }

    }

    private void loginCheckGoogleFB(FirebaseUser user, String profilePhoto) {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        if(user.getEmail() == null || user.getEmail().equals("")) {
            socialLoginCall(user.getDisplayName(),
                    user.getEmail(), profilePhoto,
                    user.getUid());
        } else {
            HashMap<String, String> paramHash = new HashMap<>();
            paramHash.put("email", user.getEmail());
            paramHash.put("register_id", registerId);

            Log.e("asdfasdfasf", "paramHash = " + paramHash);

            Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
            Call<ResponseBody> call = api.checkLoginValidCall(paramHash);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ProjectUtil.pauseProgressDialog();
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        Log.e("responseString", "responseString = " + responseString);

                        if (jsonObject.getString("status").equals("1")) {
                            socialLoginCall(user.getDisplayName(),
                                    user.getEmail(), profilePhoto,
                                    user.getUid());
                        } else if (jsonObject.getString("status").equals("2")) {
                            socialLoginCall(user.getDisplayName(),
                                    user.getEmail(), profilePhoto,
                                    user.getUid());
                        } else {
                            logoutAlertDialogGoogleFB(user, profilePhoto);
                        }
                    } catch (Exception e) {
                        // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void loginCheck() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("register_id", registerId);

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.checkLoginValidCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        loginApiCall();
                    } else if (jsonObject.getString("status").equals("2")) {
                        MyApplication.showAlert(mContext, getString(R.string.email_not_exist));
                    } else {
                        logoutAlertDialog();
                    }

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void loginApiCall() {
        ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));

        HashMap<String, String> paramHash = new HashMap<>();
        paramHash.put("email", binding.etEmail.getText().toString().trim());
        paramHash.put("password", binding.etPassword.getText().toString().trim());
        paramHash.put("lat", "");
        paramHash.put("lon", "");

        if (type.equals(AppConstant.USER)) paramHash.put("type", AppConstant.USER);
        else paramHash.put("type", AppConstant.DRIVER);

        paramHash.put("register_id", registerId);

        Log.e("asdfasdfasf", "paramHash = " + paramHash);

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.loginApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("responseString", "responseString = " + responseString);

                    if (jsonObject.getString("status").equals("1") ||
                            jsonObject.getString("status").equals("2")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER, true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        ContextCompat.startForegroundService(getApplicationContext()
                                , new Intent(getApplicationContext(), MyService.class));

                        if (modelLogin.getResult().getType().equals(AppConstant.USER)) {
                            startActivity(new Intent(mContext, UserHomeAct.class));
                            finish();
                        } else {
                            // ContextCompat.startForegroundService(LoginAct.this, new Intent(LoginAct.this, MyService.class));
                            startActivity(new Intent(mContext, DriverHomeAct.class));
                            finish();
                        }

                    } else if (jsonObject.getString("status").equals("3")) {
                        MyApplication.showAlert(mContext, getString(R.string.deactivate_account_text));
                    } else {
                        Toast.makeText(LoginAct.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
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