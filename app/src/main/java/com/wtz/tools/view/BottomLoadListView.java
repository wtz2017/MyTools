package com.wtz.tools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.wtz.tools.R;

public class BottomLoadListView extends ListView implements AbsListView.OnScrollListener {

    View mFooter;
    int mLastVisiableItem;
    int mTotalItemCount;
    boolean isLoading;
    OnBottomLoadListener mOnBottomLoadListener;

    public BottomLoadListView(Context context) throws Exception {
        this(context, null);
    }

    public BottomLoadListView(Context context, AttributeSet attrs) throws Exception {
        this(context, attrs, 0);
    }

    public BottomLoadListView(Context context, AttributeSet attrs, int defStyleAttr) throws Exception {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomLoadListView);
        int footerResId = a.getResourceId(R.styleable.BottomLoadListView_footer, 0);
        if (footerResId == 0) throw new Exception("Can't find footer resource id");
        a.recycle();

        initView(context, footerResId);
    }

    /***
     * 添加底部提示加载布局到listView
     *
     * @param context
     * @param footerResId
     */
    private void initView(Context context, int footerResId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mFooter = inflater.inflate(footerResId, null);
        mFooter.setVisibility(View.GONE);
        this.addFooterView(mFooter);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mTotalItemCount == mLastVisiableItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                mFooter.setVisibility(View.VISIBLE);
                if (mOnBottomLoadListener != null) {
                    mOnBottomLoadListener.onLoad();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mLastVisiableItem = firstVisibleItem + visibleItemCount;
        this.mTotalItemCount = totalItemCount;
    }

    /**
     * 加载完毕将footer隐藏
     */
    public void loadComplete(){
        isLoading=false;
        mFooter.setVisibility(View.GONE);
    }

    public void setOnBottomLoadListener(OnBottomLoadListener listener) {
        this.mOnBottomLoadListener = listener;
    }

    public interface OnBottomLoadListener {
        void onLoad();
    }

}
