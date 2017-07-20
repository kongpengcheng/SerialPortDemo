package com.example.shen.serialportdemo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shen.serialportdemo.R;

import java.util.ArrayList;

/**
 * Created by SHEN on 2015/10/12.
 */
public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> list;
    private LayoutInflater inflater;
    public ListViewAdapter(Context context,ArrayList<String> list){
        this.context=context;
        this.list=list;
        inflater=LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.item_listview, null);
            viewHolder=new ViewHolder();
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.tv_info);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.tvInfo.setText(list.get(position));
        return convertView;
    }

    static class ViewHolder{
        TextView tvInfo;
    }
}
