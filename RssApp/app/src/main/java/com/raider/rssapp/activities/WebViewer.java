package com.raider.rssapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.raider.rssapp.R;

public class WebViewer extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewer);

        WebView webView = (WebView) this.findViewById(R.id.urlViewer);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(WebViewer.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        webView.loadUrl(( this.getIntent().getExtras()).getString("url"));
    }
}
