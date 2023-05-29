package com.example.firebaseemailaccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Call<UserAccount> call;
    private EditText et_email, et_pwd; //회원가입 입력필드
    private Button btn_register; //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount user = new UserAccount(et_email.getText().toString(), et_pwd.getText().toString());
                call = Retrofit_client.getUserApiService().user_register(user);

                call.enqueue(new Callback<UserAccount>() {
                    //콜백 받는 부분
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                        // Community_model result = response.body();
                        if(response.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                            // 회원가입 성공하면 로그인 화면으로 전환
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "회원가입 실패", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
}