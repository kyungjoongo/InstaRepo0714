package com.instareposter.kyungjoon.instarepo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONObject;

import java.net.URL;

class DownloadAndBindImageViewAsyncTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private Context mContext;
    ProgressDialog progDialog;

    public DownloadAndBindImageViewAsyncTask(ImageView bmImage, Context mContext) {
        this.bmImage = bmImage;
        this.mContext = mContext;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.progDialog = new ProgressDialog(this.mContext);
        this.progDialog.setTitle("Please Wait..");
        this.progDialog.show();
        this.progDialog.getWindow().setLayout(-1, -2);
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap mIcon11 = null;
        try {
            mIcon11 = BitmapFactory.decodeStream(new URL((String) new JSONObject(CommonUtils.getJsonFromHttpRequestUrl(urls[0].toString())).get("thumbnail_url")).openStream());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        this.progDialog.dismiss();
        this.bmImage.setImageBitmap(result);
    }
}
