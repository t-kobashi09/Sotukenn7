package com.example.app_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private long elapsedTime = 0;
    private boolean isChronometerRunning = false;
    private double _latitude = 0;
    private double _longitude = 0;
    private FusedLocationProviderClient _fusedLocationClient;
    private LocationRequest _locationRequest;
    private OnUpdateLocation _onUpdateLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // メモボタンの設定
        Button memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> showMemoDialog());

        // タイマー機能の設定
        chronometer = findViewById(R.id.chron_text);
        chronometer.setText(formatElapsedTime(0)); // 初期値を「00:00:00」に設定
        chronometer.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setText(formatElapsedTime(elapsedMillis));
        });

        // 位置情報追跡の設定
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        _locationRequest = LocationRequest.create();
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        _locationRequest.setInterval(5000);  // 5秒ごとに更新
        _locationRequest.setFastestInterval(2000);  // 最短2秒ごとに更新
        _onUpdateLocation = new OnUpdateLocation();

        // カメラ起動ボタンの設定
        Button cameraButton = findViewById(R.id.cameraButton); // cameraButtonのIDを使用
        cameraButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openCamera();
            }
        });
    }

    // 位置情報追跡開始
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 1000);
            return;
        }
        _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
    }

    // 位置情報追跡終了
    @Override
    protected void onPause() {
        super.onPause();
        _fusedLocationClient.removeLocationUpdates(_onUpdateLocation);
    }

    // パーミッションリクエストの結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, Looper.getMainLooper());
                }
            }
        }

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "カメラの権限が必要です", Toast.LENGTH_SHORT).show();
            }
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

    // スタートボタン押下時
    public void onStart(View v) {
        if (!isChronometerRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedTime);
            chronometer.start();
            isChronometerRunning = true;
        }
    }

    // ストップボタン押下時
    public void onStop(View v) {
        if (isChronometerRunning) {
            chronometer.stop();
            elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
            isChronometerRunning = false;
        }
    }

    // メモ表示用ダイアログ
    private void showMemoDialog() {
        String memoContent = "ここにメモの内容が表示されます。";
        new AlertDialog.Builder(this)
                .setTitle("メモ")
                .setMessage(memoContent)
                .setPositiveButton("OK", (dialog, which) -> {})
                .create()
                .show();
    }

    // カメラを起動する
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 100);
        } else {
            Toast.makeText(this, "カメラが利用できません", Toast.LENGTH_SHORT).show();
        }
    }

    // 実行時パーミッションの確認とリクエスト
    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
            return false;
        }
        return true;
    }

    // カメラで撮影した画像を受け取る
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            saveImageToGallery(photo);
        }
    }

    // 撮影した画像を端末に保存する
    private void saveImageToGallery(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyCameraApp");
        }

        try {
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        Toast.makeText(this, "画像が保存されました", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "画像の保存に失敗しました", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "画像の保存に失敗しました: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 位置情報更新時のコールバック
    private class OnUpdateLocation extends LocationCallback {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    _latitude = location.getLatitude();  // getLatitude()で緯度を取得
                    _longitude = location.getLongitude(); // getLongitude()で経度を取得
                }
            }
        }
    }
}
