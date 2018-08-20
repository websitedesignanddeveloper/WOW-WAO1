package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.InfoDetailsPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InfoDetailsActivity extends AppCompatActivity {

    private static final int INFO_DETAILS_CODE = 100;
    private Toolbar toolbar;
    private TextView title_text, txtDescription;
    private Context mContext;
    private String url, name, st_id;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private Intent intent;
    private WebView webViewInfo;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    String mimeType = "text/html";
    String encoding = "UTF-8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_details);
        initViews();
        intent = getIntent();
        st_id = intent.getStringExtra("st_id");
        name = intent.getStringExtra("name");

        Log.e("InfoDetails", "st_id : " + st_id);
        Log.e("InfoDetails", "name : " + name);
        title_text.setText(name);

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getInfoDetails(st_id);
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, INFO_DETAILS_CODE);
        }
    }

    private void initViews() {
        mContext = InfoDetailsActivity.this;
        setupToolbar();
        //txtDescription = (TextView) findViewById(R.id.txtDescription);
        webViewInfo = (WebView) findViewById(R.id.webViewInfo);
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

    private void getInfoDetails(String st_id) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getSingleStaticPage");

        params.add("st_id");
        values.add(st_id);

        new ParseJSON(mContext, url, params, values, InfoDetailsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        InfoDetailsPojo resultObj = (InfoDetailsPojo) obj;
                        //txtDescription.setText(Html.fromHtml(resultObj.getData().getDescription()));
                        webViewInfo.loadDataWithBaseURL("", resultObj.getData().getDescription(), mimeType, encoding, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == INFO_DETAILS_CODE) {
                getInfoDetails(st_id);
            }
        }
    }
}
