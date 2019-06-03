package com.wtz.tools.test.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wtz.tools.R;
import com.wtz.tools.test.FileChooserActivity;
import com.wtz.tools.test.WebViewActivity;
import com.wtz.tools.test.aac.view.CityIpActivity;
import com.wtz.tools.test.adapter.IndexListAdapter;
import com.wtz.tools.test.data.FragmentItem;
import com.wtz.tools.utils.ScreenUtils;
import com.wtz.tools.utils.SystemInfoUtils;
import com.wtz.tools.view.BottomLoadListView;
import com.wtz.tools.view.ListScrollView;

public class IndexFragment extends Fragment implements OnItemClickListener {
    private final static String TAG = IndexFragment.class.getName();
    
    private ArrayList<FragmentItem> mFragmentList;

    private ListScrollView mScrollView;
    private BottomLoadListView mListView;
    private IndexListAdapter mListAdapter;

    private static final float HOME_BG_WH_SCALE = 1.64f;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean isFirstShow = true;
    
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

        mScrollView = view.findViewById(R.id.sv_root);
        int[] wh = SystemInfoUtils.getScreenPixels(getActivity());
        int headHeight = (int) (wh[0] / HOME_BG_WH_SCALE);
        Log.d(TAG, "headHeight=" + headHeight);
        mScrollView.setHeadHeight(headHeight);

        ImageView homeBg = view.findViewById(R.id.iv_home_bg);
        LinearLayout.LayoutParams homeLp = (LinearLayout.LayoutParams) homeBg.getLayoutParams();
        homeLp.height = headHeight;
        homeBg.setLayoutParams(homeLp);

        int titleHeight = ScreenUtils.dip2px(getActivity(), 60);
        Log.d(TAG, "titleHeight=" + titleHeight);
        View titleView = view.findViewById(R.id.v_title);
        LinearLayout.LayoutParams titleLp = (LinearLayout.LayoutParams) titleView.getLayoutParams();
        titleLp.height = titleHeight;
        titleView.setLayoutParams(titleLp);

        mListView = (BottomLoadListView) view.findViewById(R.id.lv_index_list);
        mScrollView.setListView(mListView);
        LinearLayout.LayoutParams listLp = (LinearLayout.LayoutParams) mListView.getLayoutParams();
        int statusBarHeight = ScreenUtils.getStatusBarHeight(getActivity());
        listLp.height = wh[1] - statusBarHeight - titleHeight;
        Log.d(TAG, "statusBar Height=" + statusBarHeight + ",list Height=" + listLp.height);
        mListView.setLayoutParams(listLp);
        initListViewData(view);

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
        if (isFirstShow) {
            // 解决首次进入页面ScrollView内容不在顶部问题
            isFirstShow = false;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(0, 0);
                }
            }, 0);
        }
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
    
    private void initFragmentListData() {
        mFragmentList = new ArrayList<FragmentItem>();
        mFragmentList.add(new FragmentItem("通用工具测试", CommonUtilFragment.class.getName()));
        mFragmentList.add(new FragmentItem("SurfaceView", SurfaceViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("CameraView", CameraViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("SurfaceVideoView", SurfaceVideoViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("TextureVideoView", TextureVideoViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("IjkPlayer", IjkPlayerFragment.class.getName()));
        mFragmentList.add(new FragmentItem("图片框架", ImageFrameworkFragment.class.getName()));
        mFragmentList.add(new FragmentItem("GridViewLayout", GridViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("RecyclerViewItemDecoration", RecyclerViewItemDecorationFragment.class.getName()));
        mFragmentList.add(new FragmentItem("RecyclerLoadMore", RecyclerLoadMoreFragment.class.getName()));
        mFragmentList.add(new FragmentItem("GalleryRecyclerView", GalleryRecyclerViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("HorizontalScrollSlideView", HorizontalScrollFragment.class.getName()));
        mFragmentList.add(new FragmentItem("ViewPager", ViewPagerFragment.class.getName()));
        mFragmentList.add(new FragmentItem("DrawerLayout", DrawerLayoutFragment.class.getName()));
        mFragmentList.add(new FragmentItem("下拉刷新", PullRefreshFragment.class.getName()));
        mFragmentList.add(new FragmentItem("左拉抽屉", SwipeLayoutFragment.class.getName()));
        mFragmentList.add(new FragmentItem("Seekbar", SeekbarAsyncTaskFragment.class.getName()));
        mFragmentList.add(new FragmentItem("LoadingProgress", LoadingProgressFragment.class.getName()));
        mFragmentList.add(new FragmentItem("LoadingTextView", LoadingTextViewFragment.class.getName()));
        mFragmentList.add(new FragmentItem("SwitchButton", SwitchButtonFragment.class.getName()));
        mFragmentList.add(new FragmentItem("打开帷幕", PullCurtainFragment.class.getName()));
        mFragmentList.add(new FragmentItem("动画演示", AnimationFragment.class.getName()));
        mFragmentList.add(new FragmentItem("折叠日历", CollapseCalendarFragment.class.getName()));
        mFragmentList.add(new FragmentItem("PopupWindow", PopupWindowFragment.class.getName()));
        mFragmentList.add(new FragmentItem("Dialog", DialogDemoFragment.class.getName()));
        mFragmentList.add(new FragmentItem("Notification", NotificationFragment.class.getName()));
        mFragmentList.add(new FragmentItem("WebView", WebViewActivity.class.getName(), true, WebViewActivity.class));
        mFragmentList.add(new FragmentItem("CityIp", CityIpActivity.class.getName(), true, CityIpActivity.class));
        mFragmentList.add(new FragmentItem("文件选择", FileChooserActivity.class.getName(), true, FileChooserActivity.class));
    }

    private void initListViewData(View parent) {
        mListAdapter = new IndexListAdapter(getActivity(), mFragmentList);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnBottomLoadListener(new BottomLoadListView.OnBottomLoadListener() {
            @Override
            public void onLoad() {
                Log.d(TAG, "onLoad...test");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "load...complete");
                        mListView.loadComplete();
                    }
                }, 5000);
            }
        });

        mListView.setSelection(0);
        mListView.requestFocus();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick...position = " + position);
        FragmentItem item = mFragmentList.get(position);
        if (item.isActivity) {
            gotoActivity(item);
        } else {
            gotoFragment(item);
        }
    }

    private void gotoActivity(FragmentItem item) {
        Intent i = new Intent(getActivity(), item.clazz);
        startActivity(i);
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

}
