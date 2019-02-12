package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.wtz.tools.R;
import com.wtz.tools.view.ToastUtils;

public class DrawerLayoutFragment extends Fragment {

    private static final String TAG = DrawerLayoutFragment.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    
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

        View view = inflater.inflate(R.layout.fragment_drawerlayout, container, false);

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mNavigationView);
            }
        });

        mNavigationView = (NavigationView) view.findViewById(R.id.navigationView);
        mNavigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showTopToast(getActivity(),"head is clicked!");
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.access_point:
                        ToastUtils.showTopToast(getActivity(),"access_point is clicked!");
                        break;
                    case R.id.account:
                        ToastUtils.showTopToast(getActivity(),"account is clicked!");
                        break;
                    case R.id.airplane:
                        ToastUtils.showTopToast(getActivity(),"airplane is clicked!");
                        break;
                    case R.id.alarm:
                        ToastUtils.showTopToast(getActivity(),"alarm is clicked!");
                        break;
                    case R.id.amazon_drive:
                        ToastUtils.showTopToast(getActivity(),"amazon_drive is clicked!");
                        break;
                    case R.id.download:
                        ToastUtils.showTopToast(getActivity(),"download is clicked!");
                        break;
                    case R.id.bike:
                        ToastUtils.showTopToast(getActivity(),"bike is clicked!");
                        break;
                }
                return true;
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
