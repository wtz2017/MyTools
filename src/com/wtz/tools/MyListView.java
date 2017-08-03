package com.wtz.tools;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * ListView重新获取焦点时，让其重新选中上次被选的item，而不是就近选择。
 * 
 * 重新获取焦点分为两种情况： 1.首次创建listView时获取焦点；
 * 此时会使用外边通过initSelectItemPositionAndTop传入的期望初始位置；
 * 
 * 2.之前有焦点，丢失后又获取到焦点； 此时会自动获取上个选中的视图的位置来恢复；
 * 
 * 对于有多个listview的viewpager情况，首次创建页面时只能有一个获取焦点，
 * 本自定义listview只能管获取了焦点的listview，其它未获取焦点的listview还需要外界自己调用setSelectionFromTop设置初始位置。
 */
public class MyListView extends ListView {
    private static final String TAG = MyListView.class.getName();

    private int mInitSelectItemPosition;
    private int mInitSelectItemTop;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initSelectItemPositionAndTop(int position, int top) {
        mInitSelectItemPosition = position;
        mInitSelectItemTop = top;
    }

    @Override
    protected void onFocusChanged(boolean hasFocus, int direction, Rect previouslyFocusedRect) {
        View lastView = getSelectedView();
        int lastSelectItemPosition;
        int lastSelectItemTop;
        long lastId = 0;
        if (lastView == null) {
            // 首次创建
            lastSelectItemPosition = mInitSelectItemPosition;
            lastSelectItemTop = mInitSelectItemTop;
        } else {
            lastSelectItemPosition = getSelectedItemPosition();
            lastSelectItemTop = lastView.getTop();
            lastId = getSelectedItemId();
        }
        Log.d(TAG,
                "onFocusChanged...hasFocus = " + hasFocus + ", lastView = " + lastView
                        + ", lastSelectItemPosition = " + lastSelectItemPosition
                        + ", lastSelectItemTop = " + lastSelectItemTop);

        super.onFocusChanged(hasFocus, direction, previouslyFocusedRect);

        OnItemSelectedListener l = getOnItemSelectedListener();
        if (hasFocus) {
            setSelectionFromTop(lastSelectItemPosition, lastSelectItemTop);
            if (lastView != null) {
                if (l != null) {
                    l.onItemSelected(this, lastView, lastSelectItemPosition, lastId);
                }
            }
        } else {
            if (l != null) {
                l.onNothingSelected(this);
            }
        }
    }
}
