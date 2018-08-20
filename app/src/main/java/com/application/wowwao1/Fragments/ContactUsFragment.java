package com.application.wowwao1.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.wowwao1.Activities.NoConnectionActivity;
import com.application.wowwao1.AsyncTask.ParseJSON;
import com.application.wowwao1.Models.CommonPojo;
import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.application.wowwao1.Utils.KeyboardUtils;
import com.application.wowwao1.WebServices.WebServiceUrl;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by nct119 on 11/8/17.
 */

public class ContactUsFragment extends Fragment {

    private static final int CONTACT_US_CODE = 100;
    private EditText edtFirstName, edtLastName, edtEmail, edtSubject, edtMessage;
    private String strFirstName, strLastName, strEmail, strSubject, strMessage;
    private Button btnSend;
    private String url;
    private ArrayList<String> params;
    private ArrayList<String> values;

    private boolean isInternetAvailable;
    private ConnectionCheck cd;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        new KeyboardUtils().setupUI(rootView.findViewById(R.id.fragment_contact_us), getActivity());
        initViews(rootView);
        cd = new ConnectionCheck();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strFirstName = edtFirstName.getText().toString().trim();
                strLastName = edtLastName.getText().toString().trim();
                strEmail = edtEmail.getText().toString().trim();
                strSubject = edtSubject.getText().toString().trim();
                strMessage = edtMessage.getText().toString().trim();

                boolean validationResult = formValidation();
                if (validationResult) {
                    isInternetAvailable = cd.isNetworkConnected(getContext());
                    if (isInternetAvailable) {
                        contactUsCall(strFirstName, strLastName, strEmail, strSubject, strMessage);
                    } else {
                        intent = new Intent(getContext(), NoConnectionActivity.class);
                        startActivityForResult(intent,CONTACT_US_CODE);
                    }
                }
            }
        });

        return rootView;
    }

    private void initViews(View rootView) {
        btnSend = (Button) rootView.findViewById(R.id.btnSend);

        edtFirstName = (EditText) rootView.findViewById(R.id.edtFirstName);
        edtLastName = (EditText) rootView.findViewById(R.id.edtLastName);
        edtEmail = (EditText) rootView.findViewById(R.id.edtEmail);
        edtSubject = (EditText) rootView.findViewById(R.id.edtSubject);
        edtMessage = (EditText) rootView.findViewById(R.id.edtMessage);
    }

    private boolean formValidation() {
        boolean validationResult = true;
        edtFirstName.setError(null);
        edtLastName.setError(null);
        edtEmail.setError(null);
        edtSubject.setError(null);
        edtMessage.setError(null);

        if (strFirstName.length() == 0) {
            edtFirstName.setError(getResources().getString(R.string.contact_us_empty_firstname));
            edtFirstName.requestFocus();
            validationResult = false;
        } else if (strLastName.length() == 0) {
            edtLastName.setError(getResources().getString(R.string.contact_us_empty_lastname));
            edtLastName.requestFocus();
            validationResult = false;
        } else if (strEmail.length() == 0) {
            edtEmail.setError(getResources().getString(R.string.contact_us_empty_email));
            edtEmail.requestFocus();
            validationResult = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            edtEmail.setError(getResources().getString(R.string.contact_us_valid_email));
            edtEmail.requestFocus();
            validationResult = false;
        } else if (strSubject.length() == 0) {
            edtSubject.setError(getResources().getString(R.string.contact_us_empty_subject));
            edtSubject.requestFocus();
            validationResult = false;
        } else if (strMessage.length() == 0) {
            edtMessage.setError(getResources().getString(R.string.contact_us_empty_message));
            edtMessage.requestFocus();
            validationResult = false;
        }

        return validationResult;
    }

    private void contactUsCall(String strFirstName, String strLastName, String strEmail, String strSubject, String strMessage) {
        url = WebServiceUrl.baseUrl;

        params = new ArrayList<>();
        values = new ArrayList<>();

        params.add("action");
        values.add("userContactUs");

        params.add("firstname");
        values.add(strFirstName);

        params.add("lastname");
        values.add(strLastName);

        params.add("email");
        values.add(strEmail);

        params.add("subject");
        values.add(strSubject);

        params.add("message");
        values.add(strMessage);

        new ParseJSON(getContext(), url, params, values, CommonPojo.class, new ParseJSON.OnResultListner() {
            @Override
            public void onResult(boolean status, Object obj) {
                if (status) {
                    try {
                        CommonPojo resultObj = (CommonPojo) obj;
                        Toast.makeText(getContext(), resultObj.getMessage(), Toast.LENGTH_SHORT).show();
                        edtFirstName.setText("");
                        edtLastName.setText("");
                        edtEmail.setText("");
                        edtSubject.setText("");
                        edtMessage.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_US_CODE) {
                contactUsCall(strFirstName, strLastName, strEmail, strSubject, strMessage);
            }
        }
    }
}
