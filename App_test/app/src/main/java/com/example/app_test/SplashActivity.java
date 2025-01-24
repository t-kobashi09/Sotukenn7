package com.example.app_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

// ロゴにフェードインアニメーションを適用
//        ImageView logo = findViewById(R.id.logo);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1500); // 1.5秒
//        logo.startAnimation(fadeIn);

////展示用の場合コメントアウト
// 2秒後にTutorialActivityへ遷移
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish(); // SplashActivityを終了
        }, 2000); // 2000ミリ秒（3秒）

// 2秒後にMainActivityへ遷移
//        new Handler().postDelayed(() -> {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish(); // SplashActivityを終了
//        }, 2000); // 2000ミリ秒（3秒）
    }
}
