package com.example.firebaseemailaccount;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.setContentView(R.layout.bottom_navigation_container);
        //this.setContentView(R.layout.activity_main);
        this.setContentView(R.layout.activity_login);

        // 화면 전환 프래그먼트 선언 및 초기 화면 설정
        // FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // fragmentTransaction.add(R.id.main_frame, CommunityViewActivity.newInstance()).commit();
    }
//    public void replaceFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        // Fragment로 사용할 MainActivity내의 layout공간을 선택
//        fragmentTransaction.replace(R.id.main_frame, fragment).commit();
//    }
}