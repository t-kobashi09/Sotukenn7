package com.example.app_test;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
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

        //公式インスタ遷移
        // TextViewを取得
        TextView textView = findViewById(R.id.textView);

        // クリックイベントを設定
        textView.setOnClickListener(v -> {
            // 外部ブラウザでURLを開く
            String url = "https://www.instagram.com/s/aGlnaGxpZ2h0OjE4MDUzNzI4OTI0NzYzNzM1?story_media_id=3503694777097793936&igsh=MTNkamxwb2FrcWx4eQ==";
//            String url = "https://www.canva.com/design/DAGYeq50wO8/jtEXSJ7QwkHZmjdOOcR-sg/view?utm_content=DAGYeq50wO8&utm_campaign=designshare&utm_medium=link&utm_source=editor";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

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
                // 表示・非表示を切り替え
                if (linearLayout.getVisibility() == View.VISIBLE) {
                    // 現在表示されている場合は非表示にする
                    linearLayout.setVisibility(View.GONE);
                } else {
                    // 現在非表示の場合は表示する
                    if (linearLayout.getChildCount() == 0) {
                        // コンテンツがまだ追加されていない場合のみ追加
                        addContent(linearLayout);
                    }
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.bringToFront(); // 最前面に移動
                    linearLayout.invalidate();  // レイアウトを再描画
                }
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
                R.drawable.instruct_1, // ホーム画面説明
                R.drawable.instruct_2, // タイマー画面説明
                R.drawable.instruct_3  // 記録画面説明
        };

        String[] texts = {
                "スタートボタンを押してスマのん時間を始めてみましょう！",
                "タイマーであなたのスマのん時間が計測されます。スマのん時間が終わったらENDボタンを押してInstagramで共有してみましょう！",
                "今日のスマのん時間がカレンダーと一緒に閲覧できます。"
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
        Intent intent = new Intent(this, TimerActivity.class);
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
        // メモの内容を表示する EditText を作成
        EditText memoEditText = new EditText(this);
        memoEditText.setText(getSavedMemo());  // 既存のメモがあれば表示

        // ダイアログを作成
        new AlertDialog.Builder(this)
                .setTitle("メモ")
                .setView(memoEditText)  // EditTextをダイアログに追加
                .setPositiveButton("保存", (dialog, which) -> {
                    // 保存ボタンが押された場合の処理
                    saveMemo(memoEditText.getText().toString()); // 入力内容を保存
                })
                .setNegativeButton("キャンセル", (dialog, which) -> {
                    // キャンセルボタンが押された場合の処理（何もしない）
                })
                .create()
                .show(); // ダイアログを表示
    }

    // メモの保存
    private void saveMemo(String memo) {
        SharedPreferences sharedPreferences = getSharedPreferences("memo_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("memo", memo);  // メモを保存
        editor.apply();  // 保存を確定
    }

    // 保存されたメモを取得
    private String getSavedMemo() {
        SharedPreferences sharedPreferences = getSharedPreferences("memo_pref", MODE_PRIVATE);
        return sharedPreferences.getString("memo", "");  // 保存されたメモを取得、なければ空文字
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