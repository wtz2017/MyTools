package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.wtz.tools.R;
import com.wtz.tools.view.ProgressRound;
import com.wtz.tools.view.ProgressWheel;
import com.wtz.tools.view.tv_recycler_view.TVRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TVRecyclerViewFragment extends Fragment {
    private static final String TAG = TVRecyclerViewFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private TVRecyclerAdapter mRecyclerAdapter;
    private List<String> mRecyclerData;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_tv_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.tv_recycler_view);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new TVRecyclerAdapter(getRecyclerData());
        mRecyclerAdapter.setItemListener(new TVRecyclerAdapter.ItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "mRecyclerView onItemClick position=" + position);
            }

            @Override
            public boolean onItemLongClick(View view, int position) {
                Log.d(TAG, "mRecyclerView onItemLongClick position=" + position);
                return false;
            }

            @Override
            public void onFocusChange(View v, boolean hasFocus, int position) {
                Log.d(TAG, "mRecyclerView onFocusChange position=" + position
                        + ", hasFocus=" + hasFocus);
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.requestFocus();

        return view;
    }

    private List<String> getRecyclerData() {
        if (mRecyclerData == null) {
            mRecyclerData = new ArrayList<>();
        }
        for (int i = 0; i < 27; i++) {
            mRecyclerData.add(i + " test ");
        }
        return mRecyclerData;
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
}
