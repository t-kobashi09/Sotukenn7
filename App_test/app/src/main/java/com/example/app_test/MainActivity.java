package com.example.app_test;

import android.Manifest;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_test.model.Shop;

import java.util.ArrayList;
import java.util.List;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
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
    private EditText etSearch;
    private ImageButton btnSearch;
    private ImageButton btnExecuteSearch, btnCloseSearch;
    private TextView tvSearchCount, tvResults;
    private LinearLayout llSearchBar;

    private List<Shop> shops; // ショップデータを格納するリスト
    private int searchCount = 10; // 検索可能回数（初期値）
    private boolean isSearchBarVisible = false; // 検索バーの表示状態を管理

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
    // ヘルプ画面表示状態の管理
    private boolean isVisible = false;

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
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);

//チュートリアル画面関連
// 初回起動かどうかを判定
        boolean isFirstLaunch = getSharedPreferences("AppPreferences", MODE_PRIVATE)
                .getBoolean("isFirstLaunch", true);

        if (isFirstLaunch) {
            // チュートリアル画面に移動
            Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(intent);
            finish(); // メインアクティビティを終了
        } else {
            // 通常のメイン画面を表示
            setContentView(R.layout.activity_main);
        }
//チュートリアル画面関連ここまで

        //検索機能
        // UI要素の初期化
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnExecuteSearch = findViewById(R.id.btnExecuteSearch);
        btnCloseSearch = findViewById(R.id.btnCloseSearch);
        tvSearchCount = findViewById(R.id.tvSearchCount);
        tvResults = findViewById(R.id.tvResults);
        llSearchBar = findViewById(R.id.llSearchBar);

        initializeData(); // 初期データをセット

        // ボタンのクリックリスナーを設定
        btnSearch.setOnClickListener(v -> toggleSearchBar()); // 検索バー表示/非表示切替
        btnExecuteSearch.setOnClickListener(v -> executeSearch()); // 検索実行
        btnCloseSearch.setOnClickListener(v -> hideSearchBar()); // 検索バーを隠す
        btnSearch.setOnLongClickListener(v -> showSearchCount()); // 検索回数の長押し表示

        // アクションバーを非表示にする
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //公式インスタ遷移
        // TextViewを取得
        ImageButton textView = findViewById(R.id.spot_view);

