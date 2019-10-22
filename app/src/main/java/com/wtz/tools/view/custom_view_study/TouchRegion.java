package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * http://www.gcssloop.com/customview/touch-matrix-region
 * 1.开启硬件加速情况下 event.getX() 和 不开启情况下 event.getRawX() 等价，获取到的是屏幕(物理)坐标 (本文的锅)。
 * 2.开启硬件加速情况下 event.getRawX() 数值是一个错误数值，因为本身就是全局的坐标又叠加了一次 View 的偏移量，所以肯定是不正确的 (本文的锅)。
 * 3.从 Canvas 获取到的 Matrix 是全局的，默认情况下 x,y 偏移量始终为0，因此你不能从这里拿到当前 View 的偏移量 ( Matrix系列文章中的锅 )。
 * 4.手指触摸的坐标系和画布坐标系不统一，就可能引起手指触摸位置和绘制位置不统一;
 * 5.使用逆矩阵 mapPoints 将触摸位置与当前canvas坐标系移动位置中和掉，就实现了画出的触摸点位置与触摸坐标系位置一样显示
 */
public class TouchRegion extends CustomView {
    float down_x = -1;
    float down_y = -1;

    /**
     * 0:canvas坐标系,1:触摸坐标系
     */
    private int mTestMode = 0;

    public TouchRegion(Context context) {
        this(context, null);
    }

    public TouchRegion(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (mTestMode == 0) {
                    down_x = event.getX();
                    down_y = event.getY();
                } else {
                    // ▼ 注意此处使用全局坐标系，开启硬件加速情况下 event.getX() 和 不开启情况下 event.getRawX() 等价
                    if (isHardwareAccelerated()) {
                        down_x = event.getX();
                        down_y = event.getY();
                    } else {
                        down_x = event.getRawX();
                        down_y = event.getRawY();
                    }
                }

                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                down_x = down_y = -1;
                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float[] pts = {down_x, down_y};

        drawTouchCoordinateSpace(canvas);            // 绘制触摸坐标系，灰色
        // ▼注意画布平移
        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        drawTranslateCoordinateSpace(canvas);        // 绘制平移后的坐标系，红色

        if (pts[0] == -1 && pts[1] == -1) return;    // 如果没有就返回

        if (mTestMode == 1) {
            // ▼ 获得当前矩阵的逆矩阵
            Matrix invertMatrix = new Matrix();
            canvas.getMatrix().invert(invertMatrix);

            // ▼ 使用逆矩阵 mapPoints 将触摸位置与当前canvas坐标系移动位置中和掉，
            // 就实现了画出的触摸点位置与触摸坐标系位置一样显示
            invertMatrix.mapPoints(pts);
        }

        // 在触摸位置绘制一个小圆
        canvas.drawCircle(pts[0], pts[1], 20, mDeafultPaint);
    }

    /**
     * 绘制触摸坐标系，颜色为灰色，为了能够显示出坐标系，将坐标系位置稍微偏移了一点
     */
    private void drawTouchCoordinateSpace(Canvas canvas) {
        canvas.save();
        canvas.translate(10, 10);
        CanvasAidUtils.set2DAxisLength(500, 0, 500, 0);
        CanvasAidUtils.setLineColor(Color.GRAY);
        CanvasAidUtils.draw2DCoordinateSpace(canvas);
        canvas.restore();
    }

    /**
     * 绘制平移后的坐标系，颜色为红色
     */
    private void drawTranslateCoordinateSpace(Canvas canvas) {
        CanvasAidUtils.set2DAxisLength(500, 500, 300, 300);
        CanvasAidUtils.setLineColor(Color.RED);
        CanvasAidUtils.draw2DCoordinateSpace(canvas);
        CanvasAidUtils.draw2DCoordinateSpace(canvas);
    }

    public void setTestMode(int mode) {
        mTestMode = mode;
        invalidate();
    }
}
