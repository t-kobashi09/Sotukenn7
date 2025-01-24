package com.example.app_test;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TutorialActivity extends AppCompatActivity {
    private ImageView image_home_1 , image_home_2 , image_timer_1 , image_timer_2 , image_calendar;
    private float scale = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        image_home_1 = findViewById(R.id.image_home_1);
        image_home_2 = findViewById(R.id.image_home_2);
        image_timer_1 = findViewById(R.id.image_timer_1);
        image_timer_2 = findViewById(R.id.image_timer_2);
        image_calendar = findViewById(R.id.image_calendar);

        // 各ImageViewのクリックリスナー
        image_home_1.setOnClickListener(v -> showZoomImage(R.drawable.help_home_1));
        image_home_2.setOnClickListener(v -> showZoomImage(R.drawable.help_home_2));
        image_timer_1.setOnClickListener(v -> showZoomImage(R.drawable.help_timer_1));
        image_timer_2.setOnClickListener(v -> showZoomImage(R.drawable.help_timer_2));
        image_calendar.setOnClickListener(v -> showZoomImage(R.drawable.help_calendar));


        // チュートリアル完了ボタンのクリックリスナー
        findViewById(R.id.btn_finish_tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // チュートリアル完了を記録
                //展示用の場合49~52をコメントアウト
//                getSharedPreferences("AppPreferences", MODE_PRIVATE)
//                        .edit()
//                        .putBoolean("isFirstLaunch", false)
//                        .apply();

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

    private void showZoomImage(int imageResId) {
        // Dialogを作成
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_image_zoom); // カスタムレイアウトを設定
        ImageView dialogImageView = dialog.findViewById(R.id.dialog_image_view);

        FloatingActionButton zoomInButton = dialog.findViewById(R.id.zoom_in_button);
        FloatingActionButton zoomOutButton = dialog.findViewById(R.id.zoom_out_button);
        FloatingActionButton closeButton = dialog.findViewById(R.id.close_button);

        // 画像を設定
        dialogImageView.setImageResource(imageResId);

        // 初期サイズを設定
        scale = 1f;
        dialogImageView.setScaleX(scale);
        dialogImageView.setScaleY(scale);

        // 画像拡大
        zoomInButton.setOnClickListener(v -> {
            scale += 0.2f; // 拡大倍率を増加
            if (scale > 3f) scale = 3f; // 最大スケール制限
            dialogImageView.setScaleX(scale);
            dialogImageView.setScaleY(scale);
        });

        // 画像縮小
        zoomOutButton.setOnClickListener(v -> {
            scale -= 0.2f; // 拡大倍率を増加
            if (scale < 1f) scale = 1f; // 最大スケール制限
            dialogImageView.setScaleX(scale);
            dialogImageView.setScaleY(scale);
        });

        // 閉じるボタンのクリックリスナー
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Dialogを表示
        dialog.show();
    }
}
