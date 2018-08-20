package com.application.wowwao1.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.wowwao1.Activities.HomeActivity;
import com.application.wowwao1.Activities.LoginActivity;
import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.Adapters.LanguageAdapter;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.AccSettingDetailsPojo;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.PrefsUtil;
import com.application.wowwao1.WebServices.WebServiceUrl;
import com.google.android.gms.appinvite.AppInviteInvitation;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class AccountSettingsFragment extends Fragment {

    private static final int ACC_SETTING_DETAILS_CODE = 100;
    private static final int CHANGE_PASS_CODE = 101;
    private static final int SAVE_NOTIFICATION_CODE = 102;
    private static final int DEACTIVATE_ACC_CODE = 103;
    private EditText edtOldPass, edtNewPass, edtConfirmNewPass, edtPassword;
    private SwitchCompat switchReqAccept, switchReqReceived, switchComments, switchMsg;
    private String strOldPass, strNewPass, strConfirmNewPass, strReqAccept, strReqReceived, strComments, strMsg, strDialogPassword;
    private Button btnSavePass, btnCancelPass, btnSaveNotification, btnSubmit, btn_saveLanguage;
    private TextView txtClickHere, txtYes, txtNo;
    private Dialog dialogConfirmation, dialogDeactivate;
    private Spinner languageSpinner;

    private String url, userId;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    private LanguageAdapter languageAdapter;
    ArrayList<String> list = new ArrayList<>();

    private boolean isChanges = false;

    SharedPreferences myPref;
    SharedPreferences.Editor editor;

    public static final int REQUEST_INVITE = 65;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //setLanguageForApp("zh","CN");



        View rootView = inflater.inflate(R.layout.fragment_account_settings, container, false);
        initViews(rootView);

        myPref = getActivity().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        editor = myPref.edit();


        if(myPref.contains("country"))
        {
            if(myPref.getString("lang",null).equals("en"))
            {
                languageSpinner.setSelection(0);

            }
            else
            {
                languageSpinner.setSelection(1);

            }
        }

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(getContext());
        if (isInternetAvailable) {
            accountSettingsDetails();
        } else {
            intent = new Intent(getContext(), NoConnectionActivity.class);
            startActivityForResult(intent, ACC_SETTING_DETAILS_CODE);
        }

        switchReqAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strReqAccept = "y";
                } else {
                    strReqAccept = "n";
                }
            }
        });

        switchReqAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanges = true;
            }
        });

        switchReqReceived.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    strReqReceived = "y";
                } else {
                    strReqReceived = "n";
                }
            }
        });

        switchReqReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanges = true;
            }
        });

        switchComments.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChanges = true;
                if (isChecked) {
                    strComments = "y";
                } else {
                    strComments = "n";
                }
            }
        });

        switchComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanges = true;
            }
        });

        switchMsg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChanges = true;
                if (isChecked) {
                    strMsg = "y";
                } else {
                    strMsg = "n";
                }
            }
        });

        switchMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChanges = true;
            }
        });

        btnSavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOldPass = edtOldPass.getText().toString().trim();
                strNewPass = edtNewPass.getText().toString().trim();
                strConfirmNewPass = edtConfirmNewPass.getText().toString().trim();
                Boolean validationResult = formValidation();
                if (validationResult) {
                    isInternetAvailable = cd.isNetworkConnected(getContext());
                    if (isInternetAvailable) {
                        changePassword(strOldPass, strNewPass);
                    } else {
                        intent = new Intent(getContext(), NoConnectionActivity.class);
                        startActivityForResult(intent, CHANGE_PASS_CODE);
                    }
                }
            }
        });

        btnCancelPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtOldPass.setText("");
                edtNewPass.setText("");
                edtConfirmNewPass.setText("");
            }
        });

        btnSaveNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInternetAvailable = cd.isNetworkConnected(getContext());
                if (isInternetAvailable) {
                    if (isChanges) {
                        saveNotificationCall(strReqAccept, strReqReceived, strComments, strMsg);
                    }
                } else {
                    intent = new Intent(getContext(), NoConnectionActivity.class);
                    startActivityForResult(intent, SAVE_NOTIFICATION_CODE);
                }
            }
        });

        txtClickHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmationMethod();
            }
        });




        btn_saveLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedIndex = languageSpinner.getSelectedItemPosition();

                if(selectedIndex ==0)
                {
                    setLanguageForApp("en","US");

                }
                else
                {
                    setLanguageForApp("zh","CN");
                }

            }
        });


        /*btn_invitefriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
            }
        });*/



        return rootView;
    }



   
    
    

    private void saveNotificationCall(String strReqAccept, String strReqReceived, String strComments, String strMsg) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userNotificationSettings");

        params.add("user_id");
        values.add(userId);

        params.add("post_cmnt");
        values.add(strComments);

        params.add("req_rece");
        values.add(strReqReceived);

        params.add("req_acpt");
        values.add(strReqAccept);

        params.add("user_msg");
        values.add(strMsg);

        params.add("user_follow");
        values.add("y");

        params.add("post_like");
        values.add("y");


        new ParseJSON(getContext(), url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        isChanges = false;
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(getContext(), resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void accountSettingsDetails() {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userGetNotificationSettings");

        params.add("user_id");
        values.add(userId);

        new ParseJSON(getContext(), url, params, values, AccSettingDetailsPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        isChanges = false;
                        AccSettingDetailsPojo resultObj = (AccSettingDetailsPojo) obj;
                        strReqAccept = resultObj.getData().getReqAcpt();
                        strReqReceived = resultObj.getData().getReqRece();
                        strComments = resultObj.getData().getPostCmnt();
                        strMsg = resultObj.getData().getUser_msg();

                        if (strReqAccept.equalsIgnoreCase("y")) {
                            switchReqAccept.setChecked(true);
                        } else {
                            switchReqAccept.setChecked(false);
                        }

                        if (strReqReceived.equalsIgnoreCase("y")) {
                            switchReqReceived.setChecked(true);
                        } else {
                            switchReqReceived.setChecked(false);
                        }

                        if (strComments.equalsIgnoreCase("y")) {
                            switchComments.setChecked(true);
                        } else {
                            switchComments.setChecked(false);
                        }

                        if (strMsg.equalsIgnoreCase("y")) {
                            switchMsg.setChecked(true);
                        } else {
                            switchMsg.setChecked(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.nav_account_settings);
    }

    private void initViews(View rootView) {

        list.add("English (EN)");
        list.add("Chinese (CH)");

        userId = PrefsUtil.with(getContext()).readString("userId");
        edtOldPass = (EditText) rootView.findViewById(R.id.edtOldPass);
        edtOldPass.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) rootView.findViewById(R.id.inLayoutOldPass)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));

        edtNewPass = (EditText) rootView.findViewById(R.id.edtNewPass);
        edtNewPass.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) rootView.findViewById(R.id.inLayoutNewPass)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));

        edtConfirmNewPass = (EditText) rootView.findViewById(R.id.edtConfirmNewPass);
        edtConfirmNewPass.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));
        ((TextInputLayout) rootView.findViewById(R.id.inLayoutConfirmPass)).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Raleway-Regular.ttf"));

        btnSavePass = (Button) rootView.findViewById(R.id.btnSavePass);
        btnCancelPass = (Button) rootView.findViewById(R.id.btnCancelPass);
        btnSaveNotification = (Button) rootView.findViewById(R.id.btnSaveNotification);

        switchReqAccept = (SwitchCompat) rootView.findViewById(R.id.switchReqAccept);
        switchReqReceived = (SwitchCompat) rootView.findViewById(R.id.switchReqReceived);
        switchComments = (SwitchCompat) rootView.findViewById(R.id.switchComments);
        switchMsg = (SwitchCompat) rootView.findViewById(R.id.switchMsg);

        txtClickHere = (TextView) rootView.findViewById(R.id.txtClickHere);

        btn_saveLanguage = (Button) rootView.findViewById(R.id.btn_saveLanguage);

        languageSpinner = (Spinner) rootView.findViewById(R.id.language_spinner);
        languageAdapter = new LanguageAdapter(getActivity(),list);
        languageSpinner.setAdapter(languageAdapter);

        //btn_invitefriends = (Button) rootView.findViewById(R.id.btn_invitefriends);


        /*Dialog confirmation variable declaration*/
        dialogConfirmation = new Dialog(getContext());
        try {
            dialogConfirmation.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogConfirmation.setContentView(R.layout.dialog_deactivate_confirmation);
        txtYes = (TextView) dialogConfirmation.findViewById(R.id.txtYes);
        txtNo = (TextView) dialogConfirmation.findViewById(R.id.txtNo);

        /*Dialog deactivate variable declaration*/
        dialogDeactivate = new Dialog(getContext());
        dialogDeactivate = new Dialog(getContext());
        try {
            dialogDeactivate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialogDeactivate.setContentView(R.layout.dialog_deactivate_acc);
        edtPassword = (EditText) dialogDeactivate.findViewById(R.id.edtPassword);
        btnSubmit = (Button) dialogDeactivate.findViewById(R.id.btnSubmit);
    }

    private void changePassword(String strOldPass, String strNewPass) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userChangePassword");

        params.add("user_id");
        values.add(userId);

        params.add("old_password");
        values.add(strOldPass);

        params.add("new_password");
        values.add(strNewPass);

        new ParseJSON(getContext(), url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(getContext(), resultObj.getMessage(), Toast.LENGTH_LONG).show();
                        edtOldPass.setText("");
                        edtNewPass.setText("");
                        edtConfirmNewPass.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = (String) obj;
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Boolean formValidation() {
        Boolean validationResult = true;

        edtOldPass.setError(null);
        edtNewPass.setError(null);
        edtConfirmNewPass.setError(null);

        if (strOldPass.length() == 0) {
            edtOldPass.setError(getString(R.string.acc_settings_pass_empty));
            edtOldPass.requestFocus();
            validationResult = false;
        } else if (strNewPass.length() == 0) {
            edtNewPass.setError(getString(R.string.acc_settings_pass_empty));
            edtNewPass.requestFocus();
            validationResult = false;
        } else if (strNewPass.length() < 6) {
            edtNewPass.setError(getString(R.string.acc_settings_pass_length));
            edtNewPass.requestFocus();
            validationResult = false;
        } else if (strConfirmNewPass.length() == 0) {
            edtConfirmNewPass.setError(getString(R.string.acc_settings_pass_empty));
            edtConfirmNewPass.requestFocus();
            validationResult = false;
        } else if (!strNewPass.equals(strConfirmNewPass)) {
            edtNewPass.setError(getString(R.string.acc_settings_pass_must_match));
            edtNewPass.requestFocus();
            validationResult = false;
        }

        return validationResult;
    }

    /*Open first dialog when click on click here link*/
    private void dialogConfirmationMethod() {
        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmation.dismiss();
                dialogDeactivateMethod();
            }
        });

        txtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmation.dismiss();
            }
        });

        dialogConfirmation.show();
        dialogConfirmation.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /*Open second dialog when click on yes*/
    private void dialogDeactivateMethod() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDialogPassword = edtPassword.getText().toString().trim();
                edtPassword.setError(null);
                if (strDialogPassword.length() != 0) {
                    isInternetAvailable = cd.isNetworkConnected(getContext());
                    if (isInternetAvailable) {
                        deactivateAccServiceCall(strDialogPassword);
                    } else {
                        intent = new Intent(getContext(), NoConnectionActivity.class);
                        startActivity(intent);
                    }
                } else {
                    edtPassword.setError(getString(R.string.acc_settings_pass_empty));
                    edtPassword.requestFocus();
                }
            }
        });

        dialogDeactivate.show();
        dialogDeactivate.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void deactivateAccServiceCall(String strDialogPassword) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<String>();
        values = new ArrayList<String>();

        params.add("action");
        values.add("userDeactive");

        params.add("user_id");
        values.add(userId);

        params.add("password");
        values.add(strDialogPassword);

        new ParseJSON(getContext(), url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    CommonPojo resultObj = (CommonPojo) obj;
                    Toast.makeText(getContext(), resultObj.getMessage(), Toast.LENGTH_LONG).show();
                    dialogDeactivate.dismiss();
                    dialogConfirmation.dismiss();
                    PrefsUtil.with(getContext()).clearPrefs();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    ((HomeActivity) getActivity()).finish();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("onActivityResult", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + "result_ok ="+RESULT_OK);


        if (resultCode == RESULT_OK) {
            if (requestCode == ACC_SETTING_DETAILS_CODE) {
                accountSettingsDetails();
            } else if (requestCode == CHANGE_PASS_CODE) {
                changePassword(strOldPass, strNewPass);
            } else if (requestCode == SAVE_NOTIFICATION_CODE) {
                saveNotificationCall(strReqAccept, strReqReceived, strComments, strMsg);
            } else if (requestCode == DEACTIVATE_ACC_CODE) {
                deactivateAccServiceCall(strDialogPassword);
            }
        }
        else if (requestCode == REQUEST_INVITE) {

            if (resultCode == RESULT_OK) {

                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Sent ").append(Integer.toString(ids.length)).append(" invitations: ");
                    for (String id : ids) sb.append("[").append(id).append("]");
                    Toast.makeText(getActivity(),"Invited!!!",Toast.LENGTH_SHORT).show();

            } else {

                    Toast.makeText(getActivity(),"Sorry, unable to send invite.",Toast.LENGTH_SHORT).show();

            }

        }
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


        Intent refresh = new Intent(getActivity(), HomeActivity.class);

        refresh.putExtra("languageChanged",true);
        startActivity(refresh);
        getActivity().finish();


       /* TextView textView = (TextView) getActivity().findViewById(R.id.title_text);
        textView.setText(R.string.nav_account_settings);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerView, new AccountSettingsFragment()).commit();*/
    }
}
