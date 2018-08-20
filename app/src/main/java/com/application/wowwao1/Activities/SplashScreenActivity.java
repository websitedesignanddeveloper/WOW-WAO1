package com.application.wowwao1.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.LoginPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 5000;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 201;
    public static final int MY_PERMISSIONS_REQUEST_ALL_PERMISSION = 200;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 202;
    public static final int MY_PERMISSIONS_REQUEST_SMS = 203;

    private Activity activity;

    private String url, email_verified = "", phone_verified = "", uname, password, device_token;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private Intent intent;

    SharedPreferences myPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        activity = this;

        try {
            device_token = FirebaseInstanceId.getInstance().getToken();
        } catch (NullPointerException e) {
            device_token = "123";
            e.printStackTrace();
        }



        myPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = myPref.edit();

        if(myPref.contains("country"))
        {
            if(myPref.getString("lang",null).equals("en"))
            {

                setLanguageForApp("en","US");
            }
            else
            {
                setLanguageForApp("zh","CN");

            }
        }



        uname = PrefsUtil.with(activity).readString("uname");
        password = PrefsUtil.with(activity).readString("password");

        Log.e("SplashScreen", "uname : " + uname);
        Log.e("SplashScreen", "password : " + password);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkOS()) {

                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_SMS},
                                MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                    } else {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

                            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                            } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_SMS);
                            }
                        } else {

                            if (!uname.equals("") && !password.equals("")) {
                                loginCall(uname, password, device_token, "Android");
                            } else {
                                PrefsUtil.with(activity).write("loggedIn", false);
                                Intent intent = new Intent(activity, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }

                } else {

                    if (!uname.equals("") && !password.equals("")) {
                        loginCall(uname, password, device_token, "Android");
                    } else {
                        PrefsUtil.with(activity).write("loggedIn", false);
                        Intent intent = new Intent(activity, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }
        }, SPLASH_TIME_OUT);
    }

    private boolean checkOS() {

        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ALL_PERMISSION: {
                // If request is cancelled, the result arrays are empty.

                Log.e("SplashScreen", "Permissions :" + grantResults.length);
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean smsAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;


                if (!storageAccepted || !cameraAccepted || !smsAccepted) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SplashTheme);
                    builder.setMessage(R.string.permission_alert_msg);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    PrefsUtil.with(activity).write("loggedIn", false);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            break;

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                Log.e("SplashScreen", "Permissions :" + grantResults.length);
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;


                if (!storageAccepted) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SplashTheme);
                    builder.setMessage(R.string.permission_alert_msg);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    PrefsUtil.with(activity).write("loggedIn", false);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            break;

            case MY_PERMISSIONS_REQUEST_CAMERA: {

                Log.e("SplashScreen", "Permissions :" + grantResults.length);
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (!cameraAccepted) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SplashTheme);
                    builder.setMessage(R.string.permission_alert_msg);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    PrefsUtil.with(activity).write("loggedIn", false);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            break;

            case MY_PERMISSIONS_REQUEST_SMS: {

                Log.e("SplashScreen", "Permissions :" + grantResults.length);
                boolean smsAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (!smsAccepted) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.SplashTheme);
                    builder.setMessage(R.string.permission_alert_msg);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    PrefsUtil.with(activity).write("loggedIn", false);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            break;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // login service call
    private void loginCall(String strEmail, String strPassword, String device_token, String device) {
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

        new ParseJSON(activity, url, params, values, LoginPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        LoginPojo resultObj = (LoginPojo) obj;
                        PrefsUtil.with(activity).write("loggedIn", true);
                        email_verified = resultObj.getData().getEmailVerified();
                        PrefsUtil.with(activity).write("email_verified", email_verified);
                        phone_verified = resultObj.getData().getPhoneVerified();
                        PrefsUtil.with(activity).write("phone_verified", phone_verified);



                    /*





                       if (email_verified.equals("n")) {
                            intent = new Intent(activity, ConfirmEmailActivity.class);
                            intent.putExtra("email", resultObj.getData().getEmail());
                            intent.putExtra("userId", resultObj.getData().getId());
                            startActivity(intent);
                        } else if (email_verified.equals("y")) {*/

                            PrefsUtil.with(activity).write("userId", resultObj.getData().getId());
                            PrefsUtil.with(activity).write("first_name", resultObj.getData().getFirstName());
                            PrefsUtil.with(activity).write("last_name", resultObj.getData().getLastName());
                            PrefsUtil.with(activity).write("email", resultObj.getData().getEmail());
                            PrefsUtil.with(activity).write("phone_no", resultObj.getData().getPhoneNo());
                            PrefsUtil.with(activity).write("gender", resultObj.getData().getGender());
                            PrefsUtil.with(activity).write("profile_img", resultObj.getData().getProfileImg());
                            PrefsUtil.with(activity).write("cover_img", resultObj.getData().getCoverImg());
                            PrefsUtil.with(activity).write("ad_id", "0");
                            PrefsUtil.with(activity).write("countryID", resultObj.getData().getCountryID());
                            PrefsUtil.with(activity).write("stateID", resultObj.getData().getStateID());
                            PrefsUtil.with(activity).write("cityID", resultObj.getData().getCityID());
                            PrefsUtil.with(activity).write("dob", resultObj.getData().getDob());
                            PrefsUtil.with(activity).write("phone_code", resultObj.getData().getPhone_code());


                        Log.d("userId", resultObj.getData().getId());
                            Log.d("User_Phone",resultObj.getData().getPhoneNo());

                            intent = new Intent(activity, PhoneNoActivity.class);
                            startActivity(intent);
                            finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    PrefsUtil.with(activity).write("loggedIn", false);
                    PrefsUtil.with(activity).clearPrefs();
                    intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void setLanguageForApp(String lang, String country){
        Locale locale;
        if(lang.equals("en")){
            //locale = Locale.getDefault();

            locale = new Locale("en", "US");
        }
        else {
            locale = new Locale("zh", "CN");


        }

        editor.putString("lang",lang);
        editor.putString("country",country);
        editor.commit();

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());


//        Intent refresh = new Intent(getActivity(), HomeActivity.class);
//        startActivity(refresh);
//        getActivity().finish();


    }
}
