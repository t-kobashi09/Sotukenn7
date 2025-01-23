package com.example.app_test;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HelpViewActivity extends AppCompatActivity{
    private ImageView image_home_1 , image_home_2 , image_timer_1 , image_timer_2 , image_calendar;
    private float scale = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

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

        // ボタンの設定
        ImageButton button_home = findViewById(R.id.button_home);
        button_home.setOnClickListener(v -> navigateToHome());

        // ScrollViewとFloatingActionButtonの参照
        ScrollView scrollView = findViewById(R.id.scrollView);
        FloatingActionButton fabScrollToTop = findViewById(R.id.to_top);

        // FloatingActionButtonのクリックリスナー
        fabScrollToTop.setOnClickListener(v -> {
            // ScrollViewを最上部にスクロール
            scrollView.smoothScrollTo(0, 0);
        });

        // スクロール状態でボタンの表示制御 (Optional)
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > 100) {
                fabScrollToTop.show();
            } else {
                fabScrollToTop.hide();
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


    //ホーム画面遷移
    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
