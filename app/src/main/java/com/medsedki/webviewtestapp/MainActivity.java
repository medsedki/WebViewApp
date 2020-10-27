package com.medsedki.webviewtestapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout)
    LinearLayout mLayout;
    @BindView(R.id.progress_horizontal)
    ProgressBar mProgress;
    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.web_view)
    WebView mWebview;

    //String WEBSITE_URL = "https://protocoderspoint.com/";
    String WEBSITE_URL = "https://www.google.com/";
    String CURRENT_URL;
    private ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // Set the upper range of the progress bar max
        mProgress.setMax(100);

        // to load all the Web page in app itself use this
        mWebview.setWebViewClient(new WebViewClient());

        mWebview.loadUrl(WEBSITE_URL);

        // If ur website using any javaScript, that need to load some script
        // Then you need to enable javaScript in the android application
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("PageView", "__onPageStarted");
                mLayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("PageView", "__onPageFinished");
                mLayout.setVisibility(View.GONE);
                mSwipeLayout.setRefreshing(false);
                super.onPageFinished(view, url);
                CURRENT_URL = url;
            }
        });
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

        mSwipeLayout.setOnRefreshListener(() -> mWebview.reload());

        // Download files:
        mWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

                DownloadManager.Request myRequest = new DownloadManager.Request(Uri.parse(url));
                myRequest.allowScanningByMediaScanner();
                myRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                DownloadManager myManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                myManager.enqueue(myRequest);

                Toast.makeText(MainActivity.this, getString(R.string.file_download), Toast.LENGTH_SHORT).show();

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
        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);*/
        getMenuInflater().inflate(R.menu.menu, menu);
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
            case R.id.menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                //shareIntent.putExtra(Intent.EXTRA_TEXT, "Some description");
                shareIntent.putExtra(Intent.EXTRA_TEXT, CURRENT_URL);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
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


    @Override
    protected void onStart() {
        super.onStart();
        mSwipeLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (mWebview.getScrollY() == 0)
                            mSwipeLayout.setEnabled(true);
                        else
                            mSwipeLayout.setEnabled(false);

                    }
                });
    }

    @Override
    protected void onStop() {
        mSwipeLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);

        super.onStop();
    }
}