package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CountryCodeItem;
import com.application.wowwao1.Models.CountryCodePojo;
import com.application.wowwao1.Models.PhoneVerificationPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhoneNoActivity extends AppCompatActivity {

    private static final int NEXT_BUTTON_CODE = 100;
    private static final int COUNTRY_LIST_CODE = 101;
    private FloatingActionButton fabNext;
    private Context mContext;
    private Intent intent;
    private Spinner spinnerCountryCode;
    private ArrayAdapter adapterCountryCode;
    private ArrayList<CountryCodeItem> arrCountryCode = new ArrayList<>();

    private ArrayList<String> params;
    private ArrayList<String> values;
    private String url, strCountryCode = "", userId, phone_verified;

    private EditText edtPhoneNo;
    private String strPhoneNo;

    private String strPhoneCode;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no);
        initViews();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_phone_no), PhoneNoActivity.this);

        cd = new ConnectionCheck();


        Log.d("myPhone",PrefsUtil.with(mContext).readString("phone_no"));

        if (phone_verified.equals("y")) {
            PrefsUtil.with(mContext).write("newAccount", false);
            intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPhoneNo.setError(null);
                strPhoneNo = edtPhoneNo.getText().toString().trim();
                if (!strPhoneNo.equalsIgnoreCase("")) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        verifyNumber(userId, strPhoneNo, strPhoneCode);
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, NEXT_BUTTON_CODE);
                    }
                } else {
                    edtPhoneNo.setError(getString(R.string.edit_empty_phone));
                    edtPhoneNo.requestFocus();
                }
                /*intent = new Intent(mContext, VerificationActivity.class);
                startActivity(intent);*/
            }
        });

        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strPhoneCode = arrCountryCode.get(position).getPhoneCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getCountryCode();
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, COUNTRY_LIST_CODE);
        }
    }

    private void initViews() {
        mContext = PhoneNoActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        phone_verified = PrefsUtil.with(mContext).readString("phone_verified");

        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);
        edtPhoneNo.setText(PrefsUtil.with(mContext).readString("phone_no"));
        edtPhoneNo.setEnabled(false);

        fabNext = (FloatingActionButton) findViewById(R.id.fabNext);
        spinnerCountryCode = (Spinner) findViewById(R.id.spinnerCountryCode);
        adapterCountryCode = new ArrayAdapter(mContext, R.layout.adapter_spinner_phone_verify, R.id.txtItem, arrCountryCode);
        spinnerCountryCode.setAdapter(adapterCountryCode);
        adapterCountryCode.notifyDataSetChanged();


    }

    private void getCountryCode() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getCountryCode");

        new ParseJSON(mContext, url, params, values, CountryCodePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CountryCodePojo resultObj = (CountryCodePojo) obj;
                        arrCountryCode.clear();
                        arrCountryCode.addAll(resultObj.getData());
                        adapterCountryCode.notifyDataSetChanged();

                        Log.d("phonecode",PrefsUtil.with(mContext).readString("phone_code"));

                        for(int i=0;i< arrCountryCode.size();i++)
                        {
                            if(arrCountryCode.get(i).getPhoneCode().equals(PrefsUtil.with(mContext).readString("phone_code")))
                            {
                                spinnerCountryCode.setSelection(i);
                                spinnerCountryCode.setEnabled(false);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
        //arrCountryCode.add("US (+1)");
    }

    private void verifyNumber(String userId, final String strPhoneNo, final String strPhoneCode) {
        /*
            action:phoneVerification
            user_id:15
            phone_no:+919033725869
            verify_action:Add
        */
        String noWithCode = strPhoneCode + strPhoneNo;
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("phoneVerification");

        params.add("user_id");
        values.add(userId);

        params.add("phone_no");
        values.add(noWithCode);

        params.add("verify_action");
        values.add("Add");

        new ParseJSON(mContext, url, params, values, PhoneVerificationPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        VerificationActivity.isFromEditProfile = false;
                        PhoneVerificationPojo resultObj = (PhoneVerificationPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        intent = new Intent(mContext, VerificationActivity.class);
                        intent.putExtra("otp_code", resultObj.getData().getCode());
                        intent.putExtra("phoneNo", strPhoneNo);
                        intent.putExtra("countryCode", strPhoneCode);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = (String) obj;
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
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
            if (requestCode == NEXT_BUTTON_CODE) {
                verifyNumber(userId, strPhoneNo, strCountryCode);
            } else if (requestCode == COUNTRY_LIST_CODE) {
                getCountryCode();
            }
        }
    }
}
