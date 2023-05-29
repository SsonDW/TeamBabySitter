package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityViewActivity extends Fragment {
    Call<Community_model> call;
    Call<Comment_model> call2;
    // CommunityActivity ca = new CommunityActivity();
    ListActivity la = new ListActivity();
    static Integer post_id;
    TextView title_txt, content_txt, created_at_txt, comment_view1, comment_view2, comment_view3, comment_view4, comment_view5;
    EditText comment_txt;
    Button comment_button, list_button;
    ListView listView;
    ListViewAdapter adapter;
    ListViewItem listViewitem;
    static int comments_count;
    int count = 0;
    static int i;

    // ------------------------------ 화면 전환 시 사용 ------------------------------
    public static CommunityViewActivity newInstance(int id) {
        post_id = id;
        return new CommunityViewActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.activity_community_view, null); // Fragment로 불러올 xml파일을 view로 가져옴
        adapter = new ListViewAdapter();
        listView = (ListView) view.findViewById(R.id.listView);
        title_txt = view.findViewById(R.id.title_txt);
        // created_at_txt = view.findViewById(R.id.created_at_txt);
        content_txt = view.findViewById(R.id.content_txt);
        comment_view1 = view.findViewById(R.id.comment_view1);
        comment_txt = view.findViewById(R.id.comment_txt);
        comment_button = view.findViewById(R.id.comment_button);
        list_button = view.findViewById(R.id.list_button);

        listView.setAdapter(adapter);

        // ------------------------------ 게시글 view ------------------------------
        call = Retrofit_client.getApiService().community_detail_get(post_id);
        call.enqueue(new Callback<Community_model>() {
            //콜백 받는 부분
            @Override
            public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                Community_model result = response.body();
                comments_count = response.body().getCommentsCount();
                title_txt.setText(result.getTitle());
                // created_at_txt.setText(result.getCreatedAt().toString());
                content_txt.setText(result.getContent());
                // Toast.makeText(getContext(), "게시글 댓글 개수: " + Integer.toString(comments_count), Toast.LENGTH_LONG).show();

                // ----------------------------------- 해당 게시글 전체 댓글 list view -----------------------------------
                call2 = Retrofit_client.getCommentApiService().comment_get(1); // 각 댓글 순서대로 가져옴
                call2.enqueue(new Callback<Comment_model>() {
                    @Override
                    public void onResponse(Call<Comment_model> call, Response<Comment_model> response) {
                        // 모든 댓글들 중 해당 게시물의 댓글만 가져와서 listing // listview로 셋팅 못함
                        if (response.isSuccessful()) { //  && post_id.equals(result.getCommunityId())
                            Toast.makeText(getContext(), "댓글 get 성공", Toast.LENGTH_LONG).show();
                            Comment_model result = response.body();
//                        adapter.addCommentItem(result.getContent());
//                        adapter.notifyDataSetChanged();
                            comment_view1.setText(result.getContent()); // test용
                        } else {
                            Toast.makeText(getContext(), "댓글 get 실패1", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Comment_model> call, Throwable t) {
                        // 댓글 get하는데 onFailure가 뜸
                        Toast.makeText(getContext(), "댓글 get 실패2", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(Call<Community_model> call, Throwable t) {
            }
        });

        // ----------------------------------- 해당 게시글 전체 댓글 list view -----------------------------------

        // ----------------------------------- 댓글 작성 후 등록 -----------------------------------
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment_model cm = new Comment_model(comment_txt.getText().toString());
                call2 = Retrofit_client.getCommentApiService().comment_post(cm, post_id);
                call2.enqueue(new Callback<Comment_model>() {
                    //콜백 받는 부분
                    @Override
                    public void onResponse(Call<Comment_model> call, Response<Comment_model> response) {
                        Toast.makeText(getContext(), "댓글 등록 성공", Toast.LENGTH_LONG).show();
                        Comment_model result = response.body();
                        comment_view1.setText(result.getContent());
                        comment_txt.setText(""); // 댓글 등록 후 지워버리기
                    }
                    @Override
                    public void onFailure(Call<Comment_model> call, Throwable t) {
                        Toast.makeText(getContext(), "댓글 등록 실패", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        // ----------------------------------- 전체 게시글 목록으로 돌아가는 버튼 -----------------------------------
        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(ListActivity.newInstance());
            }
        });
        return view;
    }
}