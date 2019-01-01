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
import com.wtz.tools.utils.event.RxBus;
import com.wtz.tools.utils.event.RxBusFlowable;
import com.wtz.tools.utils.event.RxBusRelay;

public class CommonUtilFragment extends Fragment {
    private static final String TAG = CommonUtilFragment.class.getSimpleName();
    
    private TextView tv1;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        RxBus.getInstance().send(new FragmentItem("haha", "haha-class"));
        RxBusFlowable.getInstance().send(new FragmentItem("haha", "haha-class"));
        RxBusRelay.getInstance().send(new FragmentItem("haha", "haha-class"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_common_util, container, false);
        
        tv1 = (TextView) view.findViewById(R.id.tv1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("getTotalRunMemorySize = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getTotalRunMemorySize(getActivity()), false));
        stringBuilder.append("\r\n");
        
        stringBuilder.append("getAvailableRunMemory = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getAvailableRunMemory(getActivity()), false));
        stringBuilder.append("\r\n");
        
        stringBuilder.append("getTotalExternalMemorySize = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getTotalExternalMemorySize(), false));
        stringBuilder.append("\r\n");
        
        stringBuilder.append("getAvailableExternalMemorySize = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getAvailableExternalMemorySize(), false));
        stringBuilder.append("\r\n");
        
        stringBuilder.append("getTotalInternalMemorySize = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getTotalInternalMemorySize(), false));
        stringBuilder.append("\r\n");
        
        stringBuilder.append("getAvailableInternalMemorySize = ");
        stringBuilder.append(StorageUtil.formatFileSize(StorageUtil.getAvailableInternalMemorySize(), false));
        stringBuilder.append("\r\n");
        
        tv1.setText(stringBuilder.toString());

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