//         クリックイベントを設定
        textView.setOnClickListener(v -> {
            // 外部ブラウザでURLを開く
            String url = "https://www.instagram.com/s/aGlnaGxpZ2h0OjE4MDUzNzI4OTI0NzYzNzM1?story_media_id=3503694777097793936&igsh=MTNkamxwb2FrcWx4eQ==";
//            String url = "https://www.canva.com/design/DAGYeq50wO8/jtEXSJ7QwkHZmjdOOcR-sg/view?utm_content=DAGYeq50wO8&utm_campaign=designshare&utm_medium=link&utm_source=editor";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        // ヘルプボタンの設定
        ImageButton helpButton = findViewById(R.id.help_Button);
        helpButton.setOnClickListener(v -> {
            navigateToHelpViewActivity();
        });


        // カメラ起動ボタン
        ImageButton btnCapture = findViewById(R.id.button_camera);

        // カメラアプリ起動ボタン
        btnCapture.setOnClickListener(v -> {
            // カメラのパーミッションが許可されているか確認
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCameraApp(); // 許可されていればカメラを起動
            } else {
                // 許可されていない場合、パーミッションリクエスト
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        });
        //        btnCapture.setOnClickListener(v -> {
//            if (checkAndRequestCameraPermission()) {
//                openCamera();
//            }
//        });

        // 開始ボタン
        Button btnStart = findViewById(R.id.button_start);
        btnStart.setOnClickListener(v -> {
            timerStart();
        });


    //メモボタンの設定
        ImageButton memoButton = findViewById(R.id.button_memo);
        memoButton.setOnClickListener(v -> {
            // メモの内容をポップアップで表示
            showMemoDialog();
        });

        // 記録ボタンの設定
        ImageButton recordButton = findViewById(R.id.button_record); // 記録ボタンを取得
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


    //権限確認
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    Toast.makeText(this, requestCode + "権限" + grantResults[0], Toast.LENGTH_SHORT).show();
        if (requestCode == 101) {
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "カメラ使用許可", Toast.LENGTH_SHORT).show();
                openCameraApp();
//                openCamera();
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
    // カメラアプリを起動
    private void openCameraApp() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // カメラアプリがインストールされているか確認
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent); // カメラアプリを起動
        } else {
            Toast.makeText(this, "カメラアプリが見つかりません", Toast.LENGTH_SHORT).show();
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
    public void timerStart() {
        // タイマー画面に遷移
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent); // TimerActivityに遷移
    }

    //ヘルプ画面遷移
    private void navigateToHelpViewActivity() {
        Intent intent = new Intent(this, HelpViewActivity.class);
        startActivity(intent);
        finish();
    }

    //記録画面遷移
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

    // ショップデータを初期化
    private void initializeData() {
        shops = new ArrayList<>();

        // 食事ジャンル
        shops.add(new Shop("食事", "岡山うどん", "カジュアル", "地元の素材を使った美味しいうどん", "岡山市北区", 34.6618, 133.9344, List.of("うどん", "麺類", "食事", "地元料理", "グルメ")));
        shops.add(new Shop("食事", "岡山寿司屋", "高級", "新鮮な魚を使った寿司を提供するお店", "岡山市北区", 34.6635, 133.9294, List.of("寿司", "魚", "和食", "グルメ", "高級")));
        shops.add(new Shop("食事", "岡山ラーメン", "カジュアル", "岡山のご当地ラーメンを楽しめるお店", "岡山市南区", 34.6419, 133.9197, List.of("ラーメン", "食事", "地元料理", "グルメ", "カジュアル")));
        shops.add(new Shop("食事", "岡山ステーキハウス", "高級", "豪華なステーキを提供する高級店", "岡山市北区", 34.6605, 133.9341, List.of("ステーキ", "肉料理", "グルメ", "高級")));
        shops.add(new Shop("食事", "岡山天ぷら屋", "カジュアル", "サクサクの天ぷらを楽しめるお店", "岡山市北区", 34.6630, 133.9350, List.of("天ぷら", "和食", "食事", "カジュアル")));
        shops.add(new Shop("食事", "岡山カフェ", "カジュアル", "オーガニックコーヒーと軽食のカフェ", "岡山市南区", 34.6403, 133.9175, List.of("カフェ", "コーヒー", "食事", "軽食")));
        shops.add(new Shop("食事", "岡山和食屋", "高級", "上質な和食が楽しめる店", "岡山市北区", 34.6640, 133.9300, List.of("和食", "高級", "食事")));
        shops.add(new Shop("食事", "岡山焼肉店", "カジュアル", "地元産のお肉を使った焼肉店", "岡山市北区", 34.6550, 133.9300, List.of("焼肉", "肉料理", "食事")));
        shops.add(new Shop("食事", "岡山カレー屋", "カジュアル", "スパイシーなカレーを提供するお店", "岡山市南区", 34.6430, 133.9200, List.of("カレー", "食事", "スパイシー")));
        shops.add(new Shop("食事", "岡山ビストロ", "カジュアル", "フランス料理をカジュアルに楽しめるお店", "岡山市北区", 34.6615, 133.9310, List.of("フランス料理", "ビストロ", "カジュアル")));

// お土産ジャンル
        shops.add(new Shop("お土産", "岡山名物マスカット", "スイーツ", "岡山の特産品を使ったスイーツ", "岡山市南区", 34.6485, 133.9161, List.of("マスカット", "スイーツ", "お土産", "果物")));
        shops.add(new Shop("お土産", "きびだんご", "スイーツ", "岡山名物きびだんごが楽しめる店", "岡山市北区", 34.6618, 133.9344, List.of("きびだんご", "スイーツ", "お土産", "伝統")));
        shops.add(new Shop("お土産", "岡山バラエティショップ", "雑貨", "岡山のお土産を豊富に取り扱うショップ", "岡山市北区", 34.6615, 133.9300, List.of("雑貨", "お土産", "岡山", "ショップ")));
        shops.add(new Shop("お土産", "岡山デザイン雑貨店", "雑貨", "ユニークなデザインの岡山土産を提供する店", "岡山市南区", 34.6467, 133.9101, List.of("雑貨", "デザイン", "お土産")));
        shops.add(new Shop("お土産", "岡山茶屋", "食品", "岡山産の茶葉を使ったお土産茶葉", "岡山市北区", 34.6600, 133.9300, List.of("お茶", "茶葉", "お土産", "飲料")));
        shops.add(new Shop("お土産", "倉敷陶器店", "陶器", "倉敷で作られた陶器を取り扱う店", "倉敷市", 34.5888, 133.7728, List.of("陶器", "倉敷", "お土産", "伝統")));
        shops.add(new Shop("お土産", "岡山お菓子屋", "スイーツ", "岡山特産のスイーツを販売するお店", "岡山市南区", 34.6450, 133.9150, List.of("お菓子", "スイーツ", "お土産")));
        shops.add(new Shop("お土産", "岡山ジュエリーショップ", "アクセサリー", "岡山産のジュエリーを取り扱うショップ", "岡山市北区", 34.6625, 133.9290, List.of("ジュエリー", "アクセサリー", "お土産")));
        shops.add(new Shop("お土産", "岡山観光土産店", "雑貨", "観光名所の関連商品を販売するお土産店", "岡山市北区", 34.6610, 133.9315, List.of("観光", "雑貨", "お土産")));

// 観光施設ジャンル
        shops.add(new Shop("観光施設", "岡山城", "歴史的観光地", "岡山の歴史を感じる名城", "岡山市北区", 34.6617, 133.9352, List.of("岡山城", "観光地", "歴史", "文化")));
        shops.add(new Shop("観光施設", "後楽園", "庭園", "日本三名園の一つ、広大な庭園でリラックス", "岡山市北区", 34.6544, 133.9367, List.of("後楽園", "庭園", "観光地", "自然")));
        shops.add(new Shop("観光施設", "西大寺", "寺院", "岡山の古刹、西大寺で心を癒す", "岡山市東区", 34.6946, 133.9900, List.of("寺院", "歴史", "観光地")));
        shops.add(new Shop("観光施設", "岡山文化村", "文化施設", "地元のアートを楽しむ文化施設", "岡山市東区", 34.6750, 133.9800, List.of("アート", "文化施設", "観光地")));
        shops.add(new Shop("観光施設", "倉敷美観地区", "歴史的観光地", "伝統的な街並みが残る観光地", "倉敷市", 34.5895, 133.7730, List.of("歴史", "観光地", "美観地区")));
        shops.add(new Shop("観光施設", "吉備路文学館", "文学施設", "吉備の歴史と文学を学べる施設", "岡山市北区", 34.6715, 133.9330, List.of("文学", "歴史", "文化施設")));
        shops.add(new Shop("観光施設", "吉備津神社", "神社", "岡山の古代信仰を学べる神社", "岡山市北区", 34.6620, 133.9315, List.of("神社", "歴史", "観光地")));
        shops.add(new Shop("観光施設", "岡山動物園", "動物園", "多彩な動物たちと触れ合える場所", "岡山市北区", 34.6505, 133.9300, List.of("動物園", "観光地", "家族")));
        shops.add(new Shop("観光施設", "鷲羽山ハイランド", "遊園地", "絶景を楽しめる遊園地", "倉敷市", 34.5850, 133.7410, List.of("遊園地", "観光地", "絶景")));

// イベントジャンル
        shops.add(new Shop("イベント", "岡山花火大会", "年次イベント", "夏の風物詩、迫力のある花火が楽しめる", "岡山市", 34.6618, 133.9344, List.of("花火", "イベント", "夏祭り", "年次イベント")));
        shops.add(new Shop("イベント", "岡山音楽祭", "音楽イベント", "地域のアーティストが集まる音楽フェスティバル", "岡山市", 34.6618, 133.9344, List.of("音楽", "イベント", "フェスティバル", "地域")));
        shops.add(new Shop("イベント", "岡山マラソン", "スポーツイベント", "毎年開催されるマラソン大会", "岡山市", 34.6618, 133.9344, List.of("マラソン", "スポーツ", "イベント", "年次イベント")));
        shops.add(new Shop("イベント", "岡山ビール祭り", "グルメイベント", "地元のクラフトビールと美味しい料理を楽しむイベント", "岡山市", 34.6618, 133.9344, List.of("ビール", "グルメ", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山映画祭", "映画イベント", "映画の祭典、様々な映画を上映するイベント", "岡山市", 34.6600, 133.9330, List.of("映画", "イベント", "フェスティバル", "文化")));
        shops.add(new Shop("イベント", "岡山ハロウィン祭り", "地域イベント", "岡山で開催されるハロウィンイベント", "岡山市", 34.6618, 133.9344, List.of("ハロウィン", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山フェスティバル", "年次イベント", "岡山の文化を体験できるイベント", "岡山市", 34.6618, 133.9344, List.of("文化", "イベント", "地域")));
        shops.add(new Shop("イベント", "岡山古本市", "文化イベント", "古本を集めた市場イベント", "岡山市", 34.6618, 133.9344, List.of("本", "イベント", "文化")));
        shops.add(new Shop("イベント", "岡山アートフェア", "アートイベント", "岡山のアーティストが集まるアートイベント", "岡山市", 34.6618, 133.9344, List.of("アート", "イベント", "文化")));
        shops.add(new Shop("イベント", "岡山花の展覧会", "展示イベント", "岡山の美しい花々を展示するイベント", "岡山市", 34.6618, 133.9344, List.of("花", "イベント", "展示")));
    }


    // 入力された検索ワードに関連する類義語を取得
    private List<String> getSynonyms(String query) {
        List<String> synonyms = new ArrayList<>();

        if (query.equals("食事")) {
            synonyms.add("ごはん");
            synonyms.add("ご飯");
            synonyms.add("グルメ");
            synonyms.add("料理");
            synonyms.add("食べ物");
            synonyms.add("ランチ");
            synonyms.add("ディナー");
            synonyms.add("軽食");
            synonyms.add("食事処");
            synonyms.add("飲食");
            synonyms.add("食堂");
            synonyms.add("カフェ");
            synonyms.add("和食");
            synonyms.add("レストラン");
        }
        else if (query.equals("お土産")) {
            synonyms.add("ギフト");
            synonyms.add("記念品");
            synonyms.add("おみやげ");
            synonyms.add("土産物");
            synonyms.add("贈り物");
            synonyms.add("プレゼント");
            synonyms.add("ショップ");
        }
        else if (query.equals("観光施設")) {
            synonyms.add("観光地");
            synonyms.add("名所");
            synonyms.add("観光スポット");
            synonyms.add("旅行先");
            synonyms.add("観光名所");
            synonyms.add("観光地");
            synonyms.add("観光地帯");
        }
        else if (query.equals("イベント")) {
            synonyms.add("祭り");
            synonyms.add("フェスティバル");
            synonyms.add("行事");
            synonyms.add("催し物");
            synonyms.add("活動");
            synonyms.add("イベント会場");
            synonyms.add("特別イベント");
        }

        return synonyms;
    }


    // 検索を実行するメソッド
    private void executeSearch() {
        // 検索回数が残っていない場合はメッセージを表示
        if (searchCount <= 0) {
            tvResults.setText("検索回数が上限に達しました。");
            tvResults.setVisibility(View.VISIBLE);
            return;
        }

        String query = etSearch.getText().toString().trim(); // 検索ワードを取得
        if (query.isEmpty()) { // 検索ワードが空であればエラーメッセージ
            tvResults.setText("検索キーワードを入力してください。");
            tvResults.setVisibility(View.VISIBLE);
            return;
        }

        // 類義語を検索対象に追加
        List<String> searchTerms = new ArrayList<>();
        searchTerms.add(query);
        searchTerms.addAll(getSynonyms(query)); // 類義語も検索ワードに追加

        List<String> results = new ArrayList<>();
        // ショップリストを巡回して検索
        for (Shop shop : shops) {
            for (String term : searchTerms) {
                // 検索ワードとショップ情報が一致する場合
                if (shop.getName().contains(term) || shop.getCategory().contains(term) || shop.getKeywords().contains(term)) {
                    results.add(shop.toString()); // 結果をリストに追加
                    break; // 一度一致したら次の検索語に進む
                }
            }
        }

        // 結果がなければメッセージを表示
        if (results.isEmpty()) {
            tvResults.setText("ヒットしませんでした。");
        } else {
            tvResults.setText("検索結果:\n" + String.join("\n\n", results)); // 検索結果を表示
        }

        tvResults.setVisibility(View.VISIBLE); // 結果表示
        searchCount--; // 検索回数を減らす
        updateSearchCountDisplay(); // 検索回数を更新
    }

    // 検索回数を表示
    private void updateSearchCountDisplay() {
        tvSearchCount.setText("検索可能回数: " + searchCount);
        tvSearchCount.setVisibility(View.GONE); // 長押し時のみ表示
    }

    // 検索バーの表示/非表示を切り替え
    private void toggleSearchBar() {
        if (isSearchBarVisible) {
            hideSearchBar(); // 表示中なら非表示
        } else {
            showSearchBar(); // 非表示なら表示
        }
    }

    // 検索バーを表示
    private void showSearchBar() {
        // アニメーションで検索バーをスライドイン
        TranslateAnimation slideIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slideIn.setDuration(300);
        llSearchBar.startAnimation(slideIn);
        llSearchBar.setVisibility(View.VISIBLE); // 検索バーを表示
        btnSearch.setVisibility(View.GONE); // 検索ボタンを非表示

        isSearchBarVisible = true;
    }

    // 検索バーを非表示
    private void hideSearchBar() {
        // アニメーションで検索バーをスライドアウト
        TranslateAnimation slideOut = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        slideOut.setDuration(200);
        slideOut.setInterpolator(new android.view.animation.DecelerateInterpolator()); // アニメーションのイージング
        llSearchBar.startAnimation(slideOut);
        llSearchBar.setVisibility(View.GONE); // 検索バーを非表示
        btnSearch.setVisibility(View.VISIBLE); // 検索ボタンを再表示

        isSearchBarVisible = false;

        tvResults.setVisibility(View.GONE); // 検索結果を非表示

    }

    // 検索回数を長押しで表示
    private boolean showSearchCount() {
        tvSearchCount.setText("検索可能回数: " + searchCount);
        tvSearchCount.setVisibility(View.VISIBLE); // 表示
        btnSearch.postDelayed(() -> tvSearchCount.setVisibility(View.GONE), 2000); // 2秒後に非表示
        return true; // 長押しアクションを終了
    }

}