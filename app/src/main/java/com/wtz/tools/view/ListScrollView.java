package com.wtz.tools.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 可以嵌套ListView的ScrollView
 */
public class ListScrollView extends ScrollView {
    private static final String TAG = "ListScrollView";

    /**
     * 头部图片高度
     */
    private int headHeight;

    private ListView listView;

    private int currentTop;
    private boolean isUp;

    public ListScrollView(Context context) {
        super(context);
        init(context);
    }

    public ListScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    public void setHeadHeight(int headHeight) {
        this.headHeight = headHeight;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.d(TAG, "onScrollChanged t=" + t + ",headHeight=" + headHeight);
        currentTop = t;
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private float oldX;
    private float oldY;
    private float currentX;
    private float currentY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();
                oldY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = ev.getX();
                currentY = ev.getY();
                checkDirection();
                break;
            case MotionEvent.ACTION_UP:
                currentX = ev.getX();
                currentY = ev.getY();
                checkDirection();
                break;
        }

        // 解决ScrollView嵌套ListView滑动冲突问题
        if (isUp) {
            if (currentTop >= headHeight) {
                // 顶部背景图消失时，不拦截事件，交由嵌套的listView处理
                requestDisallowInterceptTouchEvent(true);
            } else {
                requestDisallowInterceptTouchEvent(false);
            }
        } else {
            if (listView.getFirstVisiblePosition() == 0) {
                // listView下拉滑动到顶部第一条显示时交由scrollView处理事件
                requestDisallowInterceptTouchEvent(false);
            } else {
                requestDisallowInterceptTouchEvent(true);
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void checkDirection() {
        if (currentX - oldX > 0) {
            Log.d(TAG, "向右");
        } else {
            Log.d(TAG, "向左");
        }
        if (currentY - oldY > 0) {
            Log.d(TAG, "向下");
            isUp = false;
        } else {
            Log.d(TAG, "向上");
            isUp = true;
        }
    }

}
