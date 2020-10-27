package com.medsedki.webviewtestapp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.progress_horizontal)
    ProgressBar mProgress;
    @BindView(R.id.image)
    ImageButton mImage;
    @BindView(R.id.web_view)
    WebView mWebview;

    String WEBSITE_URL = "https://protocoderspoint.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // Set the upper range of the progress bar max
        mProgress.setMax(100);

        // to load all the Web page in app itself use this
        mWebview.setWebViewClient(new WebViewClient());

        mWebview.loadUrl(WEBSITE_URL);

        // If ur website using any javaScript, that need to load some script
        // Then you need to enable javaScript in the android application
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebview.setWebViewClient(new WebViewClient());
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgress.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                mImage.setImageBitmap(icon);
            }
        });

    }

    @Override
    public void onBackPressed() {
        // If any previous webpage exist the OnBackPressed will take care of it..
        if (mWebview.canGoBack()) {
            mWebview.goBack();

        } else {
            // else it will exit the application

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.alert_title));
            alertDialog.setMessage(getString(R.string.alert_message));
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_quit),
                    (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_cancel),
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();

            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_back:
                onBackPressed();
                break;
            case R.id.menu_forward:
                onForwardPressed();
                break;
            case R.id.menu_refresh:
                mWebview.reload();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onForwardPressed() {

        if (mWebview.canGoForward()) {
            mWebview.goForward();
        } else {
            Toast.makeText(this, getString(R.string.toast_forward), Toast.LENGTH_SHORT).show();
        }
    }
}