package com.dayscab.driver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.dayscab.R;
import com.dayscab.databinding.ActivityWebviewNavBinding;

public class WebviewNavAct extends AppCompatActivity {

    Context mContext = WebviewNavAct.this;
    String url = "";
    ActivityWebviewNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_webview_nav);
        itit();
    }

    private void itit() {

        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.loadUrl("http://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355");
        // binding.webView.loadUrl("google.navigation:q=22.9675929,76.0534454");

    }


}