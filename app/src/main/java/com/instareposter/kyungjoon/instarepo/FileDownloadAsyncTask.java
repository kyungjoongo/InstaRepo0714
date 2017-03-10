package com.instareposter.kyungjoon.instarepo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.widget.ImageView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class FileDownloadAsyncTask extends AsyncTask<String, String, String> {
    String imgFullPath;
    private Context mContext;
    ImageView mImageView;

    public FileDownloadAsyncTask(Context context) {
        this.imgFullPath = "";

        this.mContext = context;

    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... urls) {
        try {
            URL url = new URL((String) new JSONObject(CommonUtils.getJsonFromHttpRequestUrl(urls[0].toString())).get("thumbnail_url"));
            URLConnection conexion = url.openConnection();
            conexion.connect();
            Log.d(BuildConfig.BUILD_TYPE, url.toString());
            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
            String imageFileName = makeImageFileName(url);
            InputStream input = new BufferedInputStream(url.openStream());
            int randNum = new Random().nextInt(100000) + 1;
            String todayDate = CommonUtils.makeTodayDate();
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/" + todayDate + randNum + ".jpg");
            this.imgFullPath = "/DCIM/" + todayDate + randNum + ".jpg";
            byte[] data = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
            long total = 0;
            while (true) {
                int count = input.read(data);
                if (count == -1) {
                    break;
                }
                total += (long) count;
                String[] strArr = new String[1];
                strArr[0] = StringUtils.EMPTY + ((int) ((100 * total) / ((long) lenghtOfFile)));
                publishProgress(strArr);
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.imgFullPath;
    }

    public String makeImageFileName(URL url) {
        String[] splitedArray = url.toString().split("/");
        return splitedArray[splitedArray.length - 1];
    }

    protected void onPostExecute(String imgFullPath) {
        super.onPostExecute(imgFullPath);
        Log.d("ASYNC", "download Success imgFullPath-->!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! = " + imgFullPath);
    }

    protected void onCancelled() {
        super.onCancelled();
    }
}
