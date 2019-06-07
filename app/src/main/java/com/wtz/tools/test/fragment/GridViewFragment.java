package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wtz.tools.R;
import com.wtz.tools.test.adapter.GridAdapter;
import com.wtz.tools.view.ScaleGridView;

import java.util.ArrayList;
import java.util.List;

public class GridViewFragment extends Fragment implements View.OnClickListener, View.OnKeyListener {
    private static final String TAG = GridViewFragment.class.getSimpleName();

    private ScaleGridView mGridView;
    private GridAdapter mGridAdapter;
    private List<String> mImageList = new ArrayList<>();
    private int mOldPosition;
    private int mNewPosition;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mIndex;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=9b867a04b299a9012f38537575fc600e/4d086e061d950a7b86bee8d400d162d9f2d3c913.jpg");
        mImageList.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=16f6ff0030292df583cea456d4583615/e1fe9925bc315c60b6b051c087b1cb13495477f3.jpg");
        mImageList.add("http://img.pptjia.com/image/20180117/f4b76385a3ccdbac48893cc6418806d5.jpg");
        mImageList.add("http://pic.qiantucdn.com/58pic/25/99/58/58aa038a167e4_1024.jpg");
        mImageList.add("http://imgsrc.baidu.com/imgad/pic/item/0bd162d9f2d3572c25e340088013632763d0c3e5.jpg");
        mImageList.add("http://pic9.photophoto.cn/20081229/0034034885643767_b.jpg");
        mImageList.add("http://pic5.photophoto.cn/20071115/0033033989381963_b.jpg");
        mImageList.add("http://img17.3lian.com/d/file/201702/15/6cf382f5814cd4a136d5c49527da456a.jpg");
        mImageList.add("http://pic20.photophoto.cn/20110920/0020032825361672_b.jpg");
        mImageList.add("http://pic2.16pic.com/00/11/78/16pic_1178392_b.jpg");
        mImageList.add("http://pic31.photophoto.cn/20140527/0034034458786248_b.jpg");
        mImageList.add("http://pic3.16pic.com/00/55/48/16pic_5548763_b.jpg");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        mGridView = view.findViewById(R.id.gridView);
        mGridAdapter = new GridAdapter(getActivity(), mImageList);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "mGridView onItemClick position=" + position);
            }
        });
        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected position=" + position);
                // 调用此自定义view里的方法，解决Item放大后，id靠前的Item放大后会被后面的遮盖问题
                mGridView.onItemSelected(parent, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
            }
        });
        mGridView.setOnKeyListener(this);
        mGridView.setSelection(0);
        mGridView.requestFocus();

        view.findViewById(R.id.btn_move_left).setOnClickListener(this);
        view.findViewById(R.id.btn_move_right).setOnClickListener(this);

        // 测试缩放方式2：演示自动切换选中条目，解决放大覆盖问题。注意：
        // 一旦触摸了屏幕，就进入了touch mode，此时调用setSelection无效！
        // 只要按了按键，就会退出touch mode，此时调用setSelection有效！
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mGridView.setSelection(mIndex);
                if (mIndex < mGridView.getCount() - 1) {
                    mIndex++;
                } else {
                    mIndex = 0;
                }
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, 1500);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_move_left:
                switchViewByButton(false);
                break;
            case R.id.btn_move_right:
                switchViewByButton(true);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.d(TAG, "onKey " + keyCode + "," + event.getAction());
        return false;
    }

    /**
     * 测试缩放方式1：
     * 会出现Item放大后，id靠前的Item放大后被后面的Item遮盖问题
     * @param add
     */
    private void switchViewByButton(boolean add) {
        boolean change = false;
        if (add) {
            if (mOldPosition < mGridView.getCount() - 1) {
                change = true;
                mNewPosition = mOldPosition + 1;
            }
        } else {
            if (mOldPosition > 0) {
                change = true;
                mNewPosition = mOldPosition - 1;
            }
        }
        Log.d(TAG, "switchViewByButton change=" + change);
        if (change) {
            View oldView = mGridView.getChildAt(mOldPosition);
            View newView = mGridView.getChildAt(mNewPosition);;
            Log.d(TAG, "switchViewByButton oldView=" + oldView + ",newView=" + newView);
            restoreScale(oldView);
            enlargeScale(newView);
            mOldPosition = mNewPosition;
        }
    }

    private void enlargeScale(View view) {
        if (view == null) return;
        Log.d(TAG, "enlargeScale:" + view);
        view.setScaleX(1.267f);
        view.setScaleY(1.267f);
    }

    private void restoreScale(View view) {
        if (view == null) return;
        Log.d(TAG, "restoreScale:" + view);
        view.setScaleX(1f);
        view.setScaleY(1f);
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
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

}
