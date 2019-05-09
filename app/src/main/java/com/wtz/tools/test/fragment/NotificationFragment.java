package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.test.data.FragmentItem;
import com.wtz.tools.utils.StorageUtil;
import com.wtz.tools.utils.data_transfer_format.XmlDemo;
import com.wtz.tools.utils.event.RxBus;
import com.wtz.tools.utils.event.RxBusFlowable;
import com.wtz.tools.utils.event.RxBusRelay;
import com.wtz.tools.utils.network.OkhttpWebSocket;
import com.wtz.tools.view.NotificationUtil;

public class NotificationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = NotificationFragment.class.getSimpleName();

    private NotificationUtil mNotificationUtil;
    
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mNotificationUtil = new NotificationUtil(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        view.findViewById(R.id.btn_noti_common).setOnClickListener(this);
        view.findViewById(R.id.btn_noti_download).setOnClickListener(this);
        view.findViewById(R.id.btn_noti_big_text).setOnClickListener(this);
        view.findViewById(R.id.btn_noti_big_pic).setOnClickListener(this);
        view.findViewById(R.id.btn_noti_inbox).setOnClickListener(this);
        view.findViewById(R.id.btn_noti_custom).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_noti_common:
                mNotificationUtil.postStandardNotification();
                break;
            case R.id.btn_noti_download:
                mNotificationUtil.postDownloadNotification();
                break;
            case R.id.btn_noti_big_text:
                mNotificationUtil.postBigTextNotification();
                break;
            case R.id.btn_noti_big_pic:
                mNotificationUtil.postBigPictureNotification();
                break;
            case R.id.btn_noti_inbox:
                mNotificationUtil.postInboxNotification();
                break;
            case R.id.btn_noti_custom:
                mNotificationUtil.postCustomNotification();
                break;
        }
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
