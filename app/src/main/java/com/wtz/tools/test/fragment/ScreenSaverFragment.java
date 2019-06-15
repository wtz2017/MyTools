package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.test.ScreenSaverActivity;

public class ScreenSaverFragment extends Fragment implements View.OnKeyListener, View.OnTouchListener {
    private static final String TAG = ScreenSaverFragment.class.getSimpleName();

    private BroadcastReceiver mScreenReceiver;

    private static final int DELAY_INTERVAL = 5000;
    private static final int MSG_GOTO_SCREEN_SAVER = 100;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GOTO_SCREEN_SAVER:
                    gotoScreenSaver();
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        registerScreenReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_screen_saver_main, container, false);
        TextView tipsView = view.findViewById(R.id.tv_tips);
        tipsView.setOnKeyListener(this);
        tipsView.setOnTouchListener(this);

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

        delayGotoScreenSaver();
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
        unregisterScreenReceiver();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    private void registerScreenReceiver() {
        if (mScreenReceiver == null) {
            mScreenReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    Log.d(TAG, "mScreenReceiver onReceive " + action);
                    if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        // 开屏
                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        // 锁屏
                        gotoScreenSaver();
                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                        // 用户解锁
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            getActivity().registerReceiver(mScreenReceiver, filter);
        }
    }

    private void gotoScreenSaver() {
        mHandler.removeMessages(MSG_GOTO_SCREEN_SAVER);
        Intent i = new Intent(getActivity(), ScreenSaverActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void unregisterScreenReceiver() {
        if (mScreenReceiver != null) {
            getActivity().unregisterReceiver(mScreenReceiver);
            mScreenReceiver = null;
        }
    }

    private void delayGotoScreenSaver() {
        mHandler.removeMessages(MSG_GOTO_SCREEN_SAVER);
        mHandler.sendEmptyMessageDelayed(MSG_GOTO_SCREEN_SAVER, DELAY_INTERVAL);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d(TAG, "onKey " + keyCode + "," + event.getAction());
        if (event.getAction() == KeyEvent.ACTION_UP) {
            delayGotoScreenSaver();
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        delayGotoScreenSaver();
        return false;
    }

}
