package com.example.firebaseemailaccount;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends Fragment {

    // 로그에 사용할 TAG 변수
    final private String TAG = getClass().getSimpleName();

    Call<Community_model> call;

    // 사용할 컴포넌트 선언
    ListView listView;
    ListViewAdapter adapter;
    ListViewItem listViewitem;
    Button reg_button;
    String userid = "";
    Community_model cm;
    int i;
    public int post_id;
    static int list_count;

    // 리스트뷰에 사용할 제목 배열
    ArrayList<String> titleList = new ArrayList<>();

    // 클릭했을 때 어떤 게시물을 클릭했는지 게시물 번호를 담기 위한 배열
    ArrayList<String> seqList = new ArrayList<>();

    public static ListActivity newInstance() {
        return new ListActivity();
    }

    // ----------------------------------- 게시글 전체 list view -----------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.activity_list, null); // Fragment로 불러올 xml파일을 view로 가져옴
        adapter = new ListViewAdapter();

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        reg_button = view.findViewById(R.id.reg_button);

        // for(i=1;i<=adapter.getCount();i++) { // addItem() 이후에 저장된 데이터들에 대한 개수를 반환해주기 때문에 이 위치에서 제대로 동작X
        // 일단은 게시물 목록 중 특정 개수만 list up
        // for(i=1;i<=result.getRowCount();i++) {
        call = Retrofit_client.getApiService().community_detail_get(1);
        call.enqueue(new Callback<Community_model>() {
            @Override
            public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                list_count = response.body().getRowCount();

                for(i=1;i<=list_count;i++) {
                    call = Retrofit_client.getApiService().community_detail_get(i);
                    call.enqueue(new Callback<Community_model>() {
                        @Override
                        public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                            Community_model result = response.body();
                            // titleList.add(result.getTitle());
                            adapter.addItem(result.getId(), result.getTitle(), result.getContent());
                            adapter.notifyDataSetChanged(); // 꼭 반영해줘야 list에 제대로 addItem됨
                        }

                        @Override
                        public void onFailure(Call<Community_model> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Community_model> call, Throwable t) {

            }
        });

        // listView의 항목 중 하나 클릭 시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItem item = (ListViewItem) adapterView.getItemAtPosition(i);
                String title = item.getItemTitle();
                int id = item.getItemId();
//                Toast.makeText(getContext(), title, Toast.LENGTH_SHORT).show();
                //////                int data = result_global;
                Toast.makeText(getContext(), Integer.toString(id), Toast.LENGTH_SHORT).show();

                CommunityViewActivity fragment = CommunityViewActivity.newInstance(id);

                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                fragment.setArguments(bundle);

                ((PageActivity)getActivity()).replaceFragment(fragment);
            }
        });

        // 게시글 등록 버튼 클릭 시
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(CommunityActivity.newInstance());
            }
        });

        return view;
    }
}