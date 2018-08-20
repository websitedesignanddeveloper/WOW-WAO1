package com.application.wowwao1.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.PhoneVerificationPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VerificationActivity extends AppCompatActivity {

    private static final int VERIFICATION_CODE = 100;
    public static boolean isFromEditProfile = false;
    private Context mContext;
    private TextView txtNumber, txtResend;
    private EditText edtCode1, edtCode2, edtCode3, edtCode4, edtCode5, edtCode6;
    private String strCode1, strCode2, strCode3, strCode4, strCode5, strCode6, otp_code = "", phoneNo;
    private Intent intent;

    private BroadcastReceiver receiver;

    private ArrayList<String> params;
    private ArrayList<String> values;
    private String url, strCountryCode = "", userId, phone_verified, phone_code;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        initViews();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_verification), VerificationActivity.this);
        cd = new ConnectionCheck();

        intent = getIntent();
        otp_code = intent.getStringExtra("otp_code");
        phoneNo = intent.getStringExtra("phoneNo");
        phone_code = intent.getStringExtra("countryCode");
        Log.e("VerificationActivity", "otp_code : " + otp_code);
        Log.e("VerificationActivity", "phoneNo : " + phoneNo);
        Log.e("VerificationActivity", "phone_code : " + phone_code);
        txtNumber.setText("to " + phone_code + "" + phoneNo);

        if (!isFromEditProfile) {
            if (phone_verified.equals("y")) {
                intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

        /*String[] result = otp_code.split("");
        for (int j = 0; j <= result.length; j++) {
            if (j == 0) {
                edtCode1.setText(result[1]);
            } else if (j == 1) {
                edtCode2.setText(result[2]);
            } else if (j == 2) {
                edtCode3.setText(result[3]);
            } else if (j == 3) {
                edtCode4.setText(result[4]);
            } else if (j == 4) {
                edtCode5.setText(result[5]);
            } else if (j == 5) {
                edtCode6.setText(result[6]);
                completeVerification(userId, phoneNo, phone_code);
            }
        }*/
        edtCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strCode1 = s.toString();
                if (strCode1.length() == 1) {
                    edtCode2.requestFocus();
                }
            }
        });

        edtCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strCode2 = s.toString();
                if (strCode2.length() == 1) {
                    edtCode3.requestFocus();
                }
            }
        });

        edtCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strCode3 = s.toString();
                if (strCode3.length() == 1) {
                    edtCode4.requestFocus();
                }
            }
        });

        edtCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strCode4 = s.toString();
                if (strCode4.length() == 1) {
                    edtCode5.requestFocus();
                }
            }
        });

        edtCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                strCode5 = s.toString();
                if (strCode5.length() == 1) {
                    edtCode6.requestFocus();
                }
            }
        });

        edtCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String finalResult = s.toString();
                if (finalResult.length() != 0) {
                    strCode1 = edtCode1.getText().toString().trim();
                    strCode2 = edtCode2.getText().toString().trim();
                    strCode3 = edtCode3.getText().toString().trim();
                    strCode4 = edtCode4.getText().toString().trim();
                    strCode5 = edtCode5.getText().toString().trim();
                    strCode6 = edtCode6.getText().toString().trim();
                    boolean validation = formValidation();
                    if (validation) {
                        finalResult = strCode1 + strCode2 + strCode3 + strCode4 + strCode5 + strCode6;
                        if (finalResult.equals(otp_code)) {
                            isInternetAvailable = cd.isNetworkConnected(mContext);
                            if (isInternetAvailable) {
                                completeVerification(userId, phoneNo, phone_code);
                            } else {
                                intent = new Intent(mContext, NoConnectionActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent, VERIFICATION_CODE);
                            }
                        } else {
                            Toast.makeText(mContext, R.string.please_type_correct_code, Toast.LENGTH_LONG).show();
                            edtCode1.setText("");
                            edtCode2.setText("");
                            edtCode3.setText("");
                            edtCode4.setText("");
                            edtCode5.setText("");
                            edtCode6.setText("");
                        }
                    }
                }
            }
        });

       /* receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    String message = intent.getStringExtra("message");
                    //Do whatever you want with the code here
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
        };*/

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFromEditProfile) {
                    verifyNumber(userId, phoneNo, phone_code, "Edit");
                } else {
                    verifyNumber(userId, phoneNo, phone_code, "Add");
                }
            }
        });
    }

    private boolean formValidation() {
        boolean validation = true;
        if (strCode1.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        } else if (strCode2.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        } else if (strCode3.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        } else if (strCode4.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        } else if (strCode5.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        } else if (strCode6.length() == 0) {
            Toast.makeText(mContext, R.string.code_cannot_be_empty, Toast.LENGTH_LONG).show();
            validation = false;
        }
        return validation;
    }

    private void initViews() {
        mContext = VerificationActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        phone_verified = PrefsUtil.with(mContext).readString("phone_verified");
        txtNumber = (TextView) findViewById(R.id.txtNumber);
        txtResend = (TextView) findViewById(R.id.txtResend);

        edtCode1 = (EditText) findViewById(R.id.edtCode1);
        edtCode2 = (EditText) findViewById(R.id.edtCode2);
        edtCode3 = (EditText) findViewById(R.id.edtCode3);
        edtCode4 = (EditText) findViewById(R.id.edtCode4);
        edtCode5 = (EditText) findViewById(R.id.edtCode5);
        edtCode6 = (EditText) findViewById(R.id.edtCode6);
    }

    private void completeVerification(String userId, final String phoneNo, final String phone_code) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("phoneVerification");

        params.add("user_id");
        values.add(userId);

        params.add("phone_no");
        values.add(phoneNo);

        params.add("phone_code");
        values.add(phone_code);

        params.add("verify_action");
        values.add("Update");

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        PrefsUtil.with(mContext).write("phone_verified", "y");
                        PrefsUtil.with(mContext).write("phone_code", phone_code);
                        PrefsUtil.with(mContext).write("phone_no", phoneNo);
                        if (isFromEditProfile) {
                            intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            PrefsUtil.with(mContext).write("newAccount", true);
                            intent = new Intent(mContext, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            ((PhoneNoActivity) getApplicationContext()).finish();
                        }
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


    /*@Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == VERIFICATION_CODE) {
                completeVerification(userId, phoneNo, phone_code);
            }
        }
    }

    private void verifyNumber(String userId, final String strPhoneNo, final String strCountryCode, String verify_action) {
        /*
            action:phoneVerification
            user_id:15
            phone_no:+919033725869
            verify_action:Add
        */
        String noWithCode = strCountryCode + strPhoneNo;
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
        values.add(verify_action);

        new ParseJSON(mContext, url, params, values, PhoneVerificationPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        VerificationActivity.isFromEditProfile = false;
                        PhoneVerificationPojo resultObj = (PhoneVerificationPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        otp_code = resultObj.getData().getCode();
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
}
