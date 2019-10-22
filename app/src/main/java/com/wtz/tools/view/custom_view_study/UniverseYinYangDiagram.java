package com.wtz.tools.view.custom_view_study;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 太极阴阳图
 */
public class UniverseYinYangDiagram extends View {

    // 画笔
    private Paint mDeafultPaint = new Paint();
    // 宽高
    private int mWidth, mHeight;

    public UniverseYinYangDiagram(Context context) {
        this(context, null);
    }

    public UniverseYinYangDiagram(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UniverseYinYangDiagram(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDeafultPaint.setColor(Color.BLUE);
        mDeafultPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth / 2, mHeight / 2);

        // 画第一半
        Path path1 = new Path();
        Path path2 = new Path();
        Path path3 = new Path();
        Path path4 = new Path();

        path1.addCircle(0, 0, 200, Path.Direction.CW);
        path2.addRect(0, -200, 200, 200, Path.Direction.CW);
        path3.addCircle(0, -100, 100, Path.Direction.CW);
        path4.addCircle(0, 100, 100, Path.Direction.CCW);


        path1.op(path2, Path.Op.DIFFERENCE);
        path1.op(path3, Path.Op.UNION);
        path1.op(path4, Path.Op.DIFFERENCE);

        mDeafultPaint.setColor(Color.BLUE);
        canvas.drawPath(path1, mDeafultPaint);

        mDeafultPaint.setColor(Color.GREEN);
        canvas.drawCircle(0, -100, 30, mDeafultPaint);

        // 画另一半
        Path path5 = new Path();
        Path path6 = new Path();
        Path path7 = new Path();
        Path path8 = new Path();

        path5.addCircle(0, 0, 200, Path.Direction.CW);
        path6.addRect(0, -200, -200, 200, Path.Direction.CW);
        path7.addCircle(0, -100, 100, Path.Direction.CW);
        path8.addCircle(0, 100, 100, Path.Direction.CW);


        path5.op(path6, Path.Op.DIFFERENCE);
        path5.op(path7, Path.Op.DIFFERENCE);
        path5.op(path8, Path.Op.UNION);

        mDeafultPaint.setColor(Color.GREEN);
        canvas.drawPath(path5, mDeafultPaint);

        mDeafultPaint.setColor(Color.BLUE);
        canvas.drawCircle(0, 100, 30, mDeafultPaint);
    }
}
