package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...savedInstanceState = " + savedInstanceState);
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

}
