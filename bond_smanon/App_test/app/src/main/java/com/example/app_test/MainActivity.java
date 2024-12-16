package com.example.app_test;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.widget.Button;
import android.os.Looper;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;

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
    //パーミッション関連
    private static final int REQUEST_PERMISSION = 1000;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private static String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // カメラ起動ボタン
        Button btnCapture = findViewById(R.id.button_camera);
        btnCapture.setOnClickListener(v -> {
            if (checkAndRequestCameraPermission()) {
                openCamera();
            }
        });

        //ヘルプボタン処理
        // LinearLayout と FloatingActionButton の参照を取得
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        // FloatingActionButton のクリックイベントを設定
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // コンテンツを動的に追加
                addContent(linearLayout);
                // LinearLayout を表示
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

    //メモボタンの設定
        Button memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> {
            // メモの内容をポップアップで表示
            showMemoDialog();
        });

        // 記録ボタンの設定
        Button recordButton = findViewById(R.id.button_record); // 記録ボタンを取得
        recordButton.setOnClickListener(v -> {
            navigateToRecordActivity();
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

    }
    private void addContent(LinearLayout linearLayout) {
        // サンプル画像とテキストを追加（交互に）
        int[] imageResources = {
                R.drawable.milk_1, // 最初の画像リソース
                R.drawable.milk_2, // 2番目の画像リソース
                R.drawable.milk_3  // 3番目の画像リソース
        };

        String[] texts = {
                "これは1枚目の画像の説明文です。",
                "これは2枚目の画像の説明文です。",
                "これは3枚目の画像の説明文です。"
        };

        // リソースをもとに要素を追加
        for (int i = 0; i < imageResources.length; i++) {
            // 画像の追加
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400 // 高さを固定（必要に応じて調整）
            ));
            imageView.setImageResource(imageResources[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            linearLayout.addView(imageView);

            // テキストの追加
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(texts[i]);
            textView.setTextSize(16);
            textView.setPadding(0, 16, 0, 16); // 上下にスペースを追加
            linearLayout.addView(textView);
        }
    }


    //権限確認
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//    Toast.makeText(this, requestCode + "権限" + grantResults[0], Toast.LENGTH_SHORT).show();
        if (requestCode == 101) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "カメラ使用許可", Toast.LENGTH_SHORT).show();
                openCamera();
            }
//        Toast.makeText(this, "*" + (Manifest.permission.CAMERA) , Toast.LENGTH_SHORT).show();
//
        }else if(requestCode == 1000){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //再チェック
                if(ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }

                //位置情報追跡開始
                _fusedLocationClient.requestLocationUpdates(_locationRequest , _onUpdateLocation , Looper.getMainLooper());
            }

        }
    }

    // 日時取得
    @SuppressLint("SimpleDateFormat")
    private String getCurrentDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(calendar.getTime());
    }

    //カメラ関連
    // 実行時パーミッションの確認とリクエスト
    private boolean checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
            return false;
        }

        // Android 6.0以上の場合はストレージ権限も確認
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }
    // カメラ起動
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(this, "openCamera", Toast.LENGTH_SHORT).show();
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "カメラが利用できません", Toast.LENGTH_SHORT).show();
        }
    }

    //カメラ関連ここまで

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

    // 時間を「時:分:秒」の形式にフォーマット
    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) (elapsedMillis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    //スタートボタン押下時
    public void onStart(View v) {
        // タイマー画面に遷移
        Intent intent = new Intent(MainActivity.this, TimerActivity.class);
        startActivity(intent); // TimerActivityに遷移
    }


    //画面遷移
    private void navigateToRecordActivity() {
        Intent intent = new Intent(this, CalendarActivity.class);
        intent.putExtra("elapsed_time", elapsedTime);
        intent.putExtra("selected_date", getCurrentDate());
        startActivity(intent);
        finish();
    }

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