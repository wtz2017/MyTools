package com.wtz.tools.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wtz.tools.R;
import com.wtz.tools.utils.image.UILTool;

import java.util.List;


public class GridAdapter extends BaseAdapter {
    private final static String TAG = GridAdapter.class.getSimpleName();

    private Context mContext;
    private List<String> mDataList;

    public GridAdapter(Context context, List<String> dataList) {
        mContext = context;
        mDataList = dataList;
        UILTool.init(context);
    }

    public void updateData(List<String> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public boolean isInLastRow(int position, int columns) {
        return getRow(getCount(), columns) == getRow(position + 1, columns);
    }

    public int getRow(int target, int columns) {
        int quotient = target / columns;
        return  (target % columns > 0) ? quotient + 1 : quotient ;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, parent,
                    false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageLoader.getInstance().displayImage((String) getItem(position), holder.imageView,
                UILTool.getDefaultOptions(), UILTool.getDefaultListener(TAG));

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }

}