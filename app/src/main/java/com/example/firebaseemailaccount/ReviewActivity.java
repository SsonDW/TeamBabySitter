package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    private EditText editText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //editText = findViewById(R.id.editText1);

        Button saveButton = findViewById(R.id.save_btn);
        saveButton.setOnClickListener(v -> {
            // 작성한 글 가져오기
            String text = editText.getText().toString();

            // 저장 로직을 여기에 추가

            // 이전 액티비티로 돌아가기 위해 현재 액티비티 종료
            // 이전 액티비티에 데이터 전달하기 위해 인텐트 사용
            Intent intent = new Intent();
            intent.putExtra("Name", text);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}