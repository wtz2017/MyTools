package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wtz.tools.R;
import com.wtz.tools.utils.DialogDemo;

public class DialogDemoFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = DialogDemoFragment.class.getSimpleName();

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

        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        view.findViewById(R.id.btn1).setOnClickListener(this);
        view.findViewById(R.id.btn2).setOnClickListener(this);
        view.findViewById(R.id.btn3).setOnClickListener(this);
        view.findViewById(R.id.btn4).setOnClickListener(this);
        view.findViewById(R.id.btn5).setOnClickListener(this);
        view.findViewById(R.id.btn6).setOnClickListener(this);
        view.findViewById(R.id.btn7).setOnClickListener(this);
        view.findViewById(R.id.btn8).setOnClickListener(this);
        view.findViewById(R.id.btn9).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn1:
                DialogDemo.showCommonDialog(getActivity());
                break;
            case R.id.btn2:
                DialogDemo.showListDialog(getActivity());
                break;
            case R.id.btn3:
                DialogDemo.showSingleOptionDialog(getActivity());
                break;
            case R.id.btn4:
                DialogDemo.showMultiOptionDialog(getActivity());
                break;
            case R.id.btn5:
                DialogDemo.showHalfCustomDialog(getActivity());
                break;
            case R.id.btn6:
                DialogDemo.showCustomDialog(getActivity());
                break;
            case R.id.btn7:
                DialogDemo.showCircleProgressDialog(getActivity());
                break;
            case R.id.btn8:
                DialogDemo.showHorizontalProgressDialog(getActivity());
                break;
            case R.id.btn9:
                DialogDemo.showBottomSheetDialog(getActivity());
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
