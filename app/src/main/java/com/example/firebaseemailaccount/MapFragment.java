package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private RatingBar averageRatingBar;
    private ListView listView;
    private TextView optionTextView1;
    private TextView optionTextView2;
    private Marker currentMarker;
    private InfoWindow currentInfoWindow;



    public MapFragment() { }

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_map, container, false);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SlidingUpPanelLayout slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        imageView = rootView.findViewById(R.id.iconImageView);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        phoneTextView = rootView.findViewById(R.id.phoneNumberTextView);
        optionTextView1 = rootView.findViewById(R.id.OptionTextView1);
        optionTextView2 = rootView.findViewById(R.id.OptionTextView2);
        Button myButton = rootView.findViewById(R.id.myButton);
        myButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            intent.putExtra("key", titleTextView.getText()); // 여기서 "key"는 데이터를 식별하기 위한 키 값이고, value는 전달하려는 데이터입니다.
            startActivity(intent);
        });
        averageRatingBar = rootView.findViewById(R.id.ratingBarResult);
        listView = rootView.findViewById(R.id.listView);

        //초기화
        titleTextView.setText("오늘의 PICK"); // 제목 초기화
        // 글자 크기와 bold 설정
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // 크기를 원하는 값으로 변경
        titleTextView.setTypeface(null, Typeface.BOLD);

        // 글자 색상 변경
        titleTextView.setTextColor(Color.BLUE); // 색상을 원하는 값으로 변경
        phoneTextView.setText("서울특별시 종로구 율곡로23길 3");
        addressTextView.setText("★ 판타노디저트 ★");
        optionTextView1.setText("수유실X, 아기의자O, 아기식기O");
        optionTextView2.setText("자동문X, 놀이방O, 경사로O");
        imageView.setImageResource(R.drawable.pantanodessert); // 이미지 초기화

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
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

                    InfoWindow infoWindow = new InfoWindow();
                    infoWindow.setAdapter(new InfoWindow.ViewAdapter() {
                        @SuppressLint("SetTextI18n")
                        @NonNull
                        @Override
                        public View getView(@NonNull InfoWindow infoWindow) {
                            // 정보 창의 내용을 설정
                            @SuppressLint("InflateParams") View infoWindowView = LayoutInflater.from(getContext()).inflate(R.layout.info_window_layout, null);
                            TextView infoOption1 = infoWindowView.findViewById(R.id.titleTextView);
                            TextView infoOption2 = infoWindowView.findViewById(R.id.addressTextView);

                            infoOption1.setText(dataSnapshot.child("NursingRoom").getValue()
                                    + String.valueOf(dataSnapshot.child("BabyChair").getValue())
                                    + dataSnapshot.child("BabyTableware").getValue());
                            infoOption2.setText(dataSnapshot.child("AutomaticDoors").getValue()
                                    + String.valueOf(dataSnapshot.child("PlayRoom").getValue())
                                    + dataSnapshot.child("Ramp").getValue());

                            return infoWindowView;
                        }
                    });

                    // 마커 클릭 리스너 등록
                    newMarker.setOnClickListener(marker -> {

                        // 마커 클릭 시 동작할 내용 작성
                        titleTextView.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                        phoneTextView.setText(String.valueOf(dataSnapshot.child("PhoneNumber").getValue()));
                        addressTextView.setText(String.valueOf(dataSnapshot.child(name + "Address").getValue()));
                        // 경사로, 아기식기, 아기용품 등등 관련 정보 표시
                        String optionText1 = dataSnapshot.child("NursingRoom").getValue()
                                + String.valueOf(dataSnapshot.child("BabyChair").getValue())
                                + dataSnapshot.child("BabyTableware").getValue();
                        optionTextView1.setText(optionText1);
                        String optionText2 = dataSnapshot.child("AutomaticDoors").getValue()
                                + String.valueOf(dataSnapshot.child("PlayRoom").getValue())
                                + dataSnapshot.child("Ramp").getValue();
                        optionTextView1.setText(optionText1);
                        optionTextView2.setText(optionText2);
                        averageRatingBar.setRating(0.0f);

                        LatLng markerPosition = newMarker.getPosition();
                        LatLng databaseLatLng = new LatLng(latitude, longitude);

                        // 이전 마커와 정보 창이 있는지 확인하고 닫기
                        if (currentMarker != null && currentMarker.getInfoWindow() != null) {
                            currentMarker.getInfoWindow().close();
                            currentMarker = null;
                        }
                        if (currentInfoWindow != null) {
                            currentInfoWindow.close();
                            currentInfoWindow = null;
                        }
                        if (markerPosition.equals(databaseLatLng)) {
                            // 일치하는 경우 정보 창 열기
                            currentInfoWindow = infoWindow;
                            infoWindow.open(newMarker);
                            currentMarker = newMarker;
                        }

                        // 마커 클릭 시 동작할 내용 작성
                        titleTextView.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                        phoneTextView.setText(String.valueOf(dataSnapshot.child("PhoneNumber").getValue()));
                        addressTextView.setText(String.valueOf(dataSnapshot.child(name + "Address").getValue()));
                        averageRatingBar.setRating(0.0f);

                        // FirebaseStorage 접근
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // StorageReference 생성
                        StorageReference storageRef = storage.getReference().child(name + ".png");
                        // 이미지 다운로드 URL 가져오기
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // 다운로드 URL을 사용하여 이미지 로드 등의 작업 수행
                            String imageUrl = uri.toString();
                            // 이미지를 imageView에 설정하거나 처리하는 등의 작업 수행
                            // Glide 등의 라이브러리를 사용하여 이미지 로드를 쉽게 처리할 수 있습니다.
                            Glide.with(getContext()).load(imageUrl).into(imageView);
                        }).addOnFailureListener(exception -> {
                            // 이미지 다운로드 실패 시 처리할 작업 수행
                        });

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
                                    int info_count = 0; // 옵션 표시 배열 길이
                                    ArrayList<String> reviewData = new ArrayList<>();
                                    String Info[] = new String[7]; // 옵션 표시 배열

                                    // 모든 문서의 rating 값을 합산하고 문서 개수를 세기
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        // 평균 평점 계산
                                        double ratingDouble = documentSnapshot.getDouble("rating");
                                        totalRating += ratingDouble;
                                        count++;
                                        // 경사로, 아기식기등 옵션 사항 표시하기
