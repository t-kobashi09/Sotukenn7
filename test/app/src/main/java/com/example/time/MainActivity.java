package com.example.time;

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

    // スタートボタンの処理
    public void onStart(View v) {
        if (!isChronometerRunning) { // すでに動作中でない場合のみスタート
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
            chronometer.start();
            isChronometerRunning = true;
        }
    }

    // ストップボタンの処理
    public void onStop(View v) {
        if (isChronometerRunning) {
            chronometer.stop();
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isChronometerRunning = false;
        }

        // シェアボタンをポップアップとして表示
        showSharePopup();
    }

    // シェアボタンをポップアップとして表示する
    private void showSharePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("シェアしますか？");

        // ポップアップに「シェア」ボタンを追加
        builder.setPositiveButton("シェア", (dialog, which) -> {
            openImagePicker(); // 画像選択を開始
        });

        // キャンセルボタン
        builder.setNegativeButton("キャンセル", (dialog, which) -> {
            navigateToRecordActivity(); // キャンセル後に記録画面に遷移
        });

        AlertDialog dialog = builder.create();
        dialog.show(); // ポップアップを表示
    }

    // ギャラリーから画像を選択する
    private static final int REQUEST_IMAGE_PICK = 1;

    @SuppressLint("IntentReset")
    private void openImagePicker() {
        @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*"); // 画像のみ選択可能
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                shareImageToInstagram(selectedImageUri); // Instagramにシェア処理
            } else {
                Toast.makeText(this, "画像を選択できませんでした", Toast.LENGTH_SHORT).show();
                navigateToRecordActivity(); // 記録画面に遷移
            }
        } else if (requestCode == 123 && resultCode == RESULT_OK) {  // Instagramから戻ってきた際
            navigateToRecordActivity(); // 記録画面に遷移
        } else {
            navigateToRecordActivity(); // ユーザーが画像選択をキャンセルした場合やエラーの場合
        }
    }

    // Instagramに画像を共有する
    private void shareImageToInstagram(Uri imageUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setPackage("com.instagram.android");

        try {
            startActivityForResult(shareIntent, 123); // Instagramにシェアして戻ってきたときのリクエストコードを設定
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Instagramがインストールされていません", Toast.LENGTH_SHORT).show();
            navigateToRecordActivity(); // Instagramがインストールされていない場合に記録画面に遷移
        }
    }

    // 記録画面に遷移する
    private void navigateToRecordActivity() {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("elapsed_time", elapsedTime); // 経過時間を渡す
        startActivity(intent);
        finish(); // 現在の画面を終了
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
