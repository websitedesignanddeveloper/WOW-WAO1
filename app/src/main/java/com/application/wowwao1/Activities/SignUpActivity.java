package com.application.wowwao1.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.CountryCodeItem;
import com.application.wowwao1.Models.CountryCodePojo;
import com.application.wowwao1.Models.PhoneVerificationPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    private static final int SIGN_UP_CODE = 100;
    private EditText edtFirstName, edtLastName, edtPhoneNo, edtEmail, edtPassword, edtConfirmPassword,edtVerification;
    private String strFirstName, strLastName, strPhoneNo, strEmail ="_", strPassword, strConfirmPassword, strVerificationCode;
    private Button btnSignUp, btn_sendVerificationCode;
    private Context mContext;
    private String url, device_token = "123";
    private ArrayList<String> params;
    private ArrayList<String> values;



    private Spinner spinnerCountryCode;
    private ArrayAdapter adapterCountryCode;
    private ArrayList<CountryCodeItem> arrCountryCode = new ArrayList<>();
    private String strPhoneCode = "";


    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;
    public String VERIFICATION_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initViews();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_sign_up), SignUpActivity.this);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strFirstName = edtFirstName.getText().toString().trim();
                strLastName = edtLastName.getText().toString().trim();
                strPhoneNo=edtPhoneNo.getText().toString().trim();
                strVerificationCode = edtVerification.getText().toString().trim();
                strEmail = edtEmail.getText().toString().trim();
                strPassword = edtPassword.getText().toString().trim();
                strConfirmPassword = edtConfirmPassword.getText().toString().trim();

                Boolean validationResult = formValidation();
                if (validationResult) {
                    cd = new ConnectionCheck();
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {

                        if(checkVerificationCode(strVerificationCode,VERIFICATION_CODE))
                        {
                            signUpCall(strFirstName, strLastName, strPhoneCode,  strPhoneNo, strEmail, strPassword);
                        }
                        else
                        {
                            Toast.makeText(mContext, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                        }
                        
                        
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, SIGN_UP_CODE);
                    }
                }
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


        btn_sendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strPhoneNo = edtPhoneNo.getText().toString().trim();

                if(strPhoneNo.length() == 0)
                {
                    edtPhoneNo.setError("Phone number cannot be empty");
                    edtPhoneNo.requestFocus();
                }
                else
                {
                    verifyNumber(strPhoneNo,strPhoneCode);
                }
            }
        });


        getCountryCode();

    }

    private void initViews() {
        mContext = SignUpActivity.this;
        device_token = FirebaseInstanceId.getInstance().getToken();
        Log.e("SignUp", "deviceToken : " + device_token);

        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtPhoneNo=(EditText) findViewById(R.id.edtPhoneNo);
        edtVerification = (EditText) findViewById(R.id.edtVerification);

        edtEmail = (EditText) findViewById(R.id.edtEmail);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) findViewById(R.id.inLayoutPass)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));

        edtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        edtConfirmPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) findViewById(R.id.inLayoutConfirmPass)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));

        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btn_sendVerificationCode = (Button) findViewById(R.id.btn_sendVerificationCode);

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
        //arrCountryCode.add("US (+1)");
    }

    private Boolean formValidation() {
        Boolean validationResult = true;
        edtFirstName.setError(null);
        edtLastName.setError(null);
        edtPhoneNo.setError(null);
        edtVerification.setError(null);
        //edtEmail.setError(null);
        edtPassword.setError(null);
        edtConfirmPassword.setError(null);

        if (strFirstName.length() == 0) {
            edtFirstName.setError(getResources().getString(R.string.sign_up_empty_first_name));
            edtFirstName.requestFocus();
            validationResult = false;
        } else if (strLastName.length() == 0) {
            edtLastName.setError(getResources().getString(R.string.sign_up_empty_last_name));
            edtLastName.requestFocus();
            validationResult = false;
        }
        else if (strEmail.length() == 0) {
            edtEmail.setError(getResources().getString(R.string.sign_up_empty_email));
            edtEmail.requestFocus();
            validationResult = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError(getResources().getString(R.string.sign_up_valid_email));
            edtEmail.requestFocus();
            validationResult = false;
        }



        else  if(strPhoneNo.length() != 10){
            edtPhoneNo.setError("Enter 10 digit phone number");
            edtPhoneNo.requestFocus();
            validationResult = false;
        }
        else  if(strVerificationCode.length() ==0){
            edtVerification.setError("Verification code cannot be empty");
            edtVerification.requestFocus();
            validationResult = false;
        }
        /*
        else  if(strPhoneNo.length() != 10){
            edtPhoneNo.setError("Enter 10 digit phone number");
            edtPhoneNo.requestFocus();
            validationResult = false;
        }else if (strEmail.length() == 0) {
            edtEmail.setError(getResources().getString(R.string.sign_up_empty_email));
            edtEmail.requestFocus();
            validationResult = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError(getResources().getString(R.string.sign_up_valid_email));
            edtEmail.requestFocus();
            validationResult = false;
        } */else if (strPassword.length() == 0) {
            edtPassword.setError(getResources().getString(R.string.sign_up_empty_pass));
            edtPassword.requestFocus();
            validationResult = false;
        } else if (strConfirmPassword.length() == 0) {
            edtConfirmPassword.setError(getResources().getString(R.string.sign_up_empty_confirm_pass));
            edtConfirmPassword.requestFocus();
            validationResult = false;
        } else if (!strPassword.equals(strConfirmPassword)) {
            edtConfirmPassword.setError(getResources().getString(R.string.sign_up_pass_miss_matched));
            edtConfirmPassword.requestFocus();
            validationResult = false;
        }
        return validationResult;
    }

    private void signUpCall(String strFirstName, String strLastName, String strPhoneCode, String strPhoneNo, String strEmail, String strPassword) {
        /*
        action:signup
        firstname:abc
        lastname:xyz
        email:abc@gmail.com
        password:123456
        device_token:dfsgfgsdgsfdgs
        device:i
        */
        Log.d("countrycode",strPhoneCode);

        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("signup");

        params.add("firstname");
        values.add(strFirstName);

        params.add("lastname");
        values.add(strLastName);

        params.add("phonecode");
        values.add(strPhoneCode);

        params.add("phonenumber");
        values.add(strPhoneNo);

        params.add("email");
        values.add(strEmail);

        params.add("password");
        values.add(strPassword);

        params.add("device_token");
        values.add(device_token);

        params.add("device");
        values.add("Android");

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = (String) obj;
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
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
            if (requestCode == SIGN_UP_CODE) {
                signUpCall(strFirstName, strLastName, strPhoneCode, strPhoneNo, strEmail, strPassword);
            }
        }
    }

    private void verifyNumber(final String strPhoneNo, final String strPhoneCode) {


        String noWithCode = strPhoneCode + strPhoneNo;
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("phoneVerification");

        /*params.add("user_id");
        values.add(userId);*/

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

                        //Toast.makeText(mContext,resultObj.getData().getCode(),Toast.LENGTH_LONG).show();

                        Log.d("VerificationCode",resultObj.getData().getCode());

                        VERIFICATION_CODE = resultObj.getData().getCode();

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
    
    private boolean checkVerificationCode(String enteredCode, String verificationCode)
    {
        
        boolean res;
        
        if(enteredCode.equals(verificationCode))
        {
            res = true;
        }
        else
        {
            res = false;
        }
        
        return res;
    }


}
