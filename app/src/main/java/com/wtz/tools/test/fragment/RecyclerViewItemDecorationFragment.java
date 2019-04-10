package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wtz.tools.R;
import com.wtz.tools.view.recycler_item_decoration.BookRankAdapter;
import com.wtz.tools.view.recycler_item_decoration.ColorDividerItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.FlagItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.GroupInfo;
import com.wtz.tools.view.recycler_item_decoration.SectionDecoration;
import com.wtz.tools.view.recycler_item_decoration.StickySectionDecoration;
import com.wtz.tools.view.recycler_item_decoration.RecyclerAdapter;
import com.wtz.tools.view.recycler_item_decoration.SimpleDividerItemDecoration;
import com.wtz.tools.view.recycler_item_decoration.TimelineItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考：https://github.com/frank909zhao/ItemDecorationDemo
 */
public class RecyclerViewItemDecorationFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = RecyclerViewItemDecorationFragment.class.getSimpleName();

    private RecyclerView.ItemDecoration mItemDecoration;

    private Button mBtnDivider;
    private SimpleDividerItemDecoration mSimpleDividerItemDecoration;

    private Button mBtnDivider1;
    private ColorDividerItemDecoration mColorDividerItemDecoration;

    private Button mBtnDivider2;
    private TimelineItemDecoration mTimelineItemDecoration;

    private Button mBtnDivider3;
    private FlagItemDecoration mFlagItemDecoration;

    private Button mBtnDivider4;
    private SectionDecoration mSectionDecoration;

    private Button mBtnDivider5;
    private StickySectionDecoration mStickySectionDecoration;

    private RecyclerView mRecyclerView;
    private List<String> data;
    private RecyclerView.Adapter mAdapter;
    private RecyclerAdapter mTestAdapter;
    private BookRankAdapter mBookRankAdapter;
    private int[] bookRankResouces;

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

        View view = inflater.inflate(R.layout.fragment_item_decoration, container, false);

        mBtnDivider = (Button) view.findViewById(R.id.btn_divider);
        mBtnDivider1 = (Button) view.findViewById(R.id.btn_divider1);
        mBtnDivider2 = (Button) view.findViewById(R.id.btn_divider2);
        mBtnDivider3 = (Button) view.findViewById(R.id.btn_divider3);
        mBtnDivider4 = (Button) view.findViewById(R.id.btn_divider4);
        mBtnDivider5 = (Button) view.findViewById(R.id.btn_divider5);
        mBtnDivider.setOnClickListener(this);
        mBtnDivider1.setOnClickListener(this);
        mBtnDivider2.setOnClickListener(this);
        mBtnDivider3.setOnClickListener(this);
        mBtnDivider4.setOnClickListener(this);
        mBtnDivider5.setOnClickListener(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.divider_recyclerview);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(getActivity());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutmanager);

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

    private void initDatas() {
        if (data == null) {
            data = new ArrayList<>();
        }
        for (int i = 0; i < 56; i++) {
            data.add(i + " test ");
        }
    }

    private void initDatasForBookRank() {
        if (data == null) {
            data = new ArrayList<>();
        }
        data.clear();
        data.add("人民的名义- ￥ 33.5");
        data.add("火车头 - ￥ 27.5");
        data.add("解忧杂货店- ￥ 19.9");
        data.add("TensorFlow - ￥ 102.5");
        data.add("王阳明心学 - ￥ 60");

        data.add("人民的名义1- ￥ 33.5");
        data.add("火车头1 - ￥ 27.5");
        data.add("解忧杂货店1- ￥ 19.9");
        data.add("TensorFlow1 - ￥ 102.5");
        data.add("王阳明心学1 - ￥ 60");

        data.add("人民的名义2 - ￥ 33.5");
        data.add("火车头2 - ￥ 27.5");
        data.add("解忧杂货店2- ￥ 19.9");
        data.add("TensorFlow2 - ￥ 102.5");
        data.add("王阳明心学2 - ￥ 60");

        if (bookRankResouces == null) {
            bookRankResouces = new int[]{R.drawable.book_renmin, R.drawable.book_huochetou,
                    R.drawable.book_jieyouzahuodian, R.drawable.book_tensoflow, R.drawable.book_wangyangming
                    , R.drawable.book_renmin, R.drawable.book_huochetou,
                    R.drawable.book_jieyouzahuodian, R.drawable.book_tensoflow, R.drawable.book_wangyangming
                    , R.drawable.book_renmin, R.drawable.book_huochetou,
                    R.drawable.book_jieyouzahuodian, R.drawable.book_tensoflow, R.drawable.book_wangyangming
            };
        }
    }

    @Override
    public void onClick(View v) {
        mRecyclerView.removeItemDecoration(mItemDecoration);
        switch (v.getId()) {
            case R.id.btn_divider:
                if (mSimpleDividerItemDecoration == null) {
                    mSimpleDividerItemDecoration = new SimpleDividerItemDecoration();
                }
                mItemDecoration = mSimpleDividerItemDecoration;
                break;
            case R.id.btn_divider1:
                if (mColorDividerItemDecoration == null) {
                    mColorDividerItemDecoration = new ColorDividerItemDecoration();
                }
                mItemDecoration = mColorDividerItemDecoration;
                break;
            case R.id.btn_divider2:
                if (mTimelineItemDecoration == null) {
                    mTimelineItemDecoration = new TimelineItemDecoration(getActivity());
                }
                mItemDecoration = mTimelineItemDecoration;
                break;
            case R.id.btn_divider3:
                if (mFlagItemDecoration == null) {
                    mFlagItemDecoration = new FlagItemDecoration(getActivity());
                }
                mItemDecoration = mFlagItemDecoration;
                break;
            case R.id.btn_divider4:
                if (mSectionDecoration == null) {
                    mSectionDecoration = new SectionDecoration(getActivity(), new SectionDecoration.GroupInfoCallback() {
                        @Override
                        public GroupInfo getGroupInfo(int position) {
                            return RecyclerViewItemDecorationFragment.this.getGroupInfo(position);
                        }
                    });
                }
                mItemDecoration = mSectionDecoration;
                break;
            case R.id.btn_divider5:
                if (mStickySectionDecoration == null) {
                    mStickySectionDecoration = new StickySectionDecoration(getActivity(), new StickySectionDecoration.GroupInfoCallback() {
                        @Override
                        public GroupInfo getGroupInfo(int position) {
                            return RecyclerViewItemDecorationFragment.this.getGroupInfo(position);
                        }
                    });
                }
                mItemDecoration = mStickySectionDecoration;
                break;

            default:
                break;
        }
        mRecyclerView.addItemDecoration(mItemDecoration);

        if (v.getId() == R.id.btn_divider3) {
            if (mBookRankAdapter == null) {
                initDatasForBookRank();
                mBookRankAdapter = new BookRankAdapter(data, bookRankResouces);
            }
            mAdapter = mBookRankAdapter;
        } else {
            if (mTestAdapter == null) {
                initDatas();
                mTestAdapter = new RecyclerAdapter(data);
            }
            mAdapter = mTestAdapter;
        }
        mRecyclerView.setAdapter(mAdapter);
    }

    @NonNull
    private GroupInfo getGroupInfo(int position) {
        /**
         * 分组逻辑，这里为了测试每5个数据为一组。大家可以在实际开发中
         * 替换为真正的需求逻辑
         */
        int groupId = position / 5;
        int index = position % 5;
        GroupInfo groupInfo = new GroupInfo(groupId, groupId + "");
        groupInfo.setPosition(index);
        return groupInfo;
    }

}
