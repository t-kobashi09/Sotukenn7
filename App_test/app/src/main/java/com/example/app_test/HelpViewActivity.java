package com.example.app_test;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HelpViewActivity extends AppCompatActivity{
    private FrameLayout zoomContainer;
    private ImageView zoomImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView imageView = findViewById(R.id.imageView);
        zoomContainer = findViewById(R.id.zoomContainer);
        zoomImageView = findViewById(R.id.zoomImageView);

        // 画像クリックで拡大表示
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showZoomImage(R.drawable.sample_image); // 拡大する画像を指定
            }
        });

        // 拡大画像クリックで閉じる
        zoomContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideZoomImage();
            }
        });


        // ボタンの設定
        ImageButton button_home = findViewById(R.id.button_home);
        button_home.setOnClickListener(v -> {
            navigateToHome();
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

    //画像拡大表示
    private void showZoomImage(int imageResId) {
        zoomImageView.setImageResource(imageResId); // 拡大画像を設定
        zoomContainer.setVisibility(View.VISIBLE); // 拡大画像を表示
    }

    private void hideZoomImage() {
        zoomContainer.setVisibility(View.GONE); // 拡大画像を非表示
    }

    //ホーム画面遷移
    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
