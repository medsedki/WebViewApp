package com.medsedki.webviewtestapp;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.web_view)
    WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // to load all the Web page in app itself use this
        mWebview.setWebViewClient(new WebViewClient());

        //https://protocoderspoint.com/
        mWebview.loadUrl("https://protocoderspoint.com/");

        // If ur website using any javaScript, that need to load some script
        // Then you need to enable javaScript in the android application
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);


    }

    @Override
    public void onBackPressed() {
        // If any previous webpage exist the OnBackPressed will take care of it..
        if (mWebview.canGoBack()) {
            mWebview.goBack();

        } else {
            // else it will exit the application
            super.onBackPressed();
        }
    }
}