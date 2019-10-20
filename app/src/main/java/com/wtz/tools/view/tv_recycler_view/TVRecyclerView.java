package com.wtz.tools.view.tv_recycler_view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

/**
 * 使用注意：
 * ItemView 布局文件需要设置 android:focusable="true"
 */
public class TVRecyclerView extends RecyclerView {
    private static final String TAG = "TVRecyclerView";

    private static final int NO_FOCUS_DIRECTION = -1;
    private int needFocusDirection = NO_FOCUS_DIRECTION;

    /**
     * isFocusOutAble = true
     * <p>
     * what is the effect ?
     * <p>
     * for example if the orientation in layoutManager is horizontal
     * when the recyclerView scroll to end that the focus could be out of recyclerView
     * just effect the direction that load more able to trigger
     */
    private boolean isFocusOutAble = true;

    public TVRecyclerView(Context context) {
        this(context, null);
    }

    public TVRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TVRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: " + event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            resetFocusValue();
        } else {
            return super.dispatchKeyEvent(event);
        }

        View firstChild = null;
        if ((firstChild = this.getChildAt(0)) == null) {
            return super.dispatchKeyEvent(event);
        }

        LayoutParams childLP = (LayoutParams) firstChild.getLayoutParams();
        int offsetX = firstChild.getWidth() + childLP.leftMargin + childLP.rightMargin;
        int offsetY = firstChild.getHeight() + childLP.topMargin + childLP.bottomMargin;

        View focusView = this.getFocusedChild();
        int layoutDirection = getCurrentLayoutDirection();

        Log.d(TAG, "dispatchKeyEvent offsetX=" + offsetX + ", offsetY=" + offsetY
                + ", focusView=" + focusView);

        if (focusView != null) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                    if (layoutDirection == OrientationHelper.HORIZONTAL || (isFocusOutAble && downView == null && isRecyclerViewToBottom())) {
                        break;
                    }
                    if (downView != null) {
                        downView.requestFocusFromTouch();
                        downView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToBottom()) {
                            Log.d(TAG, "KEYCODE_DPAD_DOWN set needFocusDownView");
                            needFocusDirection = View.FOCUS_DOWN;
                        }
                        this.smoothScrollBy(0, offsetY);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_UP:
                    View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                    if (layoutDirection == OrientationHelper.HORIZONTAL || (upView == null && isRecyclerViewToTop())) {
                        break;
                    }
                    if (upView != null) {
                        upView.requestFocusFromTouch();
                        upView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToTop()) {
                            Log.d(TAG, "KEYCODE_DPAD_UP set needFocusUpView");
                            needFocusDirection = View.FOCUS_UP;
                        }
                        this.smoothScrollBy(0, -offsetY);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                    if (layoutDirection == OrientationHelper.VERTICAL || (isFocusOutAble && rightView == null && isRecyclerViewToRight())) {
                        break;
                    }
                    if (rightView != null) {
                        rightView.requestFocusFromTouch();
                        rightView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToRight()) {
                            Log.d(TAG, "KEYCODE_DPAD_UP set needFocusRightView");
                            needFocusDirection = View.FOCUS_RIGHT;
                        }
                        this.smoothScrollBy(offsetX, 0);
                        return true;
                    }
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                    if (layoutDirection == OrientationHelper.VERTICAL || (leftView == null && isRecyclerViewToLeft())) {
                        break;
                    }
                    if (leftView != null) {
                        leftView.requestFocusFromTouch();
                        leftView.requestFocus();
                        return true;
                    } else {
                        if (!isRecyclerViewToLeft()) {
                            Log.d(TAG, "KEYCODE_DPAD_UP set needFocusLeftView");
                            needFocusDirection = View.FOCUS_LEFT;
                        }
                        this.smoothScrollBy(-offsetX, 0);
                        return true;
                    }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void resetFocusValue() {
        needFocusDirection = NO_FOCUS_DIRECTION;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

        if (needFocusDirection != NO_FOCUS_DIRECTION) {
            View currentFocusView = getFocusedChild();
            if (currentFocusView != null) {
                View nextFocusView = FocusFinder.getInstance().findNextFocus(this, currentFocusView, needFocusDirection);
                if (nextFocusView != null) {
                    Log.d(TAG, "onScrolled requestFocus to direction " + needFocusDirection);
                    needFocusDirection = NO_FOCUS_DIRECTION;
                    nextFocusView.requestFocusFromTouch();
                    nextFocusView.requestFocus();
                }
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        final int tempFocusIndex = indexOfChild(getFocusedChild());
        if (tempFocusIndex == -1) {
            return i;
        }
        if (tempFocusIndex == i) {
            return childCount - 1;
        } else if (i == childCount - 1) {
            return tempFocusIndex;
        } else {
            return i;
        }
    }

    public void setFocusOutAble(boolean focusOutAble) {
        isFocusOutAble = focusOutAble;
    }

    public void setFocusFrontAble(boolean focusFront) {
        setChildrenDrawingOrderEnabled(focusFront);
    }

    private int getCurrentLayoutDirection() {
        int layoutDirection = 1;
        if (getLayoutManager() != null) {
            if (getLayoutManager() instanceof LinearLayoutManager) {
                layoutDirection = ((LinearLayoutManager) getLayoutManager()).getOrientation();
            } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutDirection = ((StaggeredGridLayoutManager) getLayoutManager()).getOrientation();
            }
        }
        return layoutDirection;
    }

    public boolean isRecyclerViewToTop() {
        return !canScrollVertically(-1);// 不能向下滚动
    }

    public boolean isRecyclerViewToBottom() {
        return !canScrollVertically(1);// 不能向上滚动
    }

    public boolean isRecyclerViewToLeft() {
        return !canScrollHorizontally(-1);// 不能向右滚动
    }

    public boolean isRecyclerViewToRight() {
        return !canScrollHorizontally(1);// 不能向左滚动
    }

}