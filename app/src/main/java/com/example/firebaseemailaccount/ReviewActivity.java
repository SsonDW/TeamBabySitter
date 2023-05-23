package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Button saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(v -> {
            // 저장 로직을 여기에 추가

            // 이전 액티비티로 돌아가기 위해 현재 액티비티 종료
            finish();
        });

    }
}
