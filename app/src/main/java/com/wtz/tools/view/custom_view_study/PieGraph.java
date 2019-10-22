package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * 参考：https://github.com/GcsSloop
 */
public class PieGraph extends View {
    private static final String TAG = PieGraph.class.getSimpleName();

    // 颜色表
    private int[] mColors = {0xFFCCFF00, 0xFF6495ED, 0xFFE32636, 0xFF800000, 0xFF808000, 0xFFFF8C69, 0xFF808080,
            0xFFE6B800, 0xFF7CFC00};
    // 饼状图初始绘制角度w
    private float mStartAngle = 0;
    // 数据
    private ArrayList<PieData> mPieDatas;
    // 宽高
    private int mWidth, mHeight;
    // 画笔
    private Paint mPaint = new Paint();

    public PieGraph(Context context) {
        this(context, null);
    }

    public PieGraph(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
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
        if (null == mPieDatas) {
            return;
        }

        float currentStartAngle = mStartAngle;                      // 当前起始角度
        canvas.translate(mWidth / 2, mHeight / 2);                  // 将画布坐标原点移动到中心位置
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.6);    // 饼状图半径
        RectF rect = new RectF(-r, -r, r, r);                       // 饼状图绘制区域

        for (int i = 0; i < mPieDatas.size(); i++) {
            PieData pie = mPieDatas.get(i);

            drawArc(canvas, currentStartAngle, rect, pie);
            drawNameValue(canvas, currentStartAngle, r, pie);

            currentStartAngle += pie.getAngle();
        }

    }

    private void drawArc(Canvas canvas, float currentStartAngle, RectF rect, PieData pie) {
        mPaint.setColor(pie.getColor());
        canvas.drawArc(rect, currentStartAngle, pie.getAngle(), true, mPaint);
    }

    private void drawNameValue(Canvas canvas, float currentStartAngle, float r, PieData pie) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(50);

        float angle = pie.getAngle() / 2 + currentStartAngle;
        // 角度转弧度：rad = deg * pi / 180
        double rad = angle * Math.PI / 180;
        // 斜边长度稍长于半径，为了字符不完全被饼图淹没
        float beveledEdge = 1.1f * r;
        float x = (float) (beveledEdge * Math.cos(rad));
        float y = (float) (beveledEdge * Math.sin(rad));
        Log.d(TAG, pie.getName() + ": angle=" + angle + ", rad=" + rad + ", x=" + x
                + ", y=" + y);

        String content = pie.getName() + ":" + (pie.getPercentage() * 100) + "%";
        canvas.drawText(content, x, y, textPaint);
    }

    // 设置起始角度
    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
        invalidate();   // 刷新
    }

    // 设置数据
    public void setData(ArrayList<PieData> mData) {
        this.mPieDatas = mData;
        initData(mData);
        invalidate();   // 刷新
    }

    // 初始化数据
    private void initData(ArrayList<PieData> mData) {
        if (null == mData || mData.size() == 0) {
            return;
        }

        float sumValue = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);

            sumValue += pie.getValue();       //计算数值和

            int j = i % mColors.length;       //设置颜色
            pie.setColor(mColors[j]);
        }

        float sumAngle = 0;
        for (int i = 0; i < mData.size(); i++) {
            PieData pie = mData.get(i);

            float percentage = pie.getValue() / sumValue;   // 百分比
            float angle = percentage * 360;                 // 对应的角度

            pie.setPercentage(percentage);                  // 记录百分比
            pie.setAngle(angle);                            // 记录角度大小
            sumAngle += angle;
        }
    }

}
