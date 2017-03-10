package com.instareposter.kyungjoon.instarepo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    EditText editText1;
    String fullUrl;
    String imageFullPath;
    ImageButton instaBtn;
    ImageView mImageView;
    ClipboardManager myClipBoard;
    String thumbUri;


    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.repost:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.download:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.share:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // addListenerOnInstaButton();
        this.mImageView = (ImageView) findViewById(R.id.imageview1);
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(this.mOnNavigationItemSelectedListener);


        String url = new Intent(getIntent()).getStringExtra("url");


        this.fullUrl = "http://api.instagram.com/oembed?url=" + url;
        String thumbUrl = StringUtils.EMPTY;
        if (url != null) {
            try {
                new DownloadAndBindImageViewAsyncTask(this.mImageView, this).execute(new String[]{this.fullUrl});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        findViewById(R.id.repost).setOnClickListener(this.mClickListner);
        findViewById(R.id.share).setOnClickListener(this.mClickListner);
        findViewById(R.id.download).setOnClickListener(this.mClickListner);


        this.myClipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        this.myClipBoard.addPrimaryClipChangedListener(this.mPrimaryClipChangedListener);
        CharSequence text = getIntent().getCharSequenceExtra("android.intent.extra.PROCESS_TEXT");
    }

    private void createShareIntent(String type, String mediaPath, String sharePackageName) {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType(type);
        shareIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(mediaPath)));
        if (sharePackageName.equals("com.instagram.android")) {
            shareIntent.setPackage("com.instagram.android");
        }
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            String con = MainActivity.this.myClipBoard.getPrimaryClip().getItemAt(0).getText().toString();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.putExtra("url", con);
            MainActivity.this.startActivity(intent);
        }
    };


    Button.OnClickListener mClickListner = new View.OnClickListener() {
        public void onClick(View v) {
            boolean hasPermission;
            if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
                hasPermission = true;
            } else {
                hasPermission = false;
            }
            if (!hasPermission) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, MainActivity.REQUEST_WRITE_STORAGE);
            }
            FileDownloadAsyncTask fileDownloadAsyncTask;
            String mediaPath;
            Toast toast;
            switch (v.getId()) {
                case R.id.repost /*2131624113*/:
                    try {
                        fileDownloadAsyncTask = new FileDownloadAsyncTask(MainActivity.this.getApplicationContext());
                        MainActivity.this.imageFullPath = (String) new FileDownloadAsyncTask(MainActivity.this.getApplicationContext()).execute(new String[]{MainActivity.this.fullUrl}).get();
                        Log.d("imageFullPath--->", MainActivity.this.imageFullPath);
                        mediaPath = Environment.getExternalStorageDirectory() + MainActivity.this.imageFullPath;
                        MainActivity.this.createShareIntent("image/*", mediaPath, "com.instagram.android");
                        Toast.makeText(MainActivity.this.getApplicationContext(), "Repost this pic to my insta", 0).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                case R.id.download /*2131624114*/:
                    toast = Toast.makeText(MainActivity.this.getApplicationContext(), "Download Complete!", 0);
                    try {
                        fileDownloadAsyncTask = new FileDownloadAsyncTask(MainActivity.this.getApplicationContext());
                        new FileDownloadAsyncTask(MainActivity.this.getApplicationContext()).execute(new String[]{MainActivity.this.fullUrl}).get();
                        toast.show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                case R.id.share /*2131624115*/:
                    toast = Toast.makeText(MainActivity.this.getApplicationContext(), "Share this pic", 0);
                    try {
                        fileDownloadAsyncTask = new FileDownloadAsyncTask(MainActivity.this.getApplicationContext());
                        MainActivity.this.imageFullPath = (String) new FileDownloadAsyncTask(MainActivity.this.getApplicationContext()).execute(new String[]{MainActivity.this.fullUrl}).get();
                        Log.d("imageFullPath--->", MainActivity.this.imageFullPath);
                        mediaPath = Environment.getExternalStorageDirectory() + MainActivity.this.imageFullPath;
                        MainActivity.this.createShareIntent("image/*", mediaPath, "0");
                        toast.show();
                    } catch (Exception e22) {
                        e22.printStackTrace();
                    }
                default:
            }
        }
    };

    public static boolean openApp(Context context, String packageName) {
        try {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
            }
            i.addCategory("android.intent.category.LAUNCHER");
            context.startActivity(i);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void bindImageViewElement(String imageFullPath) {
        File imgFile = new File(imageFullPath);
        if (imgFile.exists()) {
            ((ImageView) findViewById(R.id.imageview1)).setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!new File("/sdcard/tempdir___").mkdir()) {
            Log.w("directory not created", "directory not created");
        }
    }


}
