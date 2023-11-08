package com.a2zsuvidhaa.in.activity.aeps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.a2zsuvidhaa.in.R;
import com.a2zsuvidhaa.in.util.APIs;

public class ViewPdfActivity extends AppCompatActivity {
    WebView web;
    private ProgressBar progressBarAccountNo;
    String name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        web=(WebView)findViewById(R.id.webview);
        progressBarAccountNo = findViewById(R.id.progress_accountNo);
       // progressBarAccountNo.setVisibility(View.VISIBLE);

        name=getIntent().getExtras().getString("name");

        web.getSettings().setJavaScriptEnabled(true);
        webview();

    }

    private void webview()
    {
        web.setScrollbarFadingEnabled(false);
        web.getSettings().setAllowFileAccessFromFileURLs(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web.getSettings().setBuiltInZoomControls(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(""+url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // super.onPageFinished(view, url);
                progressBarAccountNo.setVisibility(View.GONE);
            }
        });

        String Url;

        Url =  "http://docs.google.com/gview?embedded=true&url="+ APIs.PDF_BASE_URL+name;
        //Url =   APIs.PDF_BASE_URL+name;

        Log.e("url pdf",""+Url);
        web.loadUrl(Url.toString());

    }
}
