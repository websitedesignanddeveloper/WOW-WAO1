package com.application.wowwao1.AsyncTask;


import android.os.AsyncTask;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by nct58 on 19/01/2017.
 * http://www.codejava.net/java-se/networking/upload-files-by-sending-multipart-request-programmatically
 */
public class UploadImageAsyncTask extends AsyncTask<Void, Void, String> {

    private String url;
    private OnAsyncResult onAsyncResult;
    private Boolean resultFlag;
    private ArrayList<File> files;
    String charset = "UTF-8";
    ArrayList<String> arrparams, arrvalues;
    ArrayList<String> file_params;

    public UploadImageAsyncTask(String url, OnAsyncResult listner, ArrayList<String> params,
                                ArrayList<String> values, ArrayList<String> file_params,
                                ArrayList<File> files) {
        this.url = url;
        this.onAsyncResult = listner;
        resultFlag = false;
        this.files = files;
        this.arrparams = params;
        this.arrvalues = values;
        this.file_params = file_params;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            MultipartUtility multipart = new MultipartUtility(String.valueOf(url), charset);

            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");

					/*multipart.addFormField("description", "Cool Pictures");
					multipart.addFormField("keywords", "Java,upload,Spring");*/

            /*multipart.addFormField("type", "image");
            multipart.addFormField("uploadFor", "service");*/
            for(int i = 0; i < arrparams.size(); i++){
                multipart.addFormField(arrparams.get(i), arrvalues.get(i));
            }

            //multipart.addFilePart("fileUpload", uploadFile1);
            //multipart.addFilePart("fileUpload", uploadFile2);
            for(int i = 0; i < files.size(); i++){
                multipart.addFilePart(file_params.get(i), files.get(i));
            }

            String response = multipart.finish();

            System.out.println("SERVER REPLIED:");
            System.out.println(response);
            resultFlag = true;
            return response;
        } catch (SocketTimeoutException e1) {
            resultFlag = false;
            return "Connection has timed out. Do you want to retry?";

        } catch (Exception e) {
            e.printStackTrace();
            resultFlag = false;
            return "Unexpected error has occurred";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (resultFlag) {
            if (onAsyncResult != null) {
                onAsyncResult.OnSuccess(result);
            }
        } else {
            if (onAsyncResult != null) {
                onAsyncResult.OnFailure(result);
            }
        }
    }
}