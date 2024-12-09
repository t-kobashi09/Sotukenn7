package com.example.calendarapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class CalendarActivity extends AppCompatActivity {

    private HashMap<String, String> scheduleData; // スケジュール保存用
    private HashMap<String, Long> timeData; // 時間保存用（elapsedTime）
    private String selectedDate; // 現在選択中の日付
    private SharedPreferences sharedPreferences; // SharedPreferencesのインスタンス

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        // UI要素の取得
        CalendarView calendarView = findViewById(R.id.calendarView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        EditText scheduleEditText = findViewById(R.id.scheduleEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button backButton = findViewById(R.id.backButton);
        TextView receivedValueTextView = findViewById(R.id.received_value);

        // SharedPreferencesを使ってデータを保存
        sharedPreferences = getSharedPreferences("CalendarApp", Context.MODE_PRIVATE);

        // スケジュールデータと時間データの初期化
        scheduleData = new HashMap<>();
        timeData = new HashMap<>();

        // 初期の日付を設定
        selectedDate = getCurrentDate(calendarView);
        dateTextView.setText("Selected Date: " + selectedDate);

        // 保存されているスケジュールと計測時間を読み込み
        loadSavedData();

        // 日付選択時の処理
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // 選択された日付
            selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;

            // 日付を表示
            dateTextView.setText("Selected Date: " + selectedDate);

            // 保存されているスケジュールを表示
            if (scheduleData.containsKey(selectedDate)) {
                scheduleEditText.setText(scheduleData.get(selectedDate));
            } else {
                scheduleEditText.setText(""); // 何もない場合は空欄
            }

            // 保存されている計測時間を表示
            if (timeData.containsKey(selectedDate)) {
                long elapsedTime = timeData.get(selectedDate);
                String formattedTime = formatElapsedTime(elapsedTime);
                receivedValueTextView.setText("計測時間: " + formattedTime);
            } else {
                receivedValueTextView.setText("計測時間: 00:00:00");
            }
        });

        // 保存ボタンの処理
        saveButton.setOnClickListener(v -> {
            // 入力されたスケジュールを取得
            String schedule = scheduleEditText.getText().toString();

            // スケジュールを保存
            scheduleData.put(selectedDate, schedule);

            // 計測時間を保存（加算処理）
            long currentElapsedTime = timeData.getOrDefault(selectedDate, 0L);
            timeData.put(selectedDate, currentElapsedTime);

            // SharedPreferencesに保存
            saveData();

            // 保存完了の通知
            Toast.makeText(CalendarActivity.this, "Schedule saved for " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        // 戻るボタンの処理
        backButton.setOnClickListener(v -> finish());

        // タイマー
        // Intentから値を取得
        Intent intent = getIntent();
        long elapsedTime = intent.getLongExtra("elapsed_time", 0);

        // 計測時間を加算（既存の時間があれば加算）
        long existingTime = timeData.getOrDefault(selectedDate, 0L);
        long newTime = existingTime + elapsedTime;
        timeData.put(selectedDate, newTime);

        // 計測時間をSharedPreferencesに保存
        saveData();

        // ミリ秒を「時:分:秒」の形式に変換
        String formattedTime = formatElapsedTime(newTime);

        // TextViewに表示
        receivedValueTextView.setText("計測時間: " + formattedTime);
    }

    // SharedPreferencesにデータを保存するメソッド
    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // スケジュールデータを保存
        for (String date : scheduleData.keySet()) {
            editor.putString(date + "_schedule", scheduleData.get(date));
        }
        // 計測時間データを保存
        for (String date : timeData.keySet()) {
            editor.putLong(date + "_time", timeData.get(date));
        }
        editor.apply();
    }

    // 保存されたデータを読み込むメソッド
    private void loadSavedData() {
        // スケジュールデータを読み込む
        for (String date : sharedPreferences.getAll().keySet()) {
            if (date.endsWith("_schedule")) {
                String dateKey = date.replace("_schedule", "");
                scheduleData.put(dateKey, sharedPreferences.getString(date, ""));
            }
        }

        // 計測時間データを読み込む
        for (String date : sharedPreferences.getAll().keySet()) {
            if (date.endsWith("_time")) {
                String dateKey = date.replace("_time", "");
                timeData.put(dateKey, sharedPreferences.getLong(date, 0));
            }
        }
    }

    // カレンダーの初期日付を取得
    private String getCurrentDate(CalendarView calendarView) {
        long currentDate = calendarView.getDate();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentDate);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1; // 月は0から始まる
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return year + "/" + month + "/" + day;
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
