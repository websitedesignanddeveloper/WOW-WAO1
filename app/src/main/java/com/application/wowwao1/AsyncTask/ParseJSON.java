package com.application.wowwao1.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.application.wowwao1.R;
import com.application.wowwao1.Utils.ConnectionCheck;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nct96 on 23-Sep-16.
 * http://www.jsonschema2pojo.org/
 */

public class ParseJSON {

    private GetAsyncTask getAsyncTask;
    private Uri.Builder builder;
    private ProgressDialog prd;
    private Context mContext;

    public String url;
    public ArrayList<String> params;
    public ArrayList<String> values;
    public Object model;

    private OnResultListner onResultListner;
    ConnectionCheck cd;
    boolean isInternetAvailable;

    public ParseJSON() {
    }


    public ParseJSON(Context mContext, String url, ArrayList<String> params, ArrayList<String> values, Object model, OnResultListner onResultListner) {
        this.url = url;
        this.params = params;
        this.values = values;
        this.model = model;
        this.mContext = mContext;
        this.onResultListner = onResultListner;

        cd = new ConnectionCheck();
        isInternetAvailable = cd.isNetworkConnected(mContext);
        if (isInternetAvailable) {
            getData();
        } else {
            new ConnectionCheck().showconnectiondialog(mContext).show();
        }
    }

    public interface OnResultListner {
        void onResult(boolean status, Object obj);
    }

    public void getData() {
        builder = new Uri.Builder();

        //final Object[] resultObj = new Object[1];

        for (int i = 0; i < params.size(); i++) {
            builder.appendQueryParameter(params.get(i), values.get(i));
        }


        //builder.appendQueryParameter("")
        try {
            prd = new ProgressDialog(mContext);
            prd.setTitle(mContext.getString(R.string.loading_dialog_title));
            prd.setMessage(mContext.getString(R.string.loading_dialog_msg));
            prd.setCancelable(false);
            //prd.show();
        } catch (Exception e) {
            //e.printStackTrace();
        }


        // Async Result
        OnAsyncResult onAsyncResult = new OnAsyncResult() {
            @Override
            public void OnSuccess(String result) {
                try {
                    //prd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    JSONObject object = new JSONObject(result);
                    if (object.getBoolean("status")) {
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        gsonBuilder.setDateFormat("M/d/yy hh:mm a"); //Format of our JSON dates
                        Gson gson = gsonBuilder.create();
                        onResultListner.onResult(true, gson.fromJson(result, (Class) model));
                    } else {
                        onResultListner.onResult(false, object.getString("message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFailure(String result) {
                try {
                    //prd.dismiss();

                    getAsyncTask = new GetAsyncTask(url, this, builder);
                    //Toast.makeText(SplashScreenActivity.this,result,Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.error);
                    builder.setMessage(result);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                //prd.show();
                                getAsyncTask.execute();
                            } catch (Exception e) {

                            }

                        }
                    });
                    onResultListner.onResult(false, "Error");
                    builder.setNegativeButton(R.string.no, null);
                    builder.show();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        };
        getAsyncTask = new GetAsyncTask(url, onAsyncResult, builder);
        getAsyncTask.execute();

    }


}
