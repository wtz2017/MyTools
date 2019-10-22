package com.wtz.tools.view.custom_view_study;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wtz.tools.R;


public class CameraRotate extends View {

    private final String TAG = CameraRotate.class.getSimpleName();

    // 画笔
    private Paint mPaint = new Paint();

    // 宽高
    private int mWidth, mHeight;

    private Bitmap mBitmap;

    /**
     * 0:沿X轴旋转,1:沿Y轴旋转,2:沿Z轴旋转
     */
    private int mTestMode = 0;

    // 默认的动画周期
    private int mDefaultDuration = 3000;

    // 控制各个过程的动画
    private ValueAnimator mAnimator;

    // 动画过程监听器
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;

    // 当前动画进度
    private float mAnimatorValue = 0;

    public CameraRotate(Context context) {
        this(context, null);
    }

    public CameraRotate(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraRotate(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10f);

        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.poly_test);

        initAnimator();
        startAnimator();
    }

    private void initAnimator() {
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd");
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimator();
                    }
                }, 1000);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat");
            }
        };

        mAnimator = ValueAnimator.ofFloat(0, 1).setDuration(mDefaultDuration);
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.addListener(mAnimatorListener);
    }

    private void startAnimator() {
        mAnimator.start();
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

        // 将坐标系移动到画布中央
        canvas.translate(mWidth / 2, mHeight / 2);

        // 绘制坐标系轴
        CanvasAidUtils.set2DAxisLength(mWidth / 2, mWidth / 2, mHeight / 2, mHeight / 2);
        CanvasAidUtils.draw2DCoordinateSpace(canvas);

        // 开始旋转
        Camera camera = new Camera();
        camera.save();
        switch (mTestMode) {
            case 0:
                camera.rotateX(360 * mAnimatorValue);
                break;
            case 1:
                camera.rotateY(360 * mAnimatorValue);
                break;
            case 2:
                camera.rotateZ(360 * mAnimatorValue);
                break;
        }
        Matrix matrix = new Matrix();
        // 保存旋转矩阵参数
        camera.getMatrix(matrix);
        camera.restore();

        // 把图片移到坐标系中央
        matrix.preTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);

        // 为以上操作设置图片动作中心点
        setCenterPoint(matrix, 0, 0);

        canvas.drawBitmap(mBitmap, matrix, mPaint);
    }

    /**
     * 为之前的矩阵操作设置动作中心点，在其他矩阵操作最后调用
     *
     * @param matrix
     * @param x
     * @param y
     */
    private void setCenterPoint(Matrix matrix, float x, float y) {
        matrix.preTranslate(-x, -y);// 使用pre将旋转中心移动到和Camera位置相同。
        matrix.postTranslate(x, y);// 使用post将图片(View)移动到原来的位置
    }

    public void setTestMode(int mode) {
        mTestMode = mode;
        invalidate();
    }
}