//                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasRamp"))) {
//                                            Info[info_count++] = "경사로 ";
//                                        }
                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasAutomaticDoor"))) {
                                            Info[info_count++] = "자동문 ";
                                        }
                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasBabyChair"))) {
                                            Info[info_count++] = "아기의자 ";
                                        }
                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasNursingRoom"))) {
                                            Info[info_count++] = "수유실 ";
                                        }
                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasPlayRoom"))) {
                                            Info[info_count++] = "놀이방 ";
                                        }
                                        if(Boolean.TRUE.equals(documentSnapshot.getBoolean("hasTableWare"))) {
                                            Info[info_count] = "아기식기 ";
                                        }
                                        StringBuilder InfoOption = new StringBuilder();
                                        for(int j = 0; j<info_count; j++) {
                                            InfoOption.append(Info[j]);
                                        }
                                        reviewData.add(InfoOption.toString());
                                        // reviewText 리스트뷰에 추가하기
                                        String temp = documentSnapshot.getString("reviewText");
                                        reviewData.add(temp);
                                    }

                                    // 평균 계산
                                    if (count > 0) {
                                        double averageRating = totalRating / count;
                                        float averageRatingFloat = (float) averageRating;

                                        // averageRatingBar에 평균값 설정
                                        averageRatingBar.setRating(averageRatingFloat);
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, reviewData);
                                    listView.setAdapter(adapter); // listView를 adater랑 연결해서 데이터 넣어주기
                                }

                            } else {
                                // 쿼리 실패 시 동작
                                Log.e("ReviewActivity", "Error getting reviews", task.getException());
                            }
                        });

                        return true;
                    });
                } else {
                    // 데이터가 없는 경우 처리
                    System.out.println("Error");
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

        // 마커 생성, 후에 배열로 추가할 수 있도록 변경할 수 있을 것!
        generateMarker("AtwosomePlace");
        generateMarker("CGV");
        generateMarker("CityHall");
        generateMarker("HanaBank");
        generateMarker("HansungUniversity");
        generateMarker("HansungUniversityStation");
        generateMarker("Lotteria");
        generateMarker("Napoleon");
        generateMarker("SFCMall");
        generateMarker("Sioldon");
        generateMarker("Starbucks");
        generateMarker("Sungnyemun");
        generateMarker("ThePlaza");
        generateMarker("BurgerPark");
        generateMarker("CafeTravel");
        generateMarker("Cheongkimyeonga");
        generateMarker("PantanoDessert");
        generateMarker("SeongkuakMuseum");
        generateMarker("TeenteenHall");
        generateMarker("JacksonPizza");
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