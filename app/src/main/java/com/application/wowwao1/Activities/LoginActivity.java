package com.application.wowwao1.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.LoginPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    private static final int LOGIN_CODE = 100;
    private static final int FORGOT_PASS_CODE = 101;
    private TextView txtCreateAcc, txtForgotPass;
    private EditText edtEmail, edtPassword;
    private String strEmail, strPassword, device_token = "123";
    private Button btnLogin;
    private Context mContext;
    private Intent intent;

    private String url, email_verified = "", phone_verified = "";
    private ArrayList<String> params;
    private ArrayList<String> values;

    /*forgot pass dialog variable*/
    private Dialog dialog;
    private EditText edtForgotPassEmail;
    private Button btnSubmit;
    private String strForgotPassEmail;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;

    private Locale myLocale;
    private String  currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);



        initViews();
        cd = new ConnectionCheck();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_login), LoginActivity.this);
        email_verified = PrefsUtil.with(mContext).readString("email_verified");
        phone_verified = PrefsUtil.with(mContext).readString("phone_verified");
        if (PrefsUtil.with(mContext).readBoolean("loggedIn")) {
            /*if (email_verified.equals("y")) {
                intent = new Intent(mContext, PhoneNoActivity.class);
                startActivity(intent);
                finish();
            } else if (email_verified.equals("y") && phone_verified.equals("y")) {
                intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }*/

            if (phone_verified.equals("n")) {
                intent = new Intent(mContext, PhoneNoActivity.class);
                startActivity(intent);
                finish();
            } else if (phone_verified.equals("y")) {
                intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
                finish();
            }



        }

        txtCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(mContext, SignUpActivity.class);
                startActivity(intent);
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForgotPassword();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strEmail = edtEmail.getText().toString().trim();
                strPassword = edtPassword.getText().toString().trim();
                try {
                    device_token = FirebaseInstanceId.getInstance().getToken();
                } catch (NullPointerException e) {
                    device_token = "123";
                    e.printStackTrace();
                }
                Log.e("Login", "deviceToken : " + device_token);
                Boolean validationResult = formValidation();
                if (validationResult) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        loginCall(strEmail, strPassword, device_token, "Android");
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, LOGIN_CODE);
                    }

                }
            }
        });
    }

    private void initViews() {
        mContext = LoginActivity.this;
        try {
            device_token = FirebaseInstanceId.getInstance().getToken();
        } catch (NullPointerException e) {
            device_token = "123";
            e.printStackTrace();
        }
        Log.e("Login", "deviceToken : " + device_token);

        txtCreateAcc = (TextView) findViewById(R.id.txtCreateAcc);
        txtForgotPass = (TextView) findViewById(R.id.txtForgotPass);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) findViewById(R.id.inLayoutPassword)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf"));

        btnLogin = (Button) findViewById(R.id.btnLogin);

        dialog = new Dialog(mContext);
        try {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.setContentView(R.layout.dialog_forgot_password);
        edtForgotPassEmail = (EditText) dialog.findViewById(R.id.edtForgotPassEmail);
        btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
    }

    private Boolean formValidation() {
        Boolean validationResult = true;
        edtEmail.setError(null);
        edtPassword.setError(null);
        if (strEmail.length() == 0) {
            edtEmail.setError(getResources().getString(R.string.login_empty_email));
            edtEmail.requestFocus();
            validationResult = false;
        } else if (strPassword.length() == 0) {
            edtPassword.setError(getResources().getString(R.string.login_empty_password));
            edtPassword.requestFocus();
            validationResult = false;
        }
        return validationResult;
    }

    // login service call
    private void loginCall(final String strEmail, final String strPassword, String device_token, String device) {
        /*  action:login
            email:abc@gmail.com
            password:123456
            device_token:dfsgfgsdgsfdgs
            device:a
        */
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("login");

        params.add("email");
        values.add(strEmail);

        params.add("password");
        values.add(strPassword);

        params.add("device_token");
        values.add(device_token);

        params.add("device");
        values.add(device);

        new ParseJSON(mContext, url, params, values, LoginPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        LoginPojo resultObj = (LoginPojo) obj;
                        PrefsUtil.with(mContext).write("loggedIn", true);
                        email_verified = resultObj.getData().getEmailVerified();
                        PrefsUtil.with(mContext).write("email_verified", email_verified);
                        phone_verified = resultObj.getData().getPhoneVerified();
                        PrefsUtil.with(mContext).write("phone_verified", phone_verified);
                        PrefsUtil.with(mContext).write("uname", strEmail);
                        PrefsUtil.with(mContext).write("password", strPassword);


                        /*
                        if (email_verified.equals("n")) {
                            edtEmail.setText("");
                            edtPassword.setText("");
                            intent = new Intent(mContext, ConfirmEmailActivity.class);
                            intent.putExtra("email", resultObj.getData().getEmail());
                            intent.putExtra("userId", resultObj.getData().getId());
                            startActivity(intent);
                        } else if (email_verified.equals("y")) {*/




                            PrefsUtil.with(mContext).write("userId", resultObj.getData().getId());
                            PrefsUtil.with(mContext).write("first_name", resultObj.getData().getFirstName());
                            PrefsUtil.with(mContext).write("last_name", resultObj.getData().getLastName());
                            PrefsUtil.with(mContext).write("email", resultObj.getData().getEmail());
                            PrefsUtil.with(mContext).write("phone_no", resultObj.getData().getPhoneNo());
                            PrefsUtil.with(mContext).write("gender", resultObj.getData().getGender());
                            PrefsUtil.with(mContext).write("profile_img", resultObj.getData().getProfileImg());
                            PrefsUtil.with(mContext).write("cover_img", resultObj.getData().getCoverImg());
                            PrefsUtil.with(mContext).write("ad_id", "0");
                            PrefsUtil.with(mContext).write("countryID", resultObj.getData().getCountryID());
                            PrefsUtil.with(mContext).write("stateID", resultObj.getData().getStateID());
                            PrefsUtil.with(mContext).write("cityID", resultObj.getData().getCityID());
                            PrefsUtil.with(mContext).write("dob", resultObj.getData().getDob());
                            PrefsUtil.with(mContext).write("phone_code", resultObj.getData().getPhone_code());
                            PrefsUtil.with(mContext).write("latitude", resultObj.getData().getCity_lat());
                            PrefsUtil.with(mContext).write("longitude", resultObj.getData().getCity_long());
                            intent = new Intent(mContext, PhoneNoActivity.class);
                            startActivity(intent);
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

    private void dialogForgotPassword() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strForgotPassEmail = edtForgotPassEmail.getText().toString().trim();
                edtForgotPassEmail.setError(null);

                boolean validation = forgotFormValidation();
                if (validation) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        forgotPasswordServiceCall();
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, FORGOT_PASS_CODE);
                    }
                }

            }

            private boolean forgotFormValidation() {
                boolean validationResult = true;
                if (strForgotPassEmail.length() == 0) {
                    edtForgotPassEmail.setError(getString(R.string.forgot_email_cannot_be_empty));
                    edtForgotPassEmail.requestFocus();
                    validationResult = false;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(strForgotPassEmail).matches()) {
                    edtForgotPassEmail.setError(getString(R.string.forgot_email_valid_email));
                    edtForgotPassEmail.requestFocus();
                    validationResult = false;
                }
                return validationResult;
            }


        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void forgotPasswordServiceCall() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userForgotPassword");

        params.add("email");
        values.add(strForgotPassEmail);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    dialog.dismiss();
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
            if (requestCode == LOGIN_CODE) {
                loginCall(strEmail, strPassword, device_token, "Android");
            } else if (requestCode == FORGOT_PASS_CODE) {
                forgotPasswordServiceCall();
            } /*else if (requestCode == EDIT_PROFILE_CODE) {

            }*/
        }
    }




}
