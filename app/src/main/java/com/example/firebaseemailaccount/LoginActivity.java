package com.example.firebaseemailaccount;

import static com.example.firebaseemailaccount.R.id.bottomNav;
import static com.example.firebaseemailaccount.Retrofit_client.BASE_URL; // 추가
import static com.example.firebaseemailaccount.Retrofit_client.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Request;
// import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public int id;
    Call<UserAccount> call;
    private EditText et_email, et_pwd; //로그인 입력필드
    private Button btn_login; // 로그인 버튼

    public static SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAccount user = new UserAccount(et_email.getText().toString(), et_pwd.getText().toString());
                call = Retrofit_client.getUserApiService().user_login(user);

                call.enqueue(new Callback<UserAccount>() {
                    //콜백 받는 부분
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                        if(response.isSuccessful()){
                            // ---------------- 로그인 후 받아온 데이터 확인해보기 ----------------
                            // Log() 사용하여 test 삼아 확인
                            // Log.v("login-test", "jwtToken:" + response.body().getJwt());

                            // ---------------- SharedPreferences에 cookie 저장하기 ----------------
                            // 공유 환경설정 파일 이름 지정
                            sharedPref = getSharedPreferences("user_cookie", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("cookie", response.body().getJwt());
                            editor.apply();

                            // ---------------- (test)SharedPreferences에 저장한 것 읽어와보기 ----------------
                            // String test_cookie = sharedPref.getString("cookie", "abcd");

                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, PageActivity.class);
                            startActivity(intent); // 화면 전환
                        }else{
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Button btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });


    }
}
