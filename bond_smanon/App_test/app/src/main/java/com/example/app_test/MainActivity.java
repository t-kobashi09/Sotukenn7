package com.example.app_test;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.widget.Button;
import android.os.Looper;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    // 経過時間を保持
    private long elapsedTime = 0;
    // 計測中かどうかを管理
    private boolean isChronometerRunning = false;
    //緯度
    private double _latitude = 0;
    //経度
    private double _longitude = 0;
    //FusedLocationProviderClientオブジェクトフィールド
    private FusedLocationProviderClient _fusedLocationClient;
    //LocationRequestオブジェクトフィールド
    private LocationRequest _locationRequest;
    //位置情報更新処理コールバック
    private OnUpdateLocation _onUpdateLocation;
    //リザルト画像
    private static final int REQUEST_IMAGE_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    //メモボタンの設定
        Button memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> {
            // メモの内容をポップアップで表示
            showMemoDialog();
        });

    //タイマー機能
        // Chronometerの取得
        chronometer = findViewById(R.id.chron_text);

        // Chronometerの初期設定
        chronometer.setText(formatElapsedTime(0)); // 初期値を「00:00:00」に設定
        chronometer.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setText(formatElapsedTime(elapsedMillis)); // フォーマットした時間をセット
        });

    //マップボタンの設定
        // FusedLocationProviderClientのインスタンスを作成
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // LocationRequestを作成
        _locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(5000) // 最短更新間隔
                .build();
        //位置情報変更時処理、コールバックオブジェクト生成
        _onUpdateLocation = new OnUpdateLocation();

        // 記録ボタンの設定
//        Button recordButton = findViewById(R.id.button_record);
//        recordButton.setOnClickListener(v -> {
//            // 記録画面に遷移
//            Intent intent = new Intent(MainActivity.this);
//            startActivity(intent);
//        });

    }

    //位置情報追跡
    @Override
    protected void onResume(){
        super.onResume();

        //ACCESS_FINE_LOCATIONの許可がない場合
        if(ActivityCompat.checkSelfPermission(MainActivity.this
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this , permissions , 1000);
            return;
        }

        //位置情報追跡開始
        _fusedLocationClient.requestLocationUpdates(_locationRequest , _onUpdateLocation , Looper.getMainLooper());
    }

    //位置情報追跡終了
    @Override
    protected void onPause(){
        super.onPause();
        _fusedLocationClient.removeLocationUpdates(_onUpdateLocation);
    }

    //パーミッションダイアログ表示
    @Override
    public void onRequestPermissionsResult(int requestCode , String[] permissions , int[] grantResults){
       //許可選択時
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
           //再チェック
           if(ActivityCompat.checkSelfPermission(MainActivity.this
                   , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
               return;
           }

           //位置情報追跡開始
           _fusedLocationClient.requestLocationUpdates(_locationRequest , _onUpdateLocation , Looper.getMainLooper());
       }
    }

    // 時間を「時:分:秒」の形式にフォーマット
    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) (elapsedMillis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // 日時取得
    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());
    }


    //スタートボタン押下時
    public void onStart(View v) {
        if (!isChronometerRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
            chronometer.start();
            isChronometerRunning = true;
        }
    }

    //ストップボタン押下時
    public void onStop(View v) {
        if (isChronometerRunning) {
            chronometer.stop();
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isChronometerRunning = false;
        }
        //インスタシェア質問/画面遷移ポップアップ表示
        showSharePopup();
    }

//インスタシェア可否質問/画面遷移ポップアップ表示
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

    //画像生成
    @SuppressLint("IntentReset")
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    //画像選択
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

    //インスタ遷移
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

    //画面遷移
    private void navigateToRecordActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("elapsed_asfdtime", elapsedTime);
        intent.putExtra("selected_date", getCurrentDate());
        startActivity(intent);
        finish();
    }
    // 記録画面に遷移する
//    private void navigateToRecordActivity() {
//        Intent intent = new Intent(this, RecordActivity.class);
//        intent.putExtra("elapsed_time", elapsedTime); // 経過時間を渡す
//        startActivity(intent);
//        finish(); // 現在の画面を終了
//    }


    // メモ表示用ダイアログ
    private void showMemoDialog() {
        // メモの内容をここで設定
        String memoContent = "ここにメモの内容が表示されます。";

        // ダイアログを作成
        new AlertDialog.Builder(this)
                .setTitle("メモ")
                .setMessage(memoContent) // メモの内容を設定
                .setPositiveButton("OK", (dialog, which) -> {
                    // OKボタンが押された場合の処理（閉じるだけ）
                })
                .create()
                .show(); // ダイアログを表示
    }


    public void onMapShowCurrentButtonClick(View view){
        //緯度経度をもとにマップアプリと連携するURIを生成
        String uriStr = "geo:" + _latitude + "," + _longitude;
        //URIオブジェクト生成
        Uri uri = Uri.parse(uriStr);
        //Intentオブジェクト生成
        Intent intent = new Intent(Intent.ACTION_VIEW , uri);
        //アクティビティを起動
        startActivity(intent);
    }


    //現在地取得
    private class OnUpdateLocation extends LocationCallback{
        @Override
        public void onLocationResult(LocationResult locationResult){
            if(locationResult != null){
                //現在地位置情報取得
                Location location = locationResult.getLastLocation();

                if(location != null){
                    //経度取得
                    _latitude = location.getLatitude();
                    //緯度取得
                    _longitude = location.getLongitude();
                }
            }
        }



    }



}