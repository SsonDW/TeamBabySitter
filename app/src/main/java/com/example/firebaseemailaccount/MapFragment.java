package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private Marker marker;
    private InfoWindow infoWindow;
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

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // 정보 창에 표시할 뷰를 만듭니다.
        View infoWindowView = LayoutInflater.from(getContext()).inflate(R.layout.info_window_layout, null);
        TextView titleTextView = infoWindowView.findViewById(R.id.titleTextView);
        TextView addressTextView = infoWindowView.findViewById(R.id.addressTextView);

        // 서울시청의 위도와 경도를 설정합니다.
        double seoulCityHallLatitude = 37.5662952;
        double seoulCityHallLongitude = 126.9779451;
        LatLng seoulCityHallLatLng = new LatLng(seoulCityHallLatitude, seoulCityHallLongitude);

        // 마커를 생성합니다.
        marker = new Marker();
        marker.setPosition(seoulCityHallLatLng);
        marker.setMap(naverMap);

        infoWindow = new InfoWindow();
        infoWindow.setAdapter(new InfoWindow.ViewAdapter() {
            @NonNull
            @Override
            public View getView(@NonNull InfoWindow infoWindow) {
                // 파이어베이스 데이터 읽기
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            String title = dataSnapshot.child("CityHall").getValue(String.class);
                            String address = dataSnapshot.child("CityHallAddress").getValue(String.class);

                            // 정보 창에 데이터 설정
                            titleTextView.setText(title);
                            addressTextView.setText(address);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 데이터 읽기 실패 시 처리
                        System.out.println("Failed");
                    }
                });

                return infoWindowView;
            }
        });


        // 마커와 정보 창을 초기에 숨깁니다.
        marker.setHideCollidedMarkers(true);
        infoWindow.close();

        // 지도 클릭 이벤트 리스너를 설정합니다.
        naverMap.setOnMapClickListener((point, coord) -> {
            // 클릭한 위치의 위도와 경도를 가져옵니다.
            double latitude = coord.latitude;
            double longitude = coord.longitude;

            // 클릭한 위치가 서울시청의 위도와 경도와 일치할 때에만 마커와 정보 창을 보여줍니다.
            double latitudeError = 0.0001; // 오차 범위 설정
            double longitudeError = 0.0001; // 오차 범위 설정
            if (Math.abs(latitude - seoulCityHallLatitude) < latitudeError && Math.abs(longitude - seoulCityHallLongitude) < longitudeError){
                marker.setMap(naverMap);
                infoWindow.open(marker);
            } else {
                marker.setMap(null);
                infoWindow.close();
            }
        });
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}