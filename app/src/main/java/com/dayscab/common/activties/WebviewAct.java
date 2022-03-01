package com.dayscab.common.activties;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dayscab.R;
import com.dayscab.databinding.ActivityWebviewBinding;
import com.dayscab.utils.ProjectUtil;
import com.google.android.gms.maps.Projection;

public class WebviewAct extends AppCompatActivity {

    Context mContext = WebviewAct.this;
    ActivityWebviewBinding binding;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_webview);
        url = getIntent().getStringExtra("url");
        itit();
    }

    private void itit() {

        binding.webView.getSettings().setJavaScriptEnabled(true);
         binding.webView.getSettings().setSupportZoom(true);
         binding.webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
         binding.webView.getSettings().setDisplayZoomControls(false);
        // Enable responsive layout
        binding.webView.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        // Configure related browser settings
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        binding.webView.setWebViewClient(new WebViewClient());

        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProjectUtil.showProgressDialog(mContext, false, getString(R.string.please_wait));
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ProjectUtil.pauseProgressDialog();
            }
        });
        
        // Load the initial URL
        binding.webView.loadUrl(url);

    }


}