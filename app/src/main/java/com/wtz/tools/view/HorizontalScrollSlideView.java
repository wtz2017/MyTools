package com.wtz.tools.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * https://blog.csdn.net/mythace/article/details/79251428
 */
public class HorizontalScrollSlideView extends LinearLayout implements OnScrollChangedListener {
    private static final String TAG = "HorizontalSlideView";

    //移动触发步幅
    private final int MOVE_STRIDE = 6;
    //记录移动x
    private float mRecodX;
    //移动的距离
    private float mOffsetX;

    //底部分界线位置
    private int mBottomParting;
    //底部展示区长度
    private int mBottomShow;
    //底部触发区长度
    private int mBottomAll;
    //是否有触摸
    private boolean isDown = false;

    private Handler mHandler;

    //滑动触发的监听
    private OnSlideBottomListener mOnSlideBottomListener;

    //内容外部的滑动view
    private ObservableScrollView mScroolView;

    //包裹内容view
    private LinearLayout mContentView;

    //底部展示view
    private View mBottomShowView;
    private int mBottomShowViewWidth;


    //底部触发到监听的view
    private View mBottomGoView;
    private int mBottomGoViewWidth;

    private boolean needScrollBottom = true;

    public HorizontalScrollSlideView(Context context) {
        this(context, null);
    }

    public HorizontalScrollSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHandler = new Handler();

        mContentView = new LinearLayout(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(lp);

        mScroolView = new ObservableScrollView(context);
        mScroolView.setLayoutParams(new ViewGroup.LayoutParams(lp));
        mScroolView.setHorizontalScrollBarEnabled(false);

        mScroolView.addView(mContentView);
        addView(mScroolView);
    }

    /**
     * 设置滑动区的内容
     *
     * @param views
     */
    public void setContentViews(List<View> views) {
        mContentView.removeAllViews();
        for (View view : views) {
            mContentView.addView(view);
        }
    }


    public void setContentView(View view) {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }


    public ViewGroup getContentContainer() {
        return mContentView;
    }

    /**
     * 设置触发goveiw的监听
     *
     * @param listener
     */
    public void setOnSlideBottomListener(OnSlideBottomListener listener) {
        mOnSlideBottomListener = listener;
    }

