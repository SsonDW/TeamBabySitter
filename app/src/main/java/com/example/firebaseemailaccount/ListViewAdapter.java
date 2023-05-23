package com.example.firebaseemailaccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;

public class ListViewAdapter extends BaseAdapter {
    private TextView titleTextView;
    private TextView contentTextView;
    private int id;
    Call<Community_model> call;

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        titleTextView = (TextView) convertView.findViewById(R.id.title);
        contentTextView = (TextView) convertView.findViewById(R.id.text);

        ListViewItem listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getItemTitle());
        contentTextView.setText(listViewItem.getItemContent());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemModelId(ListViewAdapter adapter) {
        // id = adapter.
        return id;
    }

    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(int id, String title, String content) {
        ListViewItem item = new ListViewItem();

        item.setItemId(id);
        item.setItemTitle(title);
        item.setItemContent(content);

        listViewItemList.add(item);

        // list 호출 시 버벅거림 없도록
        // 효과X.
        // this.notifyDataSetChanged(); // list 호출 시 버벅거림 없도록
    }

}