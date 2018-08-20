package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NoConnectionActivity extends AppCompatActivity {

    private TextView txtReload;
    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Context mContext;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_connection);
        initViews();

        txtReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cd = new ConnectionCheck();
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initViews() {
        mContext = NoConnectionActivity.this;
        txtReload = (TextView) findViewById(R.id.txtReload);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
