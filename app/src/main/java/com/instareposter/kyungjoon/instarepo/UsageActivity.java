package com.instareposter.kyungjoon.instarepo;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class UsageActivity extends AppCompatActivity {

    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);


        findViewById(R.id.backButton).setOnClickListener(this.mClickListner);
    }

    Button.OnClickListener mClickListner = new View.OnClickListener() {
        public void onClick(View v) {

           // Toast.makeText(getApplicationContext(), "back to  main", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent); // 다음 화면으로 넘어간다


        }

    };
}
