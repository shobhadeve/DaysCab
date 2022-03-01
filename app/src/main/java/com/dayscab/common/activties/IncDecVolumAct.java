package com.dayscab.common.activties;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dayscab.R;
import com.dayscab.common.models.ModelLogin;
import com.dayscab.databinding.ActivityIncDecVolumBinding;
import com.dayscab.utils.AppConstant;
import com.dayscab.utils.MyApplication;
import com.dayscab.utils.ProjectUtil;
import com.dayscab.utils.SharedPref;
import com.dayscab.utils.retrofitutils.Api;
import com.dayscab.utils.retrofitutils.ApiFactory;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncDecVolumAct extends AppCompatActivity {

    private AudioManager audioManager;
    // Variable to store brightness value
    private int brightness;
    // Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    // Window object, that will store a reference to the current window
    private Window window;
    int maxVolume = 1;
    ActivityIncDecVolumBinding binding;
    Context mContext = IncDecVolumAct.this;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    boolean isMusicMute, isMusicRequestMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inc_dec_volum);
        MyApplication.checkToken(mContext);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initializeControls();
    }

    private void initializeControls() {

        if (modelLogin.getResult().getMute_music() == null ||
                modelLogin.getResult().getMute_music().equals("")) {
            binding.toggleMuteMusic.setOn(true);
        } else {
            if ("True".equals(modelLogin.getResult().getMute_music())) {
                isMusicMute = true;
                binding.toggleMuteMusic.setOn(true);
            } else {
                isMusicMute = false;
                binding.toggleMuteMusic.setOn(false);
            }
        }

        if (modelLogin.getResult().getMute_request_sound() == null || modelLogin.getResult().getMute_request_sound().equals("")) {
            binding.toggleMuteRequestMusic.setOn(true);
        } else {
            if ("True".equals(modelLogin.getResult().getMute_request_sound())) {
                isMusicRequestMute = true;
                binding.toggleMuteRequestMusic.setOn(true);
            } else {
                isMusicRequestMute = false;
                binding.toggleMuteRequestMusic.setOn(false);
            }
        }

        binding.toggleMuteMusic.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    Log.e("asdasdasdas","isMusicMute = True");
                    isMusicMute = true;
                } else {
                    Log.e("asdasdasdas","isMusicMute = false");
                    isMusicMute = false;
                }
            }
        });

        binding.toggleMuteRequestMusic.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    Log.e("asdasdasdas","isMusicRequestMute = True");
                    isMusicRequestMute = true;
                } else {
                    Log.e("asdasdasdas","isMusicRequestMute = False");
                    isMusicRequestMute = false;
                }
            }
        });

        binding.btSave.setOnClickListener(v -> {
            saveSounddetails();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        try {

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //set max progress according to volume
            binding.sbVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            //get current volume
            binding.sbVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            //Set the seek bar progress to 1
            binding.sbVolume.setKeyProgressIncrement(1);
            //get max volume
            maxVolume = binding.sbVolume.getMax();

            float perc = (binding.sbVolume.getProgress() / (float) maxVolume) * 100;
            binding.tvVolume.setText("Volume: " + (int) perc + " %");

            binding.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    // Calculate the brightness percentage
                    float perc = (progress / (float) maxVolume) * 100;
                    // Set the brightness percentage
                    binding.tvVolume.setText("Volume: " + (int) perc + " %");
                }
            });

        } catch (Exception e) {}

    }

    private void saveSounddetails() {

        HashMap<String, String> map = new HashMap<>();

        map.put("user_id", modelLogin.getResult().getId());

        if (isMusicMute) {
            map.put("mute_music", "True");
        } else {
            map.put("mute_music", "False");
        }

        if (isMusicRequestMute) {
            map.put("mute_request_sound", "True");
        } else {
            map.put("mute_request_sound", "False");
        }

        Log.e("managerSoundApi", "managerSoundApi = " + map);

        ProjectUtil.showProgressDialog(mContext, false, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.managerSoundApi(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringResponse = response.body().string();

                    Log.e("asdfasdfasdfas", "managerSoundApi = " + stringResponse);

                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.getString("status").equals("1")) {
                        if (isMusicMute) modelLogin.getResult().setMute_music("True");
                        else modelLogin.getResult().setMute_music("False");

                        if (isMusicRequestMute)
                            modelLogin.getResult().setMute_request_sound("True");
                        else modelLogin.getResult().setMute_request_sound("False");

                        sharedPref.setUserDetails(AppConstant.USER_DETAILS, modelLogin);

                        finish();
                        Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("status").equals("2")) {

                    }

                } catch (Exception e) {
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