package com.example.app_test;

import android.Manifest;
import android.annotation.SuppressLint;
<<<<<<< HEAD
import android.content.ActivityNotFoundException;
=======
import android.content.ContentValues;
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.SystemClock;
import android.provider.MediaStore;
import android.widget.Button;
import android.os.Looper;
=======
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

<<<<<<< HEAD
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
=======
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

<<<<<<< HEAD
=======
import java.io.OutputStream;

>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
public class MainActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private long elapsedTime = 0;
    private boolean isChronometerRunning = false;
    private double _latitude = 0;
    private double _longitude = 0;
    private FusedLocationProviderClient _fusedLocationClient;
    private LocationRequest _locationRequest;
    private OnUpdateLocation _onUpdateLocation;
    //リザルト画像
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        EdgeToEdge.enable(this);
=======
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
        setContentView(R.layout.activity_main);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    //メモボタンの設定
        Button memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> showMemoDialog());

<<<<<<< HEAD
    //タイマー機能
        // Chronometerの取得
=======
        // タイマー機能の設定
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
        chronometer = findViewById(R.id.chron_text);
        chronometer.setText(formatElapsedTime(0)); // 初期値を「00:00:00」に設定
        chronometer.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            chronometer.setText(formatElapsedTime(elapsedMillis));
        });

<<<<<<< HEAD
    //マップボタンの設定
        // FusedLocationProviderClientのインスタンスを作成
=======
        // 位置情報追跡の設定
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        _locationRequest = LocationRequest.create();
        _locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        _locationRequest.setInterval(5000);  // 5秒ごとに更新
        _locationRequest.setFastestInterval(2000);  // 最短2秒ごとに更新
        _onUpdateLocation = new OnUpdateLocation();

<<<<<<< HEAD
        // 記録ボタンの設定
//        Button recordButton = findViewById(R.id.button_record);
//        recordButton.setOnClickListener(v -> {
//            // 記録画面に遷移
//            Intent intent = new Intent(MainActivity.this);
//            startActivity(intent);
//        });

=======
        // カメラ起動ボタンの設定
        Button cameraButton = findViewById(R.id.cameraButton); // cameraButtonのIDを使用
        cameraButton.setOnClickListener(v -> {
            if (checkAndRequestPermissions()) {
                openCamera();
            }
        });
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
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

<<<<<<< HEAD
    // 日時取得
    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());
    }


    //スタートボタン押下時
=======
    // スタートボタン押下時
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
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
<<<<<<< HEAD
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
        intent.putExtra("elapsed_time", elapsedTime);
        intent.putExtra("selected_date", getCurrentDate());
        startActivity(intent);
        finish();
=======
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
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

<<<<<<< HEAD

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
=======
    // カメラを起動する
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 100);
        } else {
            Toast.makeText(this, "カメラが利用できません", Toast.LENGTH_SHORT).show();
>>>>>>> ffc6728b2a9c949362c9122ab3dbb7c63c5301aa
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
