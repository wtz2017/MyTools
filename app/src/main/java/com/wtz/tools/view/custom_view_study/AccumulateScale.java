package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class AccumulateScale extends View {

    // 画笔
    private Paint mPaint = new Paint();
    // 宽高
    private int mWidth, mHeight;

    public AccumulateScale(Context context) {
        this(context, null);
    }

    public AccumulateScale(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccumulateScale(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 将坐标系原点移动到画布正中心
        canvas.translate(mWidth / 2, mHeight / 2);

        // 矩形区域
        RectF rect = new RectF(-400, -400, 400, 400);

        // 缩放是可以叠加的
        for (int i = 0; i <= 20; i++) {
            canvas.scale(0.9f, 0.9f);
            canvas.drawRect(rect, mPaint);
        }
    }
}
