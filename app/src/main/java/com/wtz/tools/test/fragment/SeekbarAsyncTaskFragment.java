package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
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

public class SeekbarAsyncTaskFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = SeekbarAsyncTaskFragment.class.getSimpleName();

    private SeekBar mSeekBar;
    private TextView mProgressText;
    private ImageView mRunImg;
    private ImageView mPlusImg;
    private ImageView mMinusImg;

    private AsyncTask mAsyncTask;

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
        mPlusImg.setOnClickListener(this);
        mPlusImg.setOnTouchListener(this);

        mMinusImg = view.findViewById(R.id.iv_minus);
        mMinusImg.setOnClickListener(this);
        mMinusImg.setOnTouchListener(this);

        mRunImg = view.findViewById(R.id.iv_run);
        mRunImg.setOnClickListener(this);
        mRunImg.setOnTouchListener(this);

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

    @Override
    public void onClick(View v) {
        int current = mSeekBar.getProgress();
        switch (v.getId()) {
            case R.id.iv_run:
                Log.d(TAG, "iv_run onClick");
                try {
                    // 把mayInterruptIfRunning设置为false是希望任务完成，减少数据丢失
                    // 设置为true，就立即结束
                    if (mAsyncTask != null) {
                        mAsyncTask.cancel(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAsyncTask = new RunTask().execute(current);
                break;
            case R.id.iv_plus:
                Log.d(TAG, "iv_plus onClick");
                current++;
                if (current > 10) {
                    current = 10;
                }
                mSeekBar.setProgress(current);
                break;
            case R.id.iv_minus:
                Log.d(TAG, "iv_minus onClick");
                current--;
                if (current < 0) {
                    current = 0;
                }
                mSeekBar.setProgress(current);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        String tag = null;
        switch (v.getId()) {
            case R.id.iv_run:
                tag = "iv_run";
                break;
            case R.id.iv_plus:
                tag = "iv_plus";
                break;
            case R.id.iv_minus:
                tag = "iv_minus";
                break;
        }
        Log.d(TAG, tag + " onTouch event:" + event.getAction());
        // 解决第一次只是获取焦点，第二次才响应onClick的问题
        v.requestFocus();
        return false;
    }

    class RunTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... args) {
            int current = args[0];
            Log.d(TAG, "RunTask doInBackground start=" + current);
            while (true) {
                current++;
                if (current >= 10) {
                    current = 10;
                    break;
                }
                publishProgress(current);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                current = mSeekBar.getProgress();
            }
            return current;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d(TAG, "RunTask onProgressUpdate progress=" + values[0]);
            mSeekBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "RunTask onPostExecute result=" + result);
            mSeekBar.setProgress(result);
        }

    }

}
