package com.instareposter.kyungjoon.instarepo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.orhanobut.logger.Logger;

import org.apache.commons.lang.StringUtils;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    EditText editText1;
    String fullUrl;
    String downloadedImagePath;
    ImageButton instaBtn;
    ImageView mImageView;
    ClipboardManager myClipBoard;
    String beSavedWaterMarkedImageUri = "";
    String beSavedImageUri = "";
    CheckBox checkbox1;

    TextView imageTextView;


    private TextView mTextMessage;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * top메뉴 클릭 이벤트..........
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_usage: // setting_btn 이 눌렸을 경우 이벤트 발생

                //Toast.makeText(MainActivity.this.getApplicationContext(), "사용법 설명", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), UsageActivity.class);
                startActivity(intent); // 다음 화면으로 넘어간다
                return true;
            case R.id.btn_about:
                //Toast.makeText(MainActivity.this.getApplicationContext(), "about", Toast.LENGTH_SHORT).show();

                intent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intent); // 다음 화면으로 넘어간다


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        addListenerOnInstaButton();

        checkbox1 = (CheckBox) findViewById(R.id.checkbox1);
        checkbox1.setChecked(true);


        imageTextView = (TextView) findViewById(R.id.imageUri1);

        findViewById(R.id.checkbox1).setOnClickListener(this.mCheckboxListner);

        findViewById(R.id.repost).setOnClickListener(this.mClickListner);
        findViewById(R.id.share).setOnClickListener(this.mClickListner);
        findViewById(R.id.download).setOnClickListener(this.mClickListner);

        this.mImageView = (ImageView) findViewById(R.id.imageview1);


        String url = new Intent(getIntent()).getStringExtra("url");
        this.fullUrl = "http://api.instagram.com/oembed?url=" + url;
        String thumbUrl = StringUtils.EMPTY;
        if (url != null) {
            try {

                beSavedWaterMarkedImageUri = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMG_DOWNLOAD_DIR + CommonUtils.makeTodayRandNo() + "_watermaked" + ".jpg";

                beSavedImageUri = Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.IMG_DOWNLOAD_DIR + CommonUtils.makeTodayRandNo() + ".jpg";

                DownloadAndBindImageViewAsyncTask downloadandbindimageviewasynctask = new DownloadAndBindImageViewAsyncTask(this.mImageView, MainActivity.this, beSavedWaterMarkedImageUri, beSavedImageUri);
                downloadandbindimageviewasynctask.execute(new String[]{this.fullUrl});


                imageTextView.setTag(beSavedWaterMarkedImageUri);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //클립카피 이벤트 리스너
        this.myClipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        this.myClipBoard.addPrimaryClipChangedListener(this.mPrimaryClipChangedListener);
    }

    //체크박스 리스터
    CheckBox.OnClickListener mCheckboxListner = new CheckBox.OnClickListener() {
        public void onClick(View v) {




            if (checkbox1.isChecked()) {
                Log.d("sdfsd", "Checked");

                try {

                    Logger.d("beSavedWaterMarkedImageUri-->"+ beSavedWaterMarkedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(beSavedWaterMarkedImageUri, options);
                    mImageView.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();

                }


            } else {

                try {

                    Logger.d("beSavedWaterMarkedImageUri-->"+ "");
                    Logger.d("beSavedImageUri-->"+ beSavedImageUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(beSavedImageUri, options);
                    mImageView.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();

                }

                Log.d("sdfsd", "unChecked");


            }
        }
    };


    public void checkPermission() {

        boolean hasPermission;
        if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            hasPermission = true;
        } else {
            hasPermission = false;
        }
        if (!hasPermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, MainActivity.REQUEST_WRITE_STORAGE);
        }

    }

    /**
     * 인스타 버튼 클릭 이벤트
     */

    public void addListenerOnInstaButton() {
        this.instaBtn = (ImageButton) findViewById(R.id.instaBtn);
        this.instaBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //DO SOMETHING! {RUN SOME FUNCTION ... DO CHECKS... ETC}
                MainActivity.openApp(MainActivity.this, "com.instagram.android");
            }
        });
    }

    private void createShareIntent(String type, String mediaPath, String sharePackageName) {

        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType(type);
        shareIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(mediaPath)));
        startActivity(Intent.createChooser(shareIntent, "Share to"));
    }

    private void createShareIntentForInstargram(String type, String mediaPath, String sharePackageName) {

        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType(type);
        Log.d("media-path", mediaPath);
        shareIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(mediaPath)));
        shareIntent.setPackage("com.instagram.android");
        startActivity(shareIntent);
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
            Toast toast;
            String targetImage = "";


            if (checkbox1.isChecked()) {
                targetImage = beSavedWaterMarkedImageUri;
            }else{
                targetImage = beSavedImageUri;
            }


            switch (v.getId()) {
                case R.id.repost:
                    try {


                        MainActivity.this.createShareIntentForInstargram("image/*", targetImage, "0");
                    } catch (Exception e22) {
                        e22.printStackTrace();
                    }

                    break;
                case R.id.download:

                    try {
                        downloadedImagePath = new FileDownloadAsyncTask(MainActivity.this.getApplicationContext()).execute(new String[]{MainActivity.this.fullUrl}).get();
                        toast = Toast.makeText(MainActivity.this.getApplicationContext(), "\n" +
                                "You have successfully downloaded on  " + downloadedImagePath, Toast.LENGTH_LONG);

                        toast.show();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    break;
                case R.id.share :

                    try {

                        Logger.d("targetImage-->"+ targetImage);
                        MainActivity.this.createShareIntent("image/*", targetImage, "0");
                        toast = Toast.makeText(MainActivity.this.getApplicationContext(), "Share this pic", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e22) {
                        e22.printStackTrace();
                    }
                    break;
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (!new File("/sdcard/tempdir___").mkdir()) {
            Log.w("directory not created", "directory not created");
        }
    }


}
