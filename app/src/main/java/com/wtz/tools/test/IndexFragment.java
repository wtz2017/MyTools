package com.wtz.tools.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wtz.tools.R;
import com.wtz.tools.test.adapter.IndexListAdapter;
import com.wtz.tools.test.data.FragmentItem;

public class IndexFragment extends Fragment implements OnItemClickListener{
    private final static String TAG = IndexFragment.class.getName();
    
    private ArrayList<FragmentItem> mFragmentList;
    
    private ListView mListView;
    private IndexListAdapter mListAdapter;
    
    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        
        initFragmentListData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_index, container, false);
        
        initView(view);

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
    
    private void initFragmentListData() {
        // TODO ADD NEW ITEM
        mFragmentList = new ArrayList<FragmentItem>();
        mFragmentList.add(new FragmentItem("StorageUtil", StorageUtilFragment.class.getName()));
        mFragmentList.add(new FragmentItem("StorageUtil", StorageUtilFragment.class.getName()));
        mFragmentList.add(new FragmentItem("StorageUtil", StorageUtilFragment.class.getName()));
        
    }

    private void initView(View parent) {
        mListAdapter = new IndexListAdapter(getActivity(), mFragmentList);
        mListView = (ListView) parent.findViewById(R.id.lv_index_list);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setSelection(0);
        mListView.requestFocus();
    }

    private void gotoFragment(FragmentItem item) {
        try {
            Log.d(TAG, "gotoFragment...item class name = " + item.fragmentClassName);
            Class<?> clazz = Class.forName(item.fragmentClassName);
            Fragment fragment = (Fragment) clazz.newInstance();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fl_container, fragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick...position = " + position);
        gotoFragment(mFragmentList.get(position));
    }
}