    /**
     * 覆盖后，返回自定义底部view
     *
     * @return 底部展现view
     */
    protected View getBottomShowView() {
        TextView textView = new TextView(getContext());
        textView.setText("继续滑动\n查看全部");
        textView.setGravity(Gravity.CENTER);
        textView.setClickable(false);
        textView.setEnabled(false);
        textView.setTextColor(Color.BLUE);

        int width = dp2px(100);
        mBottomShowViewWidth = width;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp);
        return textView;
    }

    /**
     * 覆盖后，返回自定义底部触发view
     *
     * @return 底部触发view
     */
    protected View getBottomGoView() {
        TextView textView = new TextView(getContext());
        textView.setText("->");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLUE);

        int width = dp2px(20);
        mBottomGoViewWidth = width;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(lp);
        return textView;
    }

    @Override
    protected void onFinishInflate() {
        Log.i(TAG, "onFinishInflate");
        super.onFinishInflate();
        mScroolView.setOnScrollListener(this);

        View showView = getBottomShowView();
        if (showView != null) {
            addView(showView);
            mBottomShowView = showView;
        }

        View goView = getBottomGoView();
        if (goView != null) {
            addView(goView);
            mBottomGoView = goView;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        Log.i(TAG, "onMeasure...mBottomShowView:" + mBottomShowView.getWidth() + ",mBottomGoView:" + mBottomGoView.getWidth());
        if (mBottomShowView.getWidth() > 0 && mBottomGoView.getWidth() > 0) {
            mBottomShow = mBottomShowView.getWidth();
            mBottomParting = mBottomShow / 2;
            mBottomAll = mBottomShow + mBottomGoView.getWidth();
        } else {
            mBottomShow = mBottomShowViewWidth;
            mBottomParting = mBottomShow / 2;
            mBottomAll = mBottomShow + mBottomGoViewWidth;
        }
    }

    @Override
    public void onScrollChanged(int x, int y, int oldX, int oldY) {
//        Log.i(TAG, "onScrollChanged:" + x + "," + y + "," + oldX + "," + oldY);
        if (!isDown && x > oldX && isScrollBottom(true)) {
            Log.i(TAG, "onScrollChanged to setScrollX " + mBottomShow);
            setScrollX(mBottomShow);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(TAG, "dispatchTouchEvent: " + ev.getAction());
        if (isScrollBottom(true) || getScrollX() > 0) {
            handleTouch(ev);
        } else {
            mRecodX = ev.getX();
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isDown = true;
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            isDown = false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i(TAG, "onTouchEvent:" + event.getAction());
        //消费掉，保证dispatchTouchevent
        if (needScrollBottom) {
            ViewParent parent = this;
            while (parent != null && !((parent = parent.getParent()) instanceof ViewPager)) {
//                Log.d(TAG, "parent:" + parent);
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = isScrollContentBottom() && ev.getAction() == MotionEvent.ACTION_MOVE;
//        Log.i(TAG, "onInterceptTouchEvent: " + ev.getAction() + "  isINtercept:" + isIntercept);
        if (isIntercept)
            getParent().requestDisallowInterceptTouchEvent(true);
        return isIntercept ? true : super.onInterceptTouchEvent(ev);
    }

    private boolean isScrollBottom(boolean isIncludeEqual) {
        // 获取mScroolView当前的ScrollX
        int sx = mScroolView.getScrollX();
        // 获取mContentView的宽
        int cwidth = mScroolView.getChildAt(0).getWidth();
        // 获取当前自定义控件本身的宽
        int pwidth = getWidth();
//        Log.i(TAG, "sx: " + sx + ",cwidth: " + cwidth + ",pwidth: " + pwidth);

        if (needScrollBottom)
            return isIncludeEqual ? sx + pwidth >= cwidth : sx + pwidth > cwidth;
        else
            return false;
    }

    public void setNeedScrollBottom(boolean needScrollBottom) {
        this.needScrollBottom = needScrollBottom;
    }

    private boolean isScrollContentBottom() {
        return getScrollX() > 0;
    }

    private boolean handleTouch(MotionEvent event) {
//        Log.i(TAG, "handletouch: " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRecodX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mRecodX == 0)
                    mRecodX = event.getX();
                //移动的距离
                mOffsetX = (event.getX() - mRecodX);
                //是否达移动最小值
                if (Math.abs(mOffsetX) < MOVE_STRIDE) {
                    return true;
                }
                //手指左滑
                boolean isLeft = event.getX() - mRecodX < 0;
                mRecodX = event.getX();
                if (isLeft && getScrollX() >= mBottomAll) {
                    // 已经向左滑到露出最右边
                    setScrollX(mBottomAll);
                    //Log.i(TAG,"1");
                } else if (!isLeft && getScrollX() <= 0) {
                    // 已经向右滑到露出最左边
                    setScrollX(0);
                    //Log.i(TAG,"2");
                } else {
                    // 移动中，向左移动时，mOffsetX < 0，对于ScrollX应该是加正值
                    setScrollX((int) (getScrollX() - mOffsetX));
                    //Log.i(TAG,"3");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (getScrollX() < mBottomParting) {
                    // 松手时ScrollX小于“提示加载更多”的一半，就回弹，不显示“提示加载更多”
                    setScrollX(0);
                } else {
                    // 松手时ScrollX大于等于“提示加载更多”的一半，就完全展示“提示加载更多”
                    int delay = 0;
                    if (getScrollX() >= mBottomAll - MOVE_STRIDE) {
                        // 松手时Scroll到能完全看见“加载更多的箭头”，说明到底了，需要延迟回弹到只显示“提示加载更多”，模拟加载耗时
                        Log.i(TAG, "slide bottom!");
                        if (mOnSlideBottomListener != null) {
                            mOnSlideBottomListener.onSlideBottom();
                        }
                        delay = 1000;
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "MotionEvent.ACTION_UP setScrollX:" + mBottomShow);
                            setScrollX(mBottomShow);
                        }
                    }, delay);

                }
                break;
        }
        return true;
    }

    int dp2px(int dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public interface OnSlideBottomListener {
        void onSlideBottom();
    }

    static class ObservableScrollView extends HorizontalScrollView {
        private OnScrollChangedListener onScrollChangedListener;

        public ObservableScrollView(Context context) {
            super(context);
        }

        public ObservableScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener) {
            this.onScrollChangedListener = onScrollChangedListener;
        }

        @Override
        protected void onScrollChanged(int x, int y, int oldX, int oldY) {
            super.onScrollChanged(x, y, oldX, oldY);
            if (onScrollChangedListener != null) {
                onScrollChangedListener.onScrollChanged(x, y, oldX, oldY);
            }
        }

    }

}

interface OnScrollChangedListener {
    void onScrollChanged(int x, int y, int oldX, int oldY);
}