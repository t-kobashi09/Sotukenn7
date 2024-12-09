package com.example.app_test;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // メモボタンの設定
        Button memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> {
            // メモの内容をポップアップで表示
            showMemoDialog();
        });

        // マップボタンの設定
        // FusedLocationProviderClientのインスタンスを作成
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // LocationRequestを作成
        _locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateIntervalMillis(5000) // 最短更新間隔
                .build();
        //位置情報変更時処理、コールバックオブジェクト生成
        _onUpdateLocation = new OnUpdateLocation();

        // 記録ボタンの設定
        Button recordButton = findViewById(R.id.button_record);
        recordButton.setOnClickListener(v -> {
            // 記録画面に遷移
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

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

    public LocationRequest get_locationRequest() {
        return _locationRequest;
    }

    public void set_locationRequest(LocationRequest _locationRequest) {
        this._locationRequest = _locationRequest;
    }

    public FusedLocationProviderClient get_fusedLocationClient() {
        return _fusedLocationClient;
    }

    public void set_fusedLocationClient(FusedLocationProviderClient _fusedLocationClient) {
        this._fusedLocationClient = _fusedLocationClient;
    }

    public OnUpdateLocation get_onUpdateLocation() {
        return _onUpdateLocation;
    }

    public void set_onUpdateLocation(OnUpdateLocation _onUpdateLocation) {
        this._onUpdateLocation = _onUpdateLocation;
    }

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
                    //TextViewに表示
//                    TextView tvLatitude = findViewById(R.id.tvLatitude);
//                    tvLatitude.setText(Double.toString(_latitude));
//                    TextView tvLongitude = findViewById(R.id.tvLongitude);
//                    tvLongitude.setText(Double.toString(_longitude));
                }

            }


        }
    }



}