package com.instareposter.kyungjoon.instarepo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.HashMap;
import java.util.Random;

public class HttpRequestGetJsonAsyncTask extends AsyncTask<String, String, String> {
    String imgFullPath;
    private Context mContext;
    ImageView mImageView;
    ProgressDialog progDialog;
    String author_name = "";
    HashMap resultmap = new HashMap();

    static final String IMG_DOWNLOAD_DIR = "/pictures/InstaReposter/";

    public HttpRequestGetJsonAsyncTask(Context context) {
        this.mContext = context;

    }

    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected String doInBackground(String... urls) {
        try {

            JSONObject jsonobject = (new JSONObject(CommonUtils.getJsonFromHttpRequestUrl(urls[0].toString())));
            author_name = (String) jsonobject.get("author_name");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return author_name;
    }

    protected void onPostExecute(String author_name) {
        //  this.progDialog.dismiss();
        super.onPostExecute(author_name);
        Log.d("ASYNC", "author_nameauthor_nameauthor_name-->!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! = " + author_name);
    }



    protected void onCancelled() {
        super.onCancelled();
    }
}
