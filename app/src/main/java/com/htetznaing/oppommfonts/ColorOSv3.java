package com.htetznaing.oppommfonts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ColorOSv3 extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_osv3);

        webView = findViewById(R.id.webview);
        webView.loadUrl("file:///android_asset/v3.html");
    }
}
