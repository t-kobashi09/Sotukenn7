package com.example.calendarapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Chronometer chronometer;
    private long elapsedTime = 0; // 経過時間を保持
    private boolean isChronometerRunning = false; // 計測中かどうかを管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Chronometerの取得
        chronometer = findViewById(R.id.c);

        // Chronometerの初期設定
        chronometer.setText(formatElapsedTime(0)); // 初期値を「00:00:00」に設定
        chronometer.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setText(formatElapsedTime(elapsedMillis)); // フォーマットした時間をセット
        });
    }

    public void onStart(View v) {
        if (!isChronometerRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
            chronometer.start();
            isChronometerRunning = true;
        }
    }

    public void onStop(View v) {
        if (isChronometerRunning) {
            chronometer.stop();
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isChronometerRunning = false;
        }
        showSharePopup();
    }

    private void showSharePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("シェアしますか？");

        builder.setPositiveButton("シェア", (dialog, which) -> {
            openImagePicker();
        });

        builder.setNegativeButton("キャンセル", (dialog, which) -> {
            navigateToRecordActivity();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static final int REQUEST_IMAGE_PICK = 1;

    @SuppressLint("IntentReset")
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                shareImageToInstagram(selectedImageUri);
            } else {
                Toast.makeText(this, "画像を選択できませんでした", Toast.LENGTH_SHORT).show();
                navigateToRecordActivity();
            }
        } else {
            navigateToRecordActivity();
        }
    }

    private void shareImageToInstagram(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setPackage("com.instagram.android");

        try {
            startActivityForResult(shareIntent, 123);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Instagramがインストールされていません", Toast.LENGTH_SHORT).show();
            navigateToRecordActivity();
        }
    }

    private void navigateToRecordActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("elapsed_asfdtime", elapsedTime);
        intent.putExtra("selected_date", getCurrentDate());
        startActivity(intent);
        finish();
    }

    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());
    }

    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) (elapsedMillis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
