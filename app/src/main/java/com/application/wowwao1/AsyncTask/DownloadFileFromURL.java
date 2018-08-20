package com.application.wowwao1.AsyncTask;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by nct122 on 13/7/17.
 */

public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private Context context;
    private ProgressDialog pd;
    private String dirPath = Environment.getExternalStorageDirectory() + File.separator + "WOW WAO1";

    public DownloadFileFromURL(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        pd.setMessage("Please Wait");
        pd.show();
    }

    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            URL url = new URL(f_url[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            int lenghtOfFile = conection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            createDir(dirPath);
            OutputStream output = new FileOutputStream(dirPath + File.separator + f_url[1]);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }


            output.flush();
            output.close();
            input.close();
            return dirPath + File.separator + f_url[1];
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
            return "";
        }


    }

    @Override
    protected void onPostExecute(String action) {
        pd.dismiss();
        Log.e("DownloadFileFromURL", "Action : " + action);
        File file = new File(action);
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            if (file.toString().contains(".doc") || file.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/msword");
            } else if (file.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/pdf");
            } else if (file.toString().contains(".ppt") || file.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/vnd.ms-powerpoint");
            } else if (file.toString().contains(".xls") || file.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/vnd.ms-excel");
            } else if (file.toString().contains(".zip") || file.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/x-wav");
            } else if (file.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(Uri.fromFile(new File(action)), "application/rtf");
            } else if (file.toString().contains(".wav") || file.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(Uri.fromFile(new File(action)), "audio/x-wav");
            } else if (file.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(Uri.fromFile(new File(action)), "image/gif");
            } else if (file.toString().contains(".jpg") || file.toString().contains(".jpeg") || file.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(Uri.fromFile(new File(action)), "image/jpeg");
            } else if (file.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(Uri.fromFile(new File(action)), "text/plain");
            } else if (file.toString().contains(".3gp") || file.toString().contains(".mpg") || file.toString().contains(".mpeg") || file.toString().contains(".mpe") || file.toString().contains(".mp4") || file.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(Uri.fromFile(new File(action)), "video/*");
            } else {
                //if you want you can also define the intent type for any other file
                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(Uri.fromFile(new File(action)), "*/*");
            }
            //intent.setDataAndType(Uri.fromFile(new File(action)), "application/jpg");
            context.startActivity(intent);

        } catch (ActivityNotFoundException anfe) {
            Toast toast = Toast.makeText(context, "Your device cannot open file!",
                    Toast.LENGTH_LONG);
            toast.show();
        }

    }

    public Boolean createDir(String path) {
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            return true;
        } else {
            return false;
        }
    }
}
