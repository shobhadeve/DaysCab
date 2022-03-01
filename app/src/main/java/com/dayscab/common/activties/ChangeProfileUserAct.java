package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.dayscab.R;

public class ChangeProfileUserAct extends AppCompatActivity {

    Context mContext = ChangeProfileUserAct.this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_user);
    }
    
    
}