package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wtz.tools.R;

public class MatrixBasicUse extends View {

    // 画笔
    private Paint mPaint = new Paint();
    // 宽高
    private int mWidth, mHeight;

    private Bitmap mBitmap;
    private Matrix mMatrix;

    /**
     * 0:原图,1:平移,2:旋转,3:缩放,4:错切
     */
    private int mTestMode = 0;

    public MatrixBasicUse(Context context) {
        this(context, null);
    }

    public MatrixBasicUse(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixBasicUse(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);

        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.matrix);
        mMatrix = new Matrix();
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
        canvas.translate(50,50);

        // 绘制坐标系
        CanvasAidUtils.set2DAxisLength(900, 0, 1200, 0);
        CanvasAidUtils.draw2DCoordinateSpace(canvas);

        mMatrix.reset();
        switch (mTestMode) {
            case 0:
                break;
            case 1:
                mMatrix.preTranslate(100, 100);
                break;
            case 2:
//                mMatrix.preRotate(90);
//                mMatrix.preTranslate(0, -mBitmap.getHeight());
                mMatrix.preRotate(90, mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
                break;
            case 3:
                mMatrix.preScale(0.7f, 0.7f);
                break;
            case 4:
                mMatrix.preSkew(0.2f, 0.2f);
                break;
        }
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
    }

    public void setTestMode(int mode) {
        mTestMode = mode;
        invalidate();
    }
}
