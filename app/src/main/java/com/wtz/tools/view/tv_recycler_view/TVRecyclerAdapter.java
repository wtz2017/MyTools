package com.wtz.tools.view.tv_recycler_view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.wtz.tools.R;

import java.util.List;

public class TVRecyclerAdapter extends RecyclerView.Adapter<TVRecyclerAdapter.TestHolder> {
    private static final String TAG = "RecyclerAdapter";

    private List<String> mData;
    private ItemListener mItemListener;

    public TVRecyclerAdapter(List<String> data) {
        mData = data;
    }

    public void setData(List<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public interface ItemListener {
        void onItemClick(View view, int position);

        boolean onItemLongClick(View view, int position);

        void onFocusChange(View v, boolean hasFocus, int position);
    }

    public void setItemListener(ItemListener itemListener) {
        mItemListener = itemListener;
    }

    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_recycler_item, parent, false);
//        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
//        layoutParams.topMargin = 1;
//        view.setLayoutParams(layoutParams);
        TestHolder holder = new TestHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TestHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder position=" + position);
        if (mItemListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onItemClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mItemListener.onItemLongClick(v, position);
                }
            });
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mItemListener.onFocusChange(v, hasFocus, position);
                }
            });
        }
        if (mData != null && mData.size() > 0) {
            holder.textView.setText("" + mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    static class TestHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public TestHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
            textView.setTextSize(38.0f);
        }
    }
}
