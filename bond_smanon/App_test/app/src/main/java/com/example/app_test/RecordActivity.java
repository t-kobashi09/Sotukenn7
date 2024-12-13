package com.example.app_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        // Intentから値を取得
        Intent intent = getIntent();
        long elapsedTime = intent.getLongExtra("elapsed_time", 0);

        // ミリ秒を「時:分:秒」の形式に変換
        String formattedTime = formatElapsedTime(elapsedTime);

        // TextViewに表示
        TextView receivedValueTextView = findViewById(R.id.received_value);
        receivedValueTextView.setText("計測時間: " + formattedTime);
    }

    // 時間を「時:分:秒」の形式にフォーマット
    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) (elapsedMillis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
