package com.isl.home.module;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import infozech.itower.R;

public class PolicyFragement extends Fragment {
     WebView myWebView;
     ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_policy,container, false);
        myWebView = view.findViewById(R.id.myWebView);
        progressBar = view.findViewById(R.id.progress);
        openUrl();
        return view;
    }

    public void openUrl()
    {
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE); // Show progress bar
                }
                progressBar.setProgress(newProgress); // Update progress bar
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar when done
                }
            }
        });
        // Load a URL
        myWebView.loadUrl("https://docs.google.com/document/d/1cUZl6BshTSpNt9lIpgPjaSyTk2PCbvUV/edit?usp=sharing&ouid=103202455276511804899&rtpof=true&sd=true");

    }
}
