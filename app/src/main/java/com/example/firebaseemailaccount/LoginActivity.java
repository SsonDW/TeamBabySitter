package com.example.firebaseemailaccount;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public int id;
    Call<UserAccount> call;
    private EditText et_email, et_pwd; //로그인 입력필드
    private Button btn_login; // 로그인 버튼
    private CheckBox autoLogin; // 자동 로그인 체크박스

    public static SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);
        autoLogin = findViewById(R.id.autoLogin);

        // ---------------- 이전에 로그인한 정보 저장했었는지 확인 ----------------
        sharedPref = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String email = sharedPref.getString("email", null);
        String password = sharedPref.getString("password", null);

        // 기존에 자동 로그인한 기록이 있다면
        if (email != null && password != null) {
            UserAccount user = new UserAccount(email, password);
            call = Retrofit_client.getUserApiService().user_login(user);

            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    if(response.isSuccessful()){
                        // login 할 때마다 쿠키 값도 달라지니까 자동 로그인이어도 cookie값 새로 저장?
                        // SharedPreferences.Editor editor = sharedPref.edit();
                        // editor.putString("cookie", response.body().getJwt());
                        // editor.apply();

                        Toast.makeText(LoginActivity.this, "자동 로그인 성공", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, PageActivity.class);
                        startActivity(intent); // 화면 전환
                    }else{
                        Toast.makeText(LoginActivity.this, "자동 로그인 실패1", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "자동 로그인 실패2", Toast.LENGTH_LONG).show();
                }
            });
        }

        autoLogin.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // ---------------- 자동 로그인에 체크가 되어 있다면 ----------------
                        // + 첫 로그인일 경우
                        if (email == null && password == null) { // autoLogin.isChecked() &&
                            UserAccount user = new UserAccount(et_email.getText().toString(), et_pwd.getText().toString());
                            call = Retrofit_client.getUserApiService().user_login(user);

                            call.enqueue(new Callback<UserAccount>() {
                                @Override
                                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                                    if (response.isSuccessful()) {
                                        // ---------------- SharedPreferences에 cookie 등등 저장하기 ----------------
                                        // 공유 환경설정 파일 이름 지정
                                        editor.putString("cookie", response.body().getJwt());
                                        editor.putString("email", et_email.getText().toString());
                                        editor.putString("password", et_pwd.getText().toString());
                                        editor.apply();

                                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, PageActivity.class);
                                        startActivity(intent); // 화면 전환
                                    } else {
                                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserAccount> call, Throwable t) {
                                    Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        // 그냥 일반(자동X) 로그인일 경우
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!autoLogin.isChecked()) {
                    UserAccount user = new UserAccount(et_email.getText().toString(), et_pwd.getText().toString());
                    call = Retrofit_client.getUserApiService().user_login(user);

                    call.enqueue(new Callback<UserAccount>() {
                        //콜백 받는 부분
                        @Override
                        public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                            // Toast.makeText(LoginActivity.this, "코드 에러: " + Integer.toString(response.code()), Toast.LENGTH_LONG).show();
                            // if (response.isSuccessful()) {
                            // ---------------- SharedPreferences에 cookie 저장하기 ----------------
                            editor.putString("cookie", response.body().getJwt());
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, PageActivity.class);
                            startActivity(intent); // 화면 전환
                            // } else {
                            //     Toast.makeText(LoginActivity.this, "로그인 실패1", Toast.LENGTH_LONG).show();
                            // }
                        }
                        @Override
                        public void onFailure(Call<UserAccount> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "로그인 실패2", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }
}