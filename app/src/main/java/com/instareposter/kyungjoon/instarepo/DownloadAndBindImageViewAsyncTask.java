package com.instareposter.kyungjoon.instarepo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Random;

import static com.instareposter.kyungjoon.instarepo.R.id.imageview1;

class DownloadAndBindImageViewAsyncTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private Context mContext;
    ProgressDialog progDialog;
    String beSavedImageFileUri = "";
    String beSavedWaterMarkedImageFileUri = "";

    Uri imageFilePath = null;


    public DownloadAndBindImageViewAsyncTask(ImageView bmImage, Context mContext, String beSavedWaterMarkedImageFileUri, String beSavedImageFileUri) {
        this.bmImage = bmImage;
        this.mContext = mContext;
        this.beSavedWaterMarkedImageFileUri = beSavedWaterMarkedImageFileUri;
        this.beSavedImageFileUri = beSavedImageFileUri;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.progDialog = new ProgressDialog(this.mContext);
        this.progDialog.setTitle("Please Wait..");
        this.progDialog.show();
        this.progDialog.getWindow().setLayout(-1, -2);
    }

    public String makeImageFileName(URL url) {
        String[] splitedArray = url.toString().split("/");
        return splitedArray[splitedArray.length - 1];
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmapWaterMarkedImage = null;
        try {

            JSONObject jsonRequestUrlResult = (new JSONObject(CommonUtils.getJsonFromHttpRequestUrl(urls[0].toString())));
            String author_name = (String) jsonRequestUrlResult.get("author_name");
            String thumb_url = (String) jsonRequestUrlResult.get("thumbnail_url");

            //워터 마크 이미지를 만든다
            bitmapWaterMarkedImage = CommonUtils.getWatermarkImage(BitmapFactory.decodeStream(new URL(thumb_url).openStream()), author_name, mContext);
            Bitmap originalImage = BitmapFactory.decodeStream(new URL(thumb_url).openStream());

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(this.beSavedWaterMarkedImageFileUri);
                bitmapWaterMarkedImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                out.close();
            }

            //일반이미지 스트림 아웃풋
            out = null;
            try {
                out = new FileOutputStream(beSavedImageFileUri);
                originalImage.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                out.close();
            }


        } catch (Exception e) {
            Log.d("exception", e.toString());
        }

        return bitmapWaterMarkedImage;
    }

    protected void onPostExecute(Bitmap result) {

        super.onPostExecute(result);
        this.progDialog.dismiss();
        this.bmImage.setImageBitmap(result);

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
        canvas.drawText(watermark, 20, h - 25, paint);

        return result;
    }


}


//######################
//이미지 다운로드
//######################
         /*   URL url = new URL(thumb_url);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            Log.d(BuildConfig.BUILD_TYPE, url.toString());
            int lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
            String imageFileName = makeImageFileName(url);
            InputStream input = new BufferedInputStream(url.openStream());
            int randNum = new Random().nextInt(100000) + 1;
            String todayDate = CommonUtils.makeTodayDate();
        //Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMG_DOWNLOAD_DIR + todayDate + randNum + ".jpg"

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
                //publishProgress(strArr);
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }*/

