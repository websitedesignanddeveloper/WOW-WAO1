package com.application.wowwao1.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.AsyncTask.ImageUploadParseJSON;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CityItem;
import com.application.wowwao1.Models.CityPojo;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.Models.CountryItem;
import com.application.wowwao1.Models.CountryPojo;
import com.application.wowwao1.Models.EditCountryCodeItem;
import com.application.wowwao1.Models.EditCountryCodePojo;
import com.application.wowwao1.Models.PhoneVerificationPojo;
import com.application.wowwao1.Models.StateItem;
import com.application.wowwao1.Models.StatePojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity {

    private static final int EDIT_PROFILE_DETAILS_CODE = 100;
    private static final int UPDATE_PROFILE_DETAILS_CODE = 101;
    private static final int PHONE_VERIFICATION_CODE = 102;
    private Toolbar toolbar;
    private TextView title_text, txtDob;
    private Context mContext;
    private EditText edtFirstName, edtLastName, edtPhoneNo;
    private String strFirstName, strLastName, strPhoneNo, countryCode, countryID, stateID, cityID, strDob, strGender, profileImg, city_lat, city_long, newPhoneNo;
    private RadioGroup rgGender;
    private RadioButton radioMale, radioFemale;
    private Spinner spinnerCountryCode, spinnerCity, spinnerState, spinnerCountry;
    private Button btnSave, btnCancel;
    private ImageView imgMale, imgFemale;
    //private CircleImageView imgProfile;

    private ArrayList<CountryItem> arrCountry = new ArrayList<>();
    private ArrayList<StateItem> arrState = new ArrayList<>();
    private ArrayList<CityItem> arrCity = new ArrayList<>();
    private ArrayList<EditCountryCodeItem> arrCountryCode = new ArrayList<>();
    ArrayAdapter adapterCountry, adapterState, adapterCity, adapterCountryCode;
    private Intent intent;

    /*Image picker variable*/
    private Uri mCropImageUri;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;
    private ArrayList<String> imgParams;
    private ArrayList<File> imgValues;

    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private boolean firsTime = true;
    private int aspectRatioX = 1, aspectRatioY = 1;
    private boolean isVerifyNumber = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        intent = getIntent();

        initViews();
        new KeyboardUtils().setupUI(findViewById(R.id.activity_edit_profile), EditProfileActivity.this);
        cd = new ConnectionCheck();


        editProfileDetails();

        spinnerCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryCode = arrCountryCode.get(position).getPhoneCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                countryID = arrCountry.get(position).getId();
                Log.e("EditProfile", "countryID : " + countryID);
                if (!countryID.equals("0")) {
                    getStateData(countryID);
                } else {
                    arrState.clear();
                    arrState.add(new StateItem("0", getResources().getString(R.string.edit_state)));
                    spinnerState.setAdapter(adapterState);
                    adapterState.notifyDataSetChanged();

                    arrCity.clear();
                    arrCity.add(new CityItem("0", getResources().getString(R.string.edit_city)));
                    spinnerCity.setAdapter(adapterCity);
                    adapterCity.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateID = arrState.get(position).getId();
                Log.e("EditProfile", "stateID : " + stateID);
                if (!stateID.equals("0")) {
                    getCityData(stateID);
                } else {
                    arrCity.clear();
                    arrCity.add(new CityItem("0", getResources().getString(R.string.edit_city)));
                    spinnerCity.setAdapter(adapterCity);
                    adapterCity.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityID = arrCity.get(position).getId();
                city_lat = arrCity.get(position).getLat();
                city_long = arrCity.get(position).getJsonMemberLong();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
                imgMale.setImageResource(R.drawable.rd_male_blue);
                imgFemale.setImageResource(R.drawable.rd_female_gray);
                strGender = radioMale.getText().toString().trim();
            }
        });

        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
                imgMale.setImageResource(R.drawable.rd_male_gray);
                imgFemale.setImageResource(R.drawable.rd_female_blue);
                strGender = radioFemale.getText().toString().trim();
            }
        });

        /*imgEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CropImage.isExplicitCameraPermissionRequired(mContext)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    CropImage.startPickImageActivity(EditProfileActivity.this);
                }
            }
        });*/

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFirstName = edtFirstName.getText().toString().trim();
                strLastName = edtLastName.getText().toString().trim();
                newPhoneNo = edtPhoneNo.getText().toString().trim();

                if (radioMale.isChecked()) {
                    strGender = radioMale.getText().toString().trim();
                } else if (radioFemale.isChecked()) {
                    strGender = radioFemale.getText().toString().trim();
                }

                Boolean validationResult = formValidation();
                if (validationResult) {
                    isInternetAvailable = cd.isNetworkConnected(mContext);
                    if (isInternetAvailable) {
                        if (!newPhoneNo.equals(strPhoneNo)) {
                            isVerifyNumber = false;
                            verifyNumber(userId, newPhoneNo, countryCode);
                            updateMyProfile(strFirstName, strLastName, strGender, strDob, countryCode, strPhoneNo, countryID, stateID, cityID, city_lat, city_long);
                        } else {
                            isVerifyNumber = true;
                            updateMyProfile(strFirstName, strLastName, strGender, strDob, countryCode, strPhoneNo, countryID, stateID, cityID, city_lat, city_long);
                        }
                    } else {
                        intent = new Intent(mContext, NoConnectionActivity.class);
                        startActivityForResult(intent, UPDATE_PROFILE_DETAILS_CODE);
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefsUtil.with(mContext).readBoolean("newAccount")) {
                    intent = new Intent(mContext, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    finish();
                }
            }
        });

        // for calender
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd-MM-yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                strDob = sdf.format(myCalendar.getTime());
                txtDob.setText(strDob);
            }
        };

        txtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getCountryCode();
            getCountryData();
        } else {
            intent = new Intent(mContext, NoConnectionActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_DETAILS_CODE);
        }
    }


    private void initViews() {
        mContext = EditProfileActivity.this;
        userId = PrefsUtil.with(mContext).readString("userId");
        setupToolbar();
        txtDob = (TextView) findViewById(R.id.txtDob);

        edtFirstName = (EditText) findViewById(R.id.edtFirstName);
        edtLastName = (EditText) findViewById(R.id.edtLastName);
        edtPhoneNo = (EditText) findViewById(R.id.edtPhoneNo);

        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);

        spinnerCountryCode = (Spinner) findViewById(R.id.spinnerCountryCode);
        adapterCountryCode = new ArrayAdapter(mContext, R.layout.adapter_spinner, R.id.txtItem, arrCountryCode);
        spinnerCountryCode.setAdapter(adapterCountryCode);
        adapterCountryCode.notifyDataSetChanged();

        spinnerCountry = (Spinner) findViewById(R.id.spinnerCountry);
        arrCountry.add(new CountryItem("0", getResources().getString(R.string.edit_country)));
        adapterCountry = new ArrayAdapter(mContext, R.layout.adapter_spinner, R.id.txtItem, arrCountry);
        spinnerCountry.setAdapter(adapterCountry);
        adapterCountry.notifyDataSetChanged();

        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        arrState.add(new StateItem("0", getResources().getString(R.string.edit_state)));
        adapterState = new ArrayAdapter(mContext, R.layout.adapter_spinner, R.id.txtItem, arrState);
        spinnerState.setAdapter(adapterState);
        adapterState.notifyDataSetChanged();

        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        arrCity.add(new CityItem("0", getResources().getString(R.string.edit_city)));
        adapterCity = new ArrayAdapter(mContext, R.layout.adapter_spinner, R.id.txtItem, arrCity);
        spinnerCity.setAdapter(adapterCity);
        adapterCity.notifyDataSetChanged();

        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        imgMale = (ImageView) findViewById(R.id.imgMale);
        imgFemale = (ImageView) findViewById(R.id.imgFemale);

        //imgProfile = (CircleImageView) findViewById(R.id.imgProfile);

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title_text = (TextView) findViewById(R.id.title_text);
        setSupportActionBar(toolbar);
        title_text.setText(R.string.edit_profile_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(false);
                builder.setMessage(R.string.descard_changes);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        intent = new Intent(mContext, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                //finish();
            }
        });
    }

    private Boolean formValidation() {
        Boolean validationResult = true;
        edtFirstName.setError(null);
        edtLastName.setError(null);
        edtFirstName.setError(null);

        if (strFirstName.length() == 0) {
            edtFirstName.setError(getResources().getString(R.string.edit_empty_fname));
            edtFirstName.requestFocus();
            validationResult = false;
        } else if (strLastName.length() == 0) {
            edtLastName.setError(getResources().getString(R.string.edit_empty_lname));
            edtLastName.requestFocus();
            validationResult = false;
        } else if (strDob.length() == 0) {
            Toast.makeText(mContext, getString(R.string.edit_dob), Toast.LENGTH_LONG).show();
            validationResult = false;
        } else if (countryCode.equals("0")) {
            Toast.makeText(mContext, getString(R.string.edt_country_code), Toast.LENGTH_LONG).show();
            validationResult = false;
        } else if (newPhoneNo.length() == 0) {
            edtPhoneNo.setError(getResources().getString(R.string.edit_empty_phone));
            edtPhoneNo.requestFocus();
            validationResult = false;
        } else if (countryID.equals("0")) {
            Toast.makeText(mContext, getString(R.string.edit_country), Toast.LENGTH_LONG).show();
            validationResult = false;
        } else if (stateID.equals("0")) {
            Toast.makeText(mContext, getString(R.string.edit_state), Toast.LENGTH_LONG).show();
            validationResult = false;
        } else if (cityID.equals("0")) {
            Toast.makeText(mContext, getString(R.string.edit_city), Toast.LENGTH_LONG).show();
            validationResult = false;
        }
        return validationResult;
    }

    private void updateMyProfile(final String strFirstName, final String strLastName, final String strGender, final String strDob,
                                 final String countryCode, final String strPhoneNo, final String countryID, final String stateID, final String cityID, final String city_lat, final String city_long) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userEditProfile");

        params.add("user_id");
        values.add(userId);

        params.add("firstname");
        values.add(strFirstName);

        params.add("lastname");
        values.add(strLastName);

        params.add("gender");
        values.add(strGender);

        params.add("dob");
        values.add(strDob);

        params.add("country");
        values.add(countryID);

        params.add("state");
        values.add(stateID);

        params.add("city");
        values.add(cityID);

        params.add("city_lat");
        values.add(city_lat);

        params.add("city_long");
        values.add(city_long);

        new ParseJSON(mContext, url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        PrefsUtil.with(mContext).write("first_name", strFirstName);
                        PrefsUtil.with(mContext).write("last_name", strLastName);
                        PrefsUtil.with(mContext).write("gender", strGender);
                        PrefsUtil.with(mContext).write("dob", strDob);
                        //PrefsUtil.with(mContext).write("countryCode",countryCode);
                        //PrefsUtil.with(mContext).write("phone_no",strPhoneNo);
                        PrefsUtil.with(mContext).write("countryID", countryID);
                        PrefsUtil.with(mContext).write("stateID", stateID);
                        PrefsUtil.with(mContext).write("cityID", cityID);
                        PrefsUtil.with(mContext).write("latitude", city_lat);
                        PrefsUtil.with(mContext).write("longitude", city_long);
                        if (isVerifyNumber) {
                            if (PrefsUtil.with(mContext).readBoolean("newAccount")) {
                                PrefsUtil.with(mContext).write("newAccount", false);
                                intent = new Intent(mContext, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCountryCode() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getCountryCode");

        new ParseJSON(mContext, url, params, values, EditCountryCodePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        EditCountryCodePojo resultObj = (EditCountryCodePojo) obj;
                        arrCountryCode.clear();
                        arrCountryCode.addAll(resultObj.getData());
                        adapterCountryCode.notifyDataSetChanged();
                        countryCode = intent.getStringExtra("countryCode");
                        for (int i = 0; i < arrCountryCode.size(); i++) {
                            if (arrCountryCode.get(i).getPhoneCode().equalsIgnoreCase(countryCode)) {
                                spinnerCountryCode.setSelection(i);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    private void getCountryData() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getCountry");

        new ParseJSON(mContext, url, params, values, CountryPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CountryPojo resultObj = (CountryPojo) obj;
                        arrCountry.clear();
                        arrCountry.add(new CountryItem("0", getString(R.string.edit_country)));
                        arrCountry.addAll(resultObj.getData());
                        adapterCountry.notifyDataSetChanged();
                        countryID = intent.getStringExtra("countryID");
                        for (int i = 0; i < arrCountry.size(); i++) {
                            if (arrCountry.get(i).getId().equalsIgnoreCase(countryID)) {
                                spinnerCountry.setSelection(i);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    private void getStateData(String countryID) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getState");

        params.add("CountryID");
        values.add(countryID);

        new ParseJSON(mContext, url, params, values, StatePojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        StatePojo resultObj = (StatePojo) obj;
                        arrState.clear();
                        arrState.add(new StateItem("0", getString(R.string.edit_state)));
                        arrState.addAll(resultObj.getData());
                        adapterState.notifyDataSetChanged();
                        stateID = intent.getStringExtra("stateID");
                        for (int i = 0; i < arrState.size(); i++) {
                            if (arrState.get(i).getId().equalsIgnoreCase(stateID)) {
                                spinnerState.setSelection(i);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    private void getCityData(String stateID) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("getCity");

        params.add("StateID");
        values.add(stateID);

        new ParseJSON(mContext, url, params, values, CityPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CityPojo resultObj = (CityPojo) obj;
                        arrCity.clear();
                        arrCity.add(new CityItem("0", getString(R.string.edit_city)));
                        arrCity.addAll(resultObj.getData());
                        adapterCity.notifyDataSetChanged();
                        cityID = intent.getStringExtra("cityID");
                        for (int i = 0; i < arrCity.size(); i++) {
                            if (arrCity.get(i).getId().equalsIgnoreCase(cityID)) {
                                spinnerCity.setSelection(i);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    private void editProfileDetails() {
        profileImg = intent.getStringExtra("profile_img");
        strFirstName = intent.getStringExtra("first_name");
        strLastName = intent.getStringExtra("last_name");
        strGender = intent.getStringExtra("gender");
        strDob = intent.getStringExtra("dob");
        strPhoneNo = intent.getStringExtra("phone_no");

        /*Picasso.with(mContext)
                .load(profileImg)
                .error(R.drawable.no_user)
                .placeholder(R.drawable.no_user)
                .into(imgProfile);*/

        if (!strFirstName.equals("")) {
            edtFirstName.setText(strFirstName);
        }

        if (!strLastName.equals("")) {
            edtLastName.setText(strLastName);
        }

        if (!strGender.equals("")) {
            if (strGender.equalsIgnoreCase(String.valueOf(R.string.edit_male))) {
                radioMale.setChecked(true);
                radioFemale.setChecked(false);
                imgMale.setImageResource(R.drawable.rd_male_blue);
                imgFemale.setImageResource(R.drawable.rd_female_gray);
            } else if (strGender.equalsIgnoreCase(String.valueOf(R.string.edit_female))) {
                radioMale.setChecked(false);
                radioFemale.setChecked(true);
                imgMale.setImageResource(R.drawable.rd_male_gray);
                imgFemale.setImageResource(R.drawable.rd_female_blue);
            }
        }

        if (!strDob.equals("")) {
            txtDob.setText(strDob);
        }

        if (!strPhoneNo.equals("")) {
            edtPhoneNo.setText(strPhoneNo);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                *//*mCurrentFragment.setImageUri(mCropImageUri);*//*
                CropImage.activity(mCropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRatioX, aspectRatioY)
                        .start(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }*/

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       /* if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                mCurrentFragment.setImageUri(imageUri);
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(aspectRatioX, aspectRatioY)
                        .start(this);
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.e("RESULT URI IS : ", result.getUri().toString());
                //Uri resultUri = result.getUri();
                profileImg = result.getUri().toString();
                uploadProfileImage(profileImg);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }*/

        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_PROFILE_DETAILS_CODE) {
                getCountryCode();
                getCountryData();
            } else if (requestCode == UPDATE_PROFILE_DETAILS_CODE) {
                updateMyProfile(strFirstName, strLastName, strGender, strDob, countryCode, strPhoneNo, countryID, stateID, cityID, city_lat, city_long);
            } else if (requestCode == PHONE_VERIFICATION_CODE) {
                strPhoneNo = PrefsUtil.with(mContext).readString("phone_no");
                edtPhoneNo.setText(strPhoneNo);
            }
        }
    }

    private void uploadProfileImage(final String image_upload) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();
        imgParams = new ArrayList<>();
        imgValues = new ArrayList<>();

        params.add("action");
        values.add("userEditPicture");

        params.add("user_id");
        values.add(userId);

        params.add("picture_type");
        values.add("Profile");

        imgParams.add("image_upload");
        imgValues.add(new File(image_upload.replace("file://", "")));

        new ImageUploadParseJSON(mContext, url, params, values, imgParams, imgValues, CommonPojo.class, new ImageUploadParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        PrefsUtil.with(mContext).write("profile_img", image_upload);
                        /*Picasso.with(mContext)
                                .load(profileImg)
                                .into(imgProfile);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void verifyNumber(String userId, final String strPhoneNo, final String strCountryCode) {
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
        values.add("Edit");

        new ParseJSON(mContext, url, params, values, PhoneVerificationPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        VerificationActivity.isFromEditProfile = true;
                        PhoneVerificationPojo resultObj = (PhoneVerificationPojo) obj;
                        Toast.makeText(mContext, resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        intent = new Intent(mContext, VerificationActivity.class);
                        intent.putExtra("otp_code", resultObj.getData().getCode());
                        intent.putExtra("phoneNo", strPhoneNo);
                        intent.putExtra("countryCode", strCountryCode);
                        startActivityForResult(intent, PHONE_VERIFICATION_CODE);
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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(getString(R.string.descard_changes));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                intent = new Intent(mContext, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

