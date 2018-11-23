package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wtz.tools.R;

public class SeekbarFragment extends Fragment {
    private static final String TAG = SeekbarFragment.class.getSimpleName();

    private SeekBar mSeekBar;
    private TextView mProgressText;
    private ImageView mPlusImg;
    private ImageView mMinusImg;

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

        View view = inflater.inflate(R.layout.fragment_seekbar, container, false);
        mProgressText = (TextView) view.findViewById(R.id.tv_progress);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgressText.setText((progress * 10) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mPlusImg = view.findViewById(R.id.iv_plus);
        mPlusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mPlusImg onClick");
                int current = mSeekBar.getProgress();
                current++;
                if (current > 10) {
                    current = 10;
                }
                mSeekBar.setProgress(current);
            }
        });
        mPlusImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "mPlusImg onTouch event:" + event.getAction());
                // 解决第一次只是获取焦点，第二次才响应onClick的问题
                v.requestFocus();
                return false;
            }
        });

        mMinusImg = view.findViewById(R.id.iv_minus);
        mMinusImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mMinusImg onClick");
                int current = mSeekBar.getProgress();
                current--;
                if (current < 0) {
                    current = 0;
                }
                mSeekBar.setProgress(current);
            }
        });
        mMinusImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "mMinusImg onTouch event:" + event.getAction());
                // 解决第一次只是获取焦点，第二次才响应onClick的问题
                v.requestFocus();
                return false;
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
}
