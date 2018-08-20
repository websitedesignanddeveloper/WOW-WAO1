package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConfirmEmailActivity extends AppCompatActivity {

    private static final int RESEND_EMAIL_CODE = 100;
    private TextView txtResend;
    private Context mContext;

    private String url, userId, email, email_verified;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private Intent intent;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        intent = getIntent();
        initViews();

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = intent.getStringExtra("userId");
                email = intent.getStringExtra("email");
                Log.e("ConfirmEmailActivity", "userId:" + userId);
                Log.e("ConfirmEmailActivity", "email:" + email);
                cd = new ConnectionCheck();
                isInternetAvailable = cd.isNetworkConnected(mContext);
                if (isInternetAvailable) {
                    resendEmail(email);
                } else {
                    intent = new Intent(mContext, NoConnectionActivity.class);
                    startActivityForResult(intent, RESEND_EMAIL_CODE);
                }
            }
        });
    }

    private void initViews() {
        mContext = ConfirmEmailActivity.this;
        email_verified = PrefsUtil.with(mContext).readString("email_verified");

        txtResend = (TextView) findViewById(R.id.txtResend);
    }

    // resend email for verification
    private void resendEmail(String email) {
        /*action:verifyEmailResend
          user_id:15
          email:zameer.bordiwala@ncrypted.com*/

        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("verifyEmailResend");

        params.add("user_id");
        values.add(userId);

        params.add("email");
        values.add(email);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESEND_EMAIL_CODE) {
                resendEmail(email);
            }
        }
    }
}
