package com.example.firebaseemailaccount;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
    private Button myButton;
    private RatingBar averageRatingBar;
    private  TextView newTextView;
    private ImageView iconImageView;


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

        slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        phonenumberTextView = rootView.findViewById(R.id.newTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        myButton = rootView.findViewById(R.id.myButton);
        myButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            System.out.println("MapFragment: " + titleTextView.getText());
            intent.putExtra("key", titleTextView.getText()); // 여기서 "key"는 데이터를 식별하기 위한 키 값이고, value는 전달하려는 데이터입니다.
            startActivity(intent);
        });
        averageRatingBar = rootView.findViewById(R.id.ratingBarResult);
        newTextView = rootView.findViewById(R.id.newTextView);
        iconImageView = rootView.findViewById(R.id.iconImageView);

        //초기화
        titleTextView.setText(""); // 제목 초기화
        phonenumberTextView.setText(""); // 전화번호 초기화
        newTextView.setText("");
        addressTextView.setText(""); // 주소 초기화
        // 이미지뷰에 초기 이미지 설정
        iconImageView.setImageResource(R.drawable.white);

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
                        averageRatingBar.setRating(0.0f);

                        // Firestore 인스턴스 가져오기
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        // "Reviews" 컬렉션 참조
                        CollectionReference reviewsCollectionRef = db.collection("Reviews");

                        // storeName이 "cgv"인 데이터 필터링
                        Query query = reviewsCollectionRef.whereEqualTo("storeName", titleTextView.getText().toString());

                        // 쿼리 실행
                        query.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 쿼리 결과 가져오기
                                QuerySnapshot querySnapshot = task.getResult();

                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    double totalRating = 0.0;
                                    int count = 0;

                                    // 모든 문서의 rating 값을 합산하고 문서 개수를 세기
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        double ratingDouble = documentSnapshot.getDouble("rating");
                                        totalRating += ratingDouble;
                                        count++;
                                    }

                                    // 평균 계산
                                    if (count > 0) {
                                        double averageRating = totalRating / count;
                                        float averageRatingFloat = (float) averageRating;
                                        System.out.println("averageRatingFloat: " + averageRatingFloat);

                                        // averageRatingBar에 평균값 설정
                                        averageRatingBar.setRating(averageRatingFloat);

                                        // 평균값을 사용하여 원하는 동작 수행
                                        // 예: TextView에 평균값 설정 등
                                        // ...
                                    }
                                }

                            } else {
                                // 쿼리 실패 시 동작
                                Log.e("ReviewActivity", "Error getting reviews", task.getException());
                                // 예: 오류 메시지 표시 등
                            }
                        });

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