package com.wtz.tools.view.custom_view_study;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wtz.tools.R;

public class Zombie extends View {

    private static final String TAG = Zombie.class.getSimpleName();

    private Context mContext;           // 上下文
    private int mWidth, mHeight;        // 宽高
    private Handler mHandler;           // handler

    private Paint mPaint;
    private Bitmap mBitmapSrc;
    private int mZombieWidth;
    private int mZombieHeight;

    private int animCurrentPage = 0;       // 当前页码
    private int animMaxPage = 22;           // 总页数
    private int animDuration = 6000;         // 动画时长

    private boolean isStart = false;        // 是否启动

    public Zombie(Context context) {
        this(context, null);
    }

    public Zombie(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Zombie(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {
        mContext = context;

        mPaint = new Paint();
        mPaint.setColor(0xffFF5317);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mBitmapSrc = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.zombie);
        // 得出图像边长
        mZombieWidth = mBitmapSrc.getWidth() / 11;
        mZombieHeight = mBitmapSrc.getHeight() / 2;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (animCurrentPage <= animMaxPage - 1 && animCurrentPage >= 0) {
                    invalidate();
                    removeMessages(0);
                    if (animCurrentPage < animMaxPage - 1) {
                        animCurrentPage++;
                        this.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
                    } else {
                        isStart = false;
                    }
                    Log.e(TAG, "animCurrentPage=" + animCurrentPage);
                }
            }
        };
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

        // 移动坐标系到画布中央
        canvas.translate(mWidth / 2, mHeight / 2);

        // 得到图像选区 和 实际绘制位置
        int left = mZombieWidth * (animCurrentPage % 11);
        int top = mZombieHeight * (animCurrentPage / 11);
        int right = mZombieWidth * ((animCurrentPage + 1) % 11);
        if (animCurrentPage == 10 || animCurrentPage == 21) {
            // 此处不处理会出现反转，因为right变0比left还要小了
            right = mZombieWidth * 11;
        }
        int bottom = mZombieHeight * (animCurrentPage / 11 + 1);
        Rect src = new Rect(left, top, right, bottom);
        Rect dst = new Rect(-200, -200, 200, 200);

        // 绘制
        canvas.drawBitmap(mBitmapSrc, src, dst, null);
    }

    public boolean isStart() {
        return isStart;
    }

    /**
     * 开始
     */
    public void start() {
        if (isStart) {
            return;
        }
        isStart = true;
        animCurrentPage = 0;
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 设置动画时长
     * @param animDuration
     */
    public void setAnimDuration(int animDuration) {
        if (animDuration <= 0)
            return;
        this.animDuration = animDuration;
    }

}
