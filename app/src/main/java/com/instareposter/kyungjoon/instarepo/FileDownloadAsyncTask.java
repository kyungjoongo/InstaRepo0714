package com.instareposter.kyungjoon.instarepo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import android.widget.ImageView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Random;

public class FileDownloadAsyncTask extends AsyncTask<String, String, String> {
    String imgFullPath;
    private Context mContext;
    ImageView mImageView;
    ProgressDialog progDialog;
    String author_name = "";
    HashMap resultmap=new HashMap();

    public FileDownloadAsyncTask(Context context) {
        this.imgFullPath = "";

        this.mContext = context;

    }

    protected void onPreExecute() {
        super.onPreExecute();
        /*this.progDialog = new ProgressDialog(this.mContext);
        this.progDialog.setTitle("Please Wait..");
        this.progDialog.show();
        this.progDialog.getWindow().setLayout(-1, -2);*/
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


            File instaReposterDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMG_DOWNLOAD_DIR);
            instaReposterDir.mkdirs();
            OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMG_DOWNLOAD_DIR + todayDate + randNum + ".jpg");



            this.imgFullPath = Constants.IMG_DOWNLOAD_DIR + todayDate + randNum + ".jpg";
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
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(instaReposterDir); //out is your output file
                mediaScanIntent.setData(contentUri);
                mContext.sendBroadcast(mediaScanIntent);
            } else {
                mContext.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            }*/


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
        //  this.progDialog.dismiss();
        super.onPostExecute(imgFullPath);


        Log.d("ASYNC", "download Success imgFullPath-->!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! = " + imgFullPath);
    }


    public static Bitmap getWatermarkImage(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawText(watermark, 20, h-25, paint);

        return result;
    }


    protected void onCancelled() {
        super.onCancelled();
    }
}
