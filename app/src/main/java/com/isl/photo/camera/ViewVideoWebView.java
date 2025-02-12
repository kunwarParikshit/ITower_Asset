package com.isl.photo.camera;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.isl.util.Utils;

import java.io.File;

import infozech.itower.R;


public class ViewVideoWebView extends Activity {

    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    private WebView myWebView;
    private PDFView pdfView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_zoom);
        Button btnClose = (Button) findViewById(R.id.btnIvClose);
        pdfView = findViewById(R.id.pdfView);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        pdfView.setVisibility(View.GONE);
        String path = getIntent().getExtras().getString("path");
        myWebView = (WebView) findViewById(R.id.video_view);
        if (path.contains(".doc") || path.contains(".DOC")
                || path.contains(".txt") || path.contains(".TXT")
                || path.contains(".pdf") || path.contains(".PDF")
                || path.contains(".xlsx") || path.contains(".XLSX")
                || path.contains(".pptx") || path.contains(".PPTX")
                || path.contains( ".xls" ) || path.contains( ".XLS" )
                || path.contains( ".ppt" ) || path.contains( ".PPT" )
                || path.contains(".csv") || path.contains(".CSV")) {
            if (!path.contains("http")) {
                File isfile = new File(path);
                if (isfile.exists()) {
                    if (path.contains(".pdf")) {
                        pdfView.setVisibility(View.VISIBLE);
                        String u = "file:///" + isfile.getAbsolutePath();
                        pdfView.fromUri(Uri.parse(u)).load();
                    } else {
                        pdfView.setVisibility(View.GONE);
                        try {
                            Uri uri = Uri.parse(path);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            if (isfile.toString().contains(".doc") || isfile.toString().contains(".docx") ||
                                    isfile.toString().contains(".DOC") || isfile.toString().contains(".DOCX")) {
                                // Word document
                                intent.setDataAndType(uri, "application/msword");
                            } else if (isfile.toString().contains(".ppt") || isfile.toString().contains(".pptx")
                                    || isfile.toString().contains(".PPT") || isfile.toString().contains(".PPTX")) {
                                // Powerpoint file
                                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                            } else if (isfile.toString().contains(".pdf") || isfile.toString().contains(".PDF")) {
                                // Powerpoint file
                                intent.setDataAndType(uri, "application/pdf");
                            } else if (isfile.toString().contains(".xls") || isfile.toString().contains(".xlsx") ||
                                    isfile.toString().contains(".XLS") || isfile.toString().contains(".XLSX")) {
                                // Excel file
                                intent.setDataAndType(uri, "application/vnd.ms-excel");
                            } else {
                                intent.setDataAndType(uri, "*/*");
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(this, "You Don't have proper app to see this", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Utils.downloadByFileManager(ViewVideoWebView.this,path);
                }
                finish();
            }
        } else {
            mWebChromeClient = new MyWebChromeClient();
            myWebView.setWebChromeClient(mWebChromeClient);
            myWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }
            });
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            //myWebView.loadUrl("https://www.youtube.com/watch?v=7bDLIV96LD4");
            myWebView.loadUrl(path);
        }
        //myWebView.loadUrl("https://www.youtube.com/watch?v=7bDLIV96LD4");

    }


    public class MyWebChromeClient extends WebChromeClient {

        FrameLayout.LayoutParams LayoutParameters =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mContentView = (RelativeLayout) findViewById(R.id.activity_main);
            mContentView.setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(ViewVideoWebView.this);
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            } else {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                setContentView(mContentView);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mCustomViewContainer != null)
            mWebChromeClient.onHideCustomView();
        else if (myWebView.canGoBack())
            myWebView.goBack();
        else
            super.onBackPressed();
    }
}