package com.wtz.tools.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wtz.tools.test.fragment.AnimationFragment.InterpolatorItem;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private List<InterpolatorItem> mDataList;

    public SpinnerAdapter(Context context, List<InterpolatorItem> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return (mDataList == null) ? 0 : mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return (mDataList == null) ? null : mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder itemLayout = null;
        if (convertView == null) {
            itemLayout = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
            itemLayout.tvName = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(itemLayout);
        } else {
            itemLayout = (ViewHolder) convertView.getTag();
        }

        InterpolatorItem item = null;
        if ((null != mDataList) && (item = mDataList.get(position)) != null) {
            itemLayout.tvName.setText(item.name);
        }

        return convertView;
    }

    class ViewHolder {
        TextView tvName;
    }

}
