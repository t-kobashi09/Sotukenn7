package com.example.app_test;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HelpViewActivity extends AppCompatActivity{
    private ImageView image_home_1 , image_home_2 , image_timer_1 , image_timer_2 , image_calendar;
    private Button zoomButton;
    private float scale = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        zoomButton = findViewById(R.id.zoom_button);
        // ズームボタンをクリックしたときに画像を拡大する処理
//        zoomButton.setOnClickListener(v -> {
//            // スケールを増加させて画像を拡大
//            scale += 0.2f; // 0.2倍ずつ拡大
//
//            // 画像にスケールを適用
//            image_home_1.setScaleX(scale);
//            image_home_1.setScaleY(scale);
//        });

        image_home_1 = findViewById(R.id.image_home_1);
        image_home_2 = findViewById(R.id.image_home_2);
        image_timer_1 = findViewById(R.id.image_timer_1);
        image_timer_2 = findViewById(R.id.image_timer_2);
        image_calendar = findViewById(R.id.image_calendar);

        image_home_1.setOnClickListener(v -> showZoomImage(R.drawable.help_home_1));
        image_home_2.setOnClickListener(v -> showZoomImage(R.drawable.help_home_2));
        image_timer_1.setOnClickListener(v -> showZoomImage(R.drawable.help_timer_1));
        image_timer_2.setOnClickListener(v -> showZoomImage(R.drawable.help_timer_2));
        image_calendar.setOnClickListener(v -> showZoomImage(R.drawable.help_calendar));


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

    //画像タップ時
    private void showZoomImage(int imageResId) {
        Toast.makeText(this, "画像ID:" + imageResId, Toast.LENGTH_SHORT).show();
        //ズームボタン表示
        zoomButton.setVisibility(View.VISIBLE);
        // ズームボタンをクリックしたときに画像を拡大する処理
        zoomButton.setOnClickListener(v -> {
            // スケールを増加させて画像を拡大
            scale += 0.2f; // 0.2倍ずつ拡大
            if(imageResId == 1){
                // 画像にスケールを適用
//            imageResId.setScaleX(scale);
//            imageResId.setScaleY(scale);

            }

        });
//        // ダイアログで拡大表示
//        Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_image_zoom);
//        ImageView zoomImageView = dialog.findViewById(R.id.zoomImageView);
//        zoomImageView.setImageResource(imageResId);
//        dialog.show();
    }

    //ホーム画面遷移
    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
