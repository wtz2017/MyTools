package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


/**
 * 绘制出第二个手指第位置
 * http://www.gcssloop.com/customview/multi-touch
 */
public class MultiTouchTest extends CustomView {
    private final String TAG = MultiTouchTest.class.getSimpleName();

    // 用于判断第2个手指是否存在
    boolean haveSecondPoint = false;

    // 记录第2个手指第位置
    PointF point = new PointF(0, 0);

    public MultiTouchTest(Context context) {
        this(context, null);
    }

    public MultiTouchTest(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDeafultPaint.setAntiAlias(true);
        mDeafultPaint.setTextAlign(Paint.Align.CENTER);
        mDeafultPaint.setTextSize(30);

        mDefaultTextPaint.setAntiAlias(true);
        mDefaultTextPaint.setTextAlign(Paint.Align.CENTER);
        mDefaultTextPaint.setTextSize(50);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                // 判断是否是第2个手指按下
                if (event.getPointerId(index) == 1) {
                    haveSecondPoint = true;
                    point.set(event.getX(), event.getY());
                }
                break;
            // 注意：当第一个手指也抬起后只剩第二个手指时，第二个手指index会变成0以补空缺，
            // 此时再抬起第二手指就会触发ACTION_UP
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // 判断抬起的手指是否是第2个
                if (event.getPointerId(index) == 1) {
                    haveSecondPoint = false;
                    point.set(0, 0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (haveSecondPoint) {
                    // 通过 pointerId 来获取 pointerIndex
                    int pointerIndex = event.findPointerIndex(1);
                    // 通过 pointerIndex 来取出对应的坐标
                    if (pointerIndex != -1 && pointerIndex < event.getPointerCount()) {
                        point.set(event.getX(pointerIndex), event.getY(pointerIndex));
                    } else {
                        Log.d(TAG, "findPointerIndex by id=1 failed!");
                    }
                }
                break;
        }

        invalidate();   // 刷新

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mViewWidth / 2, mViewHeight / 2);
        canvas.drawText("请按下多指，会追踪第2个按下手指的位置", 0, 0, mDefaultTextPaint);
        canvas.restore();

        // 如果屏幕上有第2个手指则绘制出来其位置
        if (haveSecondPoint) {
            canvas.drawCircle(point.x, point.y, 50, mDeafultPaint);
        }
    }
}