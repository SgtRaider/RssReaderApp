package com.raider.rssapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.raider.rssapp.R;
import com.raider.rssapp.utils.Constants;

public class About extends AppCompatActivity {

    private WebView webViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        webViewer = (WebView) findViewById(R.id.webViewerAbout);
        webViewer.getSettings().setJavaScriptEnabled(true);

        webViewer.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(About.this, description, Toast.LENGTH_SHORT).show();
            }
        });
        webViewer.clearCache(true);
        webViewer.loadUrl(Constants.urlAbout);
    }


}
