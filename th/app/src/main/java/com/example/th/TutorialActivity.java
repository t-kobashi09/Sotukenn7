package com.example.th;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class TutorialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // チュートリアル完了ボタンのクリックリスナー
        findViewById(R.id.btn_finish_tutorial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // チュートリアル完了を記録
                getSharedPreferences("AppPreferences", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isFirstLaunch", false)
                        .apply();

                // メイン画面に移動
                Intent intent = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
