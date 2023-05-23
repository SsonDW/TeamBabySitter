package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityViewActivity extends Fragment {
    Call<Community_model> call;
    // CommunityActivity ca = new CommunityActivity();
    ListActivity la = new ListActivity();
    static int post_id;
    TextView title_txt, content_txt, created_at_txt;
    Button comment_button, list_button;

    // 각각의 Fragment마다 Instance를 반환해 줄 메소드를 생성
    // Activity 하위의 모든 Fragment들이 newInstance() 메소드를 가지고 있어야 화면 전환이 가능
    public static CommunityViewActivity newInstance(int id) {
//        CommunityViewActivity fragment = new CommunityViewActivity();

//        Bundle bundle = new Bundle();
//        bundle.putInt("id", id);
//        fragment.setArguments(bundle);
        post_id = id;
        return new CommunityViewActivity();
    }

    public void postId(int post_id) {
        this.post_id = post_id;
    }

    // ----------------------------------- 특정 게시글 view -----------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.activity_community_view, null); // Fragment로 불러올 xml파일을 view로 가져옴
        title_txt = view.findViewById(R.id.title_txt);
        // created_at_txt = view.findViewById(R.id.created_at_txt);
        content_txt = view.findViewById(R.id.content_txt);
        comment_button = view.findViewById(R.id.comment_button);
        list_button = view.findViewById(R.id.list_button);

        // CommunityActivity에서 등록한 게시글의 id값 post_id로 받아온 것 그대로 사용
        // call = Retrofit_client.getApiService().community_detail_get(la.post_id); // 실패
        call = Retrofit_client.getApiService().community_detail_get(post_id);
        call.enqueue(new Callback<Community_model>() {
            //콜백 받는 부분
            @Override
            public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                Community_model result = response.body();
                title_txt.setText(result.getTitle());
                // created_at_txt.setText(result.getCreatedAt().toString());
                content_txt.setText(result.getContent());
            }

            @Override
            public void onFailure(Call<Community_model> call, Throwable t) {

            }
        });

        // 댓글 등록 버튼 클릭 시
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getActivity()로 MainActivity의 replaceFragment를 불러옴
                // 다시 또 새로 불러올 Fragment의 Instance를 Main으로 전달
                // ((PageActivity)getActivity()).replaceFragment(CommunityCommentActivity.newInstance());
            }
        });
        // 게시물 전체 list 조회 버튼 클릭 시
        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(ListActivity.newInstance());
            }
        });

        return view;
    }
}