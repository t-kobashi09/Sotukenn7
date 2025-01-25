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
    public static final String EXTRA_PREVIOUS_PAGE = "EXTRA_PREVIOUS_PAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_view);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Intentから遷移元の情報を取得
        Intent intent = getIntent();
        int que_page = intent.getIntExtra("que", 0);
        long que_elapsedTime = intent.getLongExtra("que_elapsed_time", 0);
//        Toast.makeText(getApplicationContext(),  "遷移ページ：" + que_page, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(),  "タイマー：" + que_elapsedTime, Toast.LENGTH_SHORT).show();

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
//        button_home.setOnClickListener(v -> navigateToHome());
        // 戻るボタンの設定
        findViewById(R.id.button_home).setOnClickListener(v -> {
//            Toast.makeText(getApplicationContext(),  "遷移ページ：" + que_page, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(),  "タイマー：" + que_elapsedTime, Toast.LENGTH_SHORT).show();

            if(que_page == 1){
                Intent intents = new Intent(this, TimerActivity.class);
                intents.putExtra("elapsed_time", que_elapsedTime);
                intents.putExtra("que", 1);
                startActivity(intents);
                finish();
            }else {
                Intent intents = new Intent(this, MainActivity.class);
                startActivity(intents);
                finish();
            }


        });

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
}
