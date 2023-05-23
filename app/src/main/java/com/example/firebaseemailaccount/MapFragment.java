package com.example.firebaseemailaccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private Marker cityHallMarker;
    private Marker sungnyemunMarker;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private TextView titleTextView;
    private TextView addressTextView;
    private TextView tablewareTextView;
    private TextView babyChairTextView;
    private TextView automaticDoorTextView;
    private TextView nursingRoomTextView;


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
            // 버튼을 클릭했을 때 처리할 로직 작성
            // 새로운 화면으로의 전환 코드를 여기에 추가
            // 예시로 다른 액티비티를 시작하는 Intent 사용
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            startActivity(intent);
        });


        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        tablewareTextView = rootView.findViewById(R.id.tablewareTextView);
        babyChairTextView = rootView.findViewById(R.id.babyChairTextView);
        automaticDoorTextView = rootView.findViewById(R.id.automaticDoorsTextView);
        nursingRoomTextView = rootView.findViewById(R.id.nursingRoomTextView);

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

        // 서울시청의 위치를 설정합니다.
        double seoulCityHallLatitude = 37.5666102;
        double seoulCityHallLongitude = 126.9783881;
        LatLng seoulCityHallLatLng = new LatLng(seoulCityHallLatitude, seoulCityHallLongitude);

        // 숭례문의 위치를 설정합니다.
        double sungnyemunLatitude = 37.559978;
        double sungnyemunLongitude = 126.975291;
        LatLng sungnyemunLatLng = new LatLng(sungnyemunLatitude, sungnyemunLongitude);

        // 서울시청 마커를 생성합니다.
        cityHallMarker = new Marker();
        cityHallMarker.setPosition(seoulCityHallLatLng);
        cityHallMarker.setTag("CityHall"); // 마커에 태그 설정
        cityHallMarker.setMap(naverMap);

        // 숭례문 마커를 생성합니다.
        sungnyemunMarker = new Marker();
        sungnyemunMarker.setPosition(sungnyemunLatLng);
        sungnyemunMarker.setTag("Sungnyemun"); // 마커에 태그 설정
        sungnyemunMarker.setMap(naverMap);

        // 마커 클릭 이벤트 리스너를 설정합니다.
        Overlay.OnClickListener markerClickListener = new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                if (overlay instanceof Marker) {
                    Marker marker = (Marker) overlay;
                    String markerTag = (String) marker.getTag();

                    if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        // 파이어베이스 데이터 읽기
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");
                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String title = null;
                                    String address = null;
                                    String tableware = null;
                                    String babychair = null;
                                    String automaticdoor = null;
                                    String nursingroom = null;

                                    if (markerTag.equals("CityHall") && marker.getPosition().latitude == seoulCityHallLatitude && marker.getPosition().longitude == seoulCityHallLongitude) {
                                        DataSnapshot cityHallSnapshot = dataSnapshot.child("CityHall").child("name");
                                        if (cityHallSnapshot.exists()) {
                                            title = String.valueOf(cityHallSnapshot.getValue());
                                        }
                                        //주소
                                        address = String.valueOf(dataSnapshot.child("CityHallAddress").getValue());
                                        //아기식기
                                        tableware = String.valueOf(dataSnapshot.child("BabyTableware").getValue());
                                        if(tableware.equals(1)) {
                                            tableware = "아기식기 있음";
                                        }
                                        else {
                                            tableware = "아기식기 없음";
                                        }
                                        //아기의자
                                        babychair = String.valueOf(dataSnapshot.child("BabyChair").getValue());
                                        if(babychair.equals(1)) {
                                            babychair = "아기의자 있음";
                                        }
                                        else {
                                            babychair = "아기의자 없음";
                                        }
                                        //자동문
                                        automaticdoor = String.valueOf(dataSnapshot.child("AutomaticDoors").getValue());
                                        if(automaticdoor.equals(1)) {
                                            automaticdoor = "자동문 있음";
                                        }
                                        else {
                                            automaticdoor = "자동문 없음";
                                        }

                                        nursingroom = String.valueOf(dataSnapshot.child("NursingRoom").getValue());
                                        if(nursingroom.equals(1)) {
                                            nursingroom = "수유실 있음";
                                        }
                                        else {
                                            nursingroom = "수유실 없음";
                                        }

                                    } else if (markerTag.equals("Sungnyemun") && marker.getPosition().latitude == sungnyemunLatitude && marker.getPosition().longitude == sungnyemunLongitude) {
                                        DataSnapshot sungnyemunSnapshot = dataSnapshot.child("Sungnyemun").child("name");
                                        if (sungnyemunSnapshot.exists()) {
                                            title = String.valueOf(sungnyemunSnapshot.getValue());
                                        }
                                        address = String.valueOf(dataSnapshot.child("SungnyemunAddress").getValue());
                                    }

                                    // 정보 표시
                                    titleTextView.setText(title);
                                    addressTextView.setText(address);
                                    tablewareTextView.setText(tableware);
                                    babyChairTextView.setText(babychair);
                                    automaticDoorTextView.setText(automaticdoor);
                                    nursingRoomTextView.setText(nursingroom);

                                    // 슬라이딩 패널 열기
                                    //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // 데이터 읽기 실패 시 처리
                                Log.e("Firebase", "Failed to read data", databaseError.toException());
                            }
                        });
                    } else {
                        // 슬라이딩 패널 닫기
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                }

                return true;
            }
        };

        cityHallMarker.setOnClickListener(markerClickListener);
        sungnyemunMarker.setOnClickListener(markerClickListener);
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