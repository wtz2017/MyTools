package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.wtz.tools.R;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getName();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate...savedInstanceState = " + savedInstanceState);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        gotoIndexFragment();
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
