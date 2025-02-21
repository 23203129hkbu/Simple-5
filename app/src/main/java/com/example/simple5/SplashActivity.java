package com.example.simple5;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    TextView nameTv, name2Tv;
    long animTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        getWindow(): 獲取當前活動的窗口對象。
        setFlags(): 設置窗口的屬性標誌。
        WindowManager.LayoutParams.FLAG_FULLSCREEN: 這是一個標誌，表示將窗口設置為全螢幕模式。
         */
        // 這行代碼的作用是隱藏狀態欄，讓應用程序佔滿整個屏幕，提供更沉浸的使用體驗。
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        imageView = findViewById(R.id.iv_logo_splash);
        nameTv = findViewById(R.id.tv_splash_name);
        name2Tv = findViewById(R.id.tv_splash_name2);

        ObjectAnimator animatorY = ObjectAnimator.ofFloat(imageView,"y",400f);
        ObjectAnimator animatorname = ObjectAnimator.ofFloat(nameTv,"x",300f);
        animatorY.setDuration(animTime);
        animatorname.setDuration(animTime);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,animatorname);
        animatorSet.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
}