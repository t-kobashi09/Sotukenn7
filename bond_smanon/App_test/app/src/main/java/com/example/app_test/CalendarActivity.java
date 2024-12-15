package com.example.app_test;


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
//        Button deleteButton = findViewById(R.id.deleteButton); // 追加
//        Button allDataButton = findViewById(R.id.allDataButton); // 追加
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
            selectedDate = year + "/" + (month + 1) + "/" + dayOfMonth;
            dateTextView.setText("Selected Date: " + selectedDate);
            getSelectedDateData(selectedDate, scheduleEditText, receivedValueTextView);
        });

        // 保存ボタンの処理
        saveButton.setOnClickListener(v -> {
            String schedule = scheduleEditText.getText().toString();
            scheduleData.put(selectedDate, schedule);
            long currentElapsedTime = timeData.getOrDefault(selectedDate, 0L);
            timeData.put(selectedDate, currentElapsedTime);
            saveData();
            Toast.makeText(CalendarActivity.this, "Schedule saved for " + selectedDate, Toast.LENGTH_SHORT).show();
        });

        // 戻るボタンの処理
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, MainActivity.class); // MainActivityに置き換え
            startActivity(intent);
            finish();
        });

//        // 削除ボタンの処理（追加）
//        deleteButton.setOnClickListener(v -> {
//            deleteSelectedDateData(selectedDate);
//            scheduleEditText.setText("");
//            receivedValueTextView.setText("計測時間: 00:00:00");
//        });
//
//        // 全データ取得ボタンの処理（追加）
//        allDataButton.setOnClickListener(v -> getAllData());

        // タイマーのデータをインテントから受け取る
        Intent intent = getIntent();
        long elapsedTime = intent.getLongExtra("elapsed_time", 0);
        long existingTime = timeData.getOrDefault(selectedDate, 0L);
        long newTime = existingTime + elapsedTime;
        timeData.put(selectedDate, newTime);
        saveData();
        String formattedTime = formatElapsedTime(newTime);
        receivedValueTextView.setText("計測時間: " + formattedTime);
    }

    // 選択した日付のデータを取得
    @SuppressLint("SetTextI18n")
    private void getSelectedDateData(String date, EditText scheduleEditText, TextView receivedValueTextView) {
        if (scheduleData.containsKey(date)) {
            scheduleEditText.setText(scheduleData.get(date));
        } else {
            scheduleEditText.setText(""); // スケジュールがない場合
        }
        if (timeData.containsKey(date)) {
            long elapsedTime = timeData.get(date);
            String formattedTime = formatElapsedTime(elapsedTime);
            receivedValueTextView.setText("計測時間: " + formattedTime);
        } else {
            receivedValueTextView.setText("計測時間: 00:00:00");
        }
    }

    // 選択した日付のデータを削除（追加）
    private void deleteSelectedDateData(String date) {
        scheduleData.remove(date);
        timeData.remove(date);
        saveData();
        Toast.makeText(this, date + " のデータを削除しました。", Toast.LENGTH_SHORT).show();
    }

    // すべてのデータを取得（追加）
    private void getAllData() {
        StringBuilder allData = new StringBuilder();
        for (String date : scheduleData.keySet()) {
            String schedule = scheduleData.get(date);
            String formattedTime = formatElapsedTime(timeData.getOrDefault(date, 0L));
            allData.append(date).append(": ").append(schedule).append(" / ").append(formattedTime).append("\n");
        }
        Toast.makeText(this, allData.toString(), Toast.LENGTH_LONG).show();
    }

    // SharedPreferencesにデータを保存
    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (String date : scheduleData.keySet()) {
            editor.putString(date + "_schedule", scheduleData.get(date));
        }
        for (String date : timeData.keySet()) {
            editor.putLong(date + "_time", timeData.get(date));
        }
        editor.apply();
    }

    // 保存されたデータを読み込む
    private void loadSavedData() {
        for (String date : sharedPreferences.getAll().keySet()) {
            if (date.endsWith("_schedule")) {
                String dateKey = date.replace("_schedule", "");
                scheduleData.put(dateKey, sharedPreferences.getString(date, ""));
            } else if (date.endsWith("_time")) {
                String dateKey = date.replace("_time", "");
                timeData.put(dateKey, sharedPreferences.getLong(date, 0));
            }
        }
    }

    private String getCurrentDate(CalendarView calendarView) {
        long currentDate = calendarView.getDate();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentDate);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return year + "/" + month + "/" + day;
    }

    @SuppressLint("DefaultLocale")
    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis % 3600000) / 60000;
        int seconds = (int) (elapsedMillis % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}