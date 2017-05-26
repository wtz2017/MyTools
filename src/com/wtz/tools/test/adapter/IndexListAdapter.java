package com.wtz.tools.test.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.test.data.FragmentItem;

public class IndexListAdapter extends BaseAdapter {
    private final static String TAG = IndexListAdapter.class.getName();

    private Context mContext;
    private ArrayList<FragmentItem> mData;

    public IndexListAdapter(Context context, ArrayList<FragmentItem> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return (mData == null) ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return (mData == null) ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == mContext) {
            Log.d(TAG, "getView...null == mContext");
            return null;
        }

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_index, null);
            convertView.setPadding(0, 0, 0, 0);
            holder.name = (TextView) convertView.findViewById(R.id.tv_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FragmentItem item = null;
        if ((null != mData) && (item = mData.get(position)) != null) {
            holder.name.setText(item.name == null ? "" : item.name);
        }

        return convertView;
    }

    class ViewHolder {
        TextView name;
    }

}
