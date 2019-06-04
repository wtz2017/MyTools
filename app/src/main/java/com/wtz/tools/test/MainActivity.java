package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.wtz.tools.R;
import com.wtz.tools.receiver.AppReceiver;
import com.wtz.tools.test.data.FragmentItem;
import com.wtz.tools.test.fragment.IndexFragment;
import com.wtz.tools.utils.event.RxBus;
import com.wtz.tools.utils.event.RxBusFlowable;
import com.wtz.tools.utils.event.RxBusRelay;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getName();

    private AppReceiver mAppReceiver;

    private Disposable mDisposableEvent1;
    private Disposable mDisposableEvent2;
    private Disposable mDisposableEvent3;

    private WindowManager mWindowManager;
    private ImageView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...savedInstanceState = " + savedInstanceState);
        // Make sure this line comes before calling super.onCreate().
        setTheme(R.style.AppTheme);// 从启动主题切换恢复正常主题
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        gotoIndexFragment();

        mAppReceiver = new AppReceiver(this);
        mAppReceiver.register();

        // Test RxBus
        mDisposableEvent1 = RxBus.registerOnMainThread(FragmentItem.class, new Consumer<FragmentItem>() {
            @Override
            public void accept(FragmentItem fragmentItem) throws Exception {
                Log.d(TAG, "RxBus accept FragmentItem: " + fragmentItem.toString());
            }
        });
        mDisposableEvent2 = RxBusFlowable.registerOnMainThread(FragmentItem.class, new Consumer<FragmentItem>() {
            @Override
            public void accept(FragmentItem fragmentItem) throws Exception {
                Log.d(TAG, "RxBusFlowable accept FragmentItem: " + fragmentItem.toString());
            }
        });
        mDisposableEvent3 = RxBusRelay.registerOnMainThread(FragmentItem.class, new Consumer<FragmentItem>() {
            @Override
            public void accept(FragmentItem fragmentItem) throws Exception {
                Log.d(TAG, "RxBusRelay accept FragmentItem: " + fragmentItem.toString());
            }
        });

        createFloatWindow();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart...");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume...");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause...");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop...");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy...");
        mAppReceiver.unRegister();
        RxBus.unregister(mDisposableEvent1);
        RxBusFlowable.unregister(mDisposableEvent2);
        RxBusRelay.unregister(mDisposableEvent3);
        destroyFloatWindow();
        super.onDestroy();
    }
    
    private void gotoIndexFragment() {
        try {
            Fragment fragment = new IndexFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_container, fragment);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFloatWindow() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        //layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 100;
        layoutParams.y = 100;

        mContentView = new ImageView(this);
        mContentView.setImageResource(android.R.drawable.ic_menu_add);
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch action：" + event.getAction());
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    // TODO do something
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                return false;
            }
        });

        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mContentView, layoutParams);
    }

    private void destroyFloatWindow() {
        if (mWindowManager != null && mContentView != null) {
            mWindowManager.removeView(mContentView);
        }
    }

}
