package com.example.app_test;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TutorialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // チュートリアル完了ボタンのクリックリスナー
        findViewById(R.id.btn_finish_tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // チュートリアル完了を記録
                getSharedPreferences("AppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isFirstLaunch", false)
                        .apply();

                // メイン画面に移動
                Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // ScrollViewとFloatingActionButtonの参照
        ScrollView scrollView = findViewById(R.id.scrollView);
        FloatingActionButton fabScrollToTop = findViewById(R.id.to_top);

        // FloatingActionButtonのクリックリスナー
        fabScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ScrollViewを最上部にスクロール
                scrollView.smoothScrollTo(0, 0);
            }
        });

        // スクロール状態でボタンの表示制御 (Optional)
        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // スクロールが少しでもあった場合、ボタンを表示
                if (scrollY > 100) {
                    fabScrollToTop.show();
                } else {
                    fabScrollToTop.hide();
                }
            }
        });
    }
}
