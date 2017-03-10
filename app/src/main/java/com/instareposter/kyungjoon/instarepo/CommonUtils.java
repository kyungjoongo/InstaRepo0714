package com.instareposter.kyungjoon.instarepo;

import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


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
}
