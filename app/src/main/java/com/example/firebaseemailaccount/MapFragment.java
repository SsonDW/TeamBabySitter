package com.example.firebaseemailaccount;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView titleTextView;
    private TextView addressTextView;
    private TextView phonenumberTextView;


    public MapFragment() { }

    @NonNull
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_map, container, false);

        Button myButton = rootView.findViewById(R.id.myButton);
        myButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            startActivity(intent);
        });

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        phonenumberTextView = rootView.findViewById(R.id.newTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // 슬라이딩 패널의 슬라이드 상태 변경 이벤트 처리
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // 슬라이딩 패널의 상태 변경 이벤트 처리
            }
        });

        return rootView;
    }

    public void generateMarker(String name) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location/" + name);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng newLatLng = new LatLng(latitude, longitude);

                    // 새로운 LatLng 객체를 사용하여 원하는 작업 수행
                    Marker newMarker = new Marker();
                    newMarker.setPosition(newLatLng);
                    newMarker.setTag(name); // 마커에 태그 설정
                    newMarker.setMap(naverMap);

                    // 마커 클릭 리스너 등록
                    newMarker.setOnClickListener(marker -> {
                        // 마커 클릭 시 동작할 내용 작성
                        titleTextView.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                        phonenumberTextView.setText(String.valueOf(dataSnapshot.child("PhoneNumber").getValue()));
                        addressTextView.setText(String.valueOf(dataSnapshot.child(name + "Address").getValue()));
                        return true;
                    });
                } else {
                    // 데이터가 없는 경우 처리
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // 초기 위치 설정
        double initialLatitude = 37.5828483; // 초기 위도(한성대)
        double initialLongitude = 127.0105811; // 초기 경도(한성대)

        // 지도의 초기 위치로 이동
        naverMap.setCameraPosition(new CameraPosition(
                new LatLng(initialLatitude, initialLongitude), // 위도와 경도 설정
                15 // 줌 레벨 설정
        ));

        // 마커 생성
        generateMarker("CityHall");
        generateMarker("AtwosomePlace");
        generateMarker("Sungnyemun");
        generateMarker("CGV");
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}