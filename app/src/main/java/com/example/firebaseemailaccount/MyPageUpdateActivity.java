package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageUpdateActivity extends Fragment {
    private View view;
    Call<UserAccount> call;

    EditText email, nickname, baby_birthday, baby_gender;
    Button update_button;

    public static MyPageUpdateActivity newInstance() {
        return new MyPageUpdateActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        view = inflater.inflate(R.layout.activity_update_mypage, null);

        email = view.findViewById(R.id.email);
        nickname = view.findViewById(R.id.nickname);
        baby_birthday = view.findViewById(R.id.baby_birthday);
        baby_gender = view.findViewById(R.id.baby_gender);
        update_button = view.findViewById(R.id.update_button);

        // update_button을 '누르면' 값 설정하고 반영
        update_button.setOnClickListener(view -> {
            UserAccount user = new UserAccount(
                    email.getText().toString(),
                    nickname.getText().toString(),
                    baby_birthday.getText().toString(),
                    baby_gender.getText().toString()
            );
            String login_cookie = LoginActivity.sharedPref.getString("cookie", "abcd");
            call = Retrofit_client.getUserApiService().user_update(login_cookie, user);
            call.enqueue(new Callback<UserAccount>() {
                //콜백 받는 부분
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getContext(), "회원정보 수정 성공", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getContext(), "회원정보 수정 실행되지 않음", Toast.LENGTH_LONG).show();
                    }

                    ((PageActivity)getActivity()).replaceFragment(MyPageActivity.newInstance());
                }
                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                    Toast.makeText(getContext(), "회원정보 수정 실패", Toast.LENGTH_LONG).show();
                }
            });
        });

        return view;
    }
}
