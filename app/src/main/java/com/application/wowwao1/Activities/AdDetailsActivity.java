package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.application.wowwao1.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AdDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title_text;
    private WebView webViewAds;
    private Intent intent;
    private String name, imageLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);
        initViews();
        intent = getIntent();
        name = intent.getStringExtra("name");
        imageLink = intent.getStringExtra("link");
        Log.e("AdDetailsActivity", "name : " + name);
        Log.e("AdDetailsActivity", "imageLink : " + imageLink);

        title_text.setText(name);
        webViewAds.setWebViewClient(new MyBrowser());
        webViewAds.getSettings().setJavaScriptEnabled(true);
        webViewAds.loadUrl(imageLink);
        webViewAds.setHorizontalScrollBarEnabled(false);
    }

    private void initViews() {
        setupToolbar();
        webViewAds = (WebView) findViewById(R.id.webViewAds);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
