package com.example.firebaseemailaccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends Fragment {
    private View view;
    private String TAG = "MyPageFragment";
    Call<UserAccount> call;
    Button update_button, logout_button;
    TextView nickname_view, baby_birthday_view, baby_gender_view;

    public static MyPageActivity newInstance() {
        return new MyPageActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.i(TAG,"onCreateView");

        view = inflater.inflate(R.layout.activity_mypage,container,false);

        update_button = view.findViewById(R.id.update_button);
        logout_button = view.findViewById(R.id.logout_button);
        nickname_view = view.findViewById(R.id.nickname_view);
        baby_birthday_view = view.findViewById(R.id.baby_birthday_view);
        baby_gender_view = view.findViewById(R.id.baby_gender_view);

        // SharedPreferences sharedPref = getSharedPreferences("user_cookie", Context.MODE_PRIVATE);
        String login_cookie = LoginActivity.sharedPref.getString("cookie", "abcd");
        call = Retrofit_client.getUserApiService().user_view(login_cookie);
        call.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                if (response.isSuccessful()) {
                    UserAccount result = response.body();
                    nickname_view.setText(result.getNickname());
                    baby_birthday_view.setText(result.getBabyBirthday());
                    baby_gender_view.setText(result.getBabyGender());
                }
            }
            @Override
            public void onFailure(Call<UserAccount> call, Throwable t) {

            }
        });

        // '프로필 편집' 버튼 클릭 시 수정 화면으로 전환
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(MyPageUpdateActivity.newInstance());
            }
        });

        // '로그아웃' 버튼 클릭 시 cookie 삭제
        // 로그아웃 된 화면의 마이페이지로 전환
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // token 값 전달하여 서버에 로그인된 상태였음을 알림
                call = Retrofit_client.getUserApiService().user_logout(login_cookie);
                call.enqueue(new Callback<UserAccount>() {
                    @Override
                    public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                        if (response.isSuccessful()) {
                            // 서버 상에서 로그아웃 완료 + 안드로이드 상에서 저장해놨던 cookie 삭제하기
                            SharedPreferences.Editor editor = LoginActivity.sharedPref.edit();
                            editor.remove("cookie");
                            editor.apply();
                            Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_LONG).show();
                            // 1) 로그아웃 후 로그인 전의 마이페이지로 리디렉션할 경우
                            // ((PageActivity)getActivity()).replaceFragment(MyPageActivity.newInstance());
                            // 2) 로그아웃 후 처음 로그인 화면으로 리디렉션할 경우
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "로그아웃 실행되지 않음", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserAccount> call, Throwable t) {
                        Toast.makeText(getContext(), "로그아웃 실패", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return view;
    }
}