package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.view.RecyclerLoadMoreAdapter;
import com.wtz.tools.view.recycler_item_decoration.BookRankAdapter;
import com.wtz.tools.view.recycler_item_decoration.ColorDividerItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.FlagItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.GroupInfo;
import com.wtz.tools.view.recycler_item_decoration.RecyclerAdapter;
import com.wtz.tools.view.recycler_item_decoration.SectionDecoration;
import com.wtz.tools.view.recycler_item_decoration.SimpleDividerItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.StickySectionDecoration;
import com.wtz.tools.view.recycler_item_decoration.TimelineItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 参考：https://www.jianshu.com/p/b502c5b59998
 */
public class RecyclerLoadMoreFragment extends Fragment {
    private static final String TAG = RecyclerLoadMoreFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<String> data = new ArrayList<>();
    private MyAdapter myAdapter;
    private RecyclerLoadMoreAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_recycler_load_more, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutmanager);

        myAdapter = new MyAdapter(data);
        mAdapter = new RecyclerLoadMoreAdapter(myAdapter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerLoadMoreAdapter.ScrollListener() {
            @Override
            public void onLoadMore() {
                mAdapter.setLoadState(RecyclerLoadMoreAdapter.LOADING);
                if (data.size() < 52) {
                    // 模拟获取网络数据，延时1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Log.d(TAG, "Timer run");
                            getData();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "myAdapter.updateData");
                                    myAdapter.updateData(data);
                                    mAdapter.setLoadState(RecyclerLoadMoreAdapter.LOADING_COMPLETE);
                                }
                            });
                        }
                    }, 2000);
                } else {
                    // 显示加载到底的提示
                    mAdapter.setLoadState(RecyclerLoadMoreAdapter.LOADING_END);
                }
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        // 设置刷新控件颜色
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#FF0000"));
        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 延时模拟下拉刷新
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 刷新数据
                        data.clear();
                        getData();
                        myAdapter.updateData(data);
                        mAdapter.notifyDataSetChanged();
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 2000);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    private void getData() {
        char letter = 'A';
        for (int i = 0; i < 26; i++) {
            data.add(String.valueOf(letter));
            letter++;
        }
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<String> dataList;

        public MyAdapter(List<String> dataList) {
            this.dataList = dataList;
        }

        public void updateData(List<String> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recyclerview, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            recyclerViewHolder.tvItem.setText(dataList.get(position));
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        private class RecyclerViewHolder extends RecyclerView.ViewHolder {

            TextView tvItem;

            RecyclerViewHolder(View itemView) {
                super(itemView);
                tvItem = (TextView) itemView.findViewById(R.id.tv_item);
            }
        }
    }

}
