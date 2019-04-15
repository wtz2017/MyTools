package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.wtz.tools.R;
import com.wtz.tools.view.ProgressRound;
import com.wtz.tools.view.ProgressWheel;

public class LoadingProgressFragment extends Fragment {
    private static final String TAG = LoadingProgressFragment.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mCount;

    private ProgressWheel mProgressWheel;
    private ProgressRound mProgressRound;

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

        View view = inflater.inflate(R.layout.fragment_loading_progress, container, false);

        final ProgressBar pbHorizontal = view.findViewById(R.id.pb_horizontal);
        final ProgressBar pbHorizontalCustom = view.findViewById(R.id.pb_horizontal_custom_progress);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCount++;
                pbHorizontal.setProgress(mCount);
                pbHorizontalCustom.setProgress(mCount);
                mHandler.removeCallbacks(this);
                if (mCount < 100) mHandler.postDelayed(this, 100);
            }
        }, 100);

        mProgressWheel = view.findViewById(R.id.progress_wheel);
        mProgressWheel.startSpinning();

        mProgressRound = view.findViewById(R.id.progress_round);
        mProgressRound.runAnimate(0.7f);

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
        mProgressWheel.stopSpinning();
        mProgressRound.cancelAnimate();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
