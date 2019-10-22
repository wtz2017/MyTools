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
 * https://github.com/GcsSloop
 */
public class PathBooleanOp extends View {

    // 画笔
    private Paint mDeafultPaint = new Paint();
    private Paint mTextPaint = new Paint();
    // 宽高
    private int mWidth, mHeight;

    public PathBooleanOp(Context context) {
        this(context, null);
    }

    public PathBooleanOp(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathBooleanOp(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDeafultPaint.setColor(Color.BLUE);
        mDeafultPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(50);
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

        int x = 80;
        int r = 100;

        canvas.translate(250,0);
        canvas.scale(0.76f, 0.76f);

        Path path1 = new Path();
        Path path2 = new Path();
        Path pathOpResult = new Path();

        path1.addCircle(-x, 0, r, Path.Direction.CW);
        path2.addCircle(x, 0, r, Path.Direction.CW);

        pathOpResult.op(path1,path2, Path.Op.DIFFERENCE);
        canvas.translate(0, 200);
        canvas.drawText("DIFFERENCE", 240,0,mTextPaint);
        canvas.drawPath(pathOpResult,mDeafultPaint);

        pathOpResult.op(path1,path2, Path.Op.REVERSE_DIFFERENCE);
        canvas.translate(0, 300);
        canvas.drawText("REVERSE_DIFFERENCE", 240,0,mTextPaint);
        canvas.drawPath(pathOpResult,mDeafultPaint);

        pathOpResult.op(path1,path2, Path.Op.INTERSECT);
        canvas.translate(0, 300);
        canvas.drawText("INTERSECT", 240,0,mTextPaint);
        canvas.drawPath(pathOpResult,mDeafultPaint);

        pathOpResult.op(path1,path2, Path.Op.UNION);
        canvas.translate(0, 300);
        canvas.drawText("UNION", 240,0,mTextPaint);
        canvas.drawPath(pathOpResult,mDeafultPaint);

        pathOpResult.op(path1,path2, Path.Op.XOR);
        canvas.translate(0, 300);
        canvas.drawText("XOR", 240,0,mTextPaint);
        canvas.drawPath(pathOpResult,mDeafultPaint);
    }
}
