package com.example.th;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初回起動かどうかを判定
        boolean isFirstLaunch = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // チュートリアル画面に移動
            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish(); // メインアクティビティを終了
        } else {
            // 通常のメイン画面を表示
            setContentView(R.layout.activity_main);
        }
    }
}
