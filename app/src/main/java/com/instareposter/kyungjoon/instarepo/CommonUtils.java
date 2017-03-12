package com.instareposter.kyungjoon.instarepo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class CommonUtils {
    public static String getJsonFromHttpRequestUrl(String address) {
        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(address));
            if (response.getStatusLine().getStatusCode() == Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    builder.append(line);
                }
            } else {
                Log.e(MainActivity.class.toString(), "failto JSON object");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String makeTodayDate() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public static String makeTodayRandNo(){

        int randNum = new Random().nextInt(100000) + 1;
        String todayDate = CommonUtils.makeTodayDate();

        return todayDate + randNum;
    }

    public static Bitmap getWatermarkImage(Bitmap src, String watermark,Context mContext) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(23);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(watermark, 20, h - 25, paint);

        Bitmap appLogoBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);

        Bitmap  resizedAppLogo = Bitmap.createScaledBitmap(appLogoBitmap, 60,60, false);
        canvas.drawBitmap(resizedAppLogo ,10,10,null);

        return result;
    }

   /* public static Uri bitmapToUriConverter(Bitmap mBitmap, Context mContext) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = mBitmap;

            File file = new File(mContext.getFilesDir(), "Image"+ new Random().nextInt() + ".jpeg");

            FileOutputStream out = mContext.openFileOutput(file.getName(), 0);

            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);


            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }*/

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;

        final int width = options.outWidth;

        int inSampleSize = 1;


        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);

            final int widthRatio = Math.round((float) width / (float) reqWidth);


            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }


        return inSampleSize;

    }

}
