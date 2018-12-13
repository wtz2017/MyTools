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
import android.widget.Button;
import android.widget.ImageView;

import com.wtz.tools.R;

public class PullCurtainFragment extends Fragment {
    private static final String TAG = PullCurtainFragment.class.getSimpleName();

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private ImageView mClipLeft;
    private ImageView mClipRight;
    private int mLevel;


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

        View view = inflater.inflate(R.layout.fragment_pull_curtain, container, false);
        mClipLeft = view.findViewById(R.id.clip_left_image);
        mClipRight = view.findViewById(R.id.clip_right_image);

        mClipLeft.getBackground().setLevel(10000);
        mClipRight.getBackground().setLevel(10000);

        Button button = view.findViewById(R.id.button_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mRunnable);
//                mLevel = 0;
                mLevel = 10000;
                mHandler.post(mRunnable);
            }
        });

        return view;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // mLevel The new mLevel, from 0 (minimum) to 10000 (maximum).
//            mLevel += 500;
//            if (mLevel >= 10000) {
//                mLevel = 10000;
//            }
            mLevel -= 100;
            if (mLevel <= 0) {
                mLevel = 0;
            }
            mClipLeft.getBackground().setLevel(mLevel);
            mClipRight.getBackground().setLevel(mLevel);
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 20);
        }
    };

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
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }
}
