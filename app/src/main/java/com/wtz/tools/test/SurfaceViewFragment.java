package com.wtz.tools.test;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wtz.tools.R;

public class SurfaceViewFragment extends Fragment {
    private static final String TAG = SurfaceViewFragment.class.getSimpleName();

    private MySurfaceView mMySurfaceView;

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.fragment_surfaceview, container, false);

        mMySurfaceView = new MySurfaceView(getActivity());
        rootView.addView(mMySurfaceView);

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }


    /**
     * 实现SurfaceHolder.Callback接口中的三个方法，都是在主线程中调用，而不是在绘制线程中调用的
     */
    class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

        private SurfaceHolder mSurfaceHolder;
        private Thread mDrawThread;
        private boolean canDraw;
        private Paint mPaint;
        private Path mPath;

        public MySurfaceView(Context context) {
            super(context);

            // 通过SurfaceView获得SurfaceHolder对象
            mSurfaceHolder = getHolder();

            // 为holder添加回调SurfaceHolder.Callback
            mSurfaceHolder.addCallback(this);

            setFocusable(true);
            setFocusableInTouchMode(true);
            setKeepScreenOn(true);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated");
            // 当这个方法调用时，说明Surface已经有效了，可以启动绘画线程了
            canDraw = true;

            // 创建一个绘制线程，将holder对象作为参数传入，
            // 这样在绘制线程中就可以进一步获得Canvas对象，并在Canvas上进行绘制
            mDrawThread = new Thread(this);
            mDrawThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed");
            // 当这个方法调用时，说明Surface即将要被销毁了，需要结束线程
            canDraw = false;
            mDrawThread.interrupt();

            // 在绘制视频和销毁的过程中都要加上同步锁，防止SurfaceView被销毁时已经lock出来的canvas没有unlock
            // 否则，SurfaceView再次创建时会出现等待lock的情况，导致出现ANR
            synchronized (holder) {
                Log.d(TAG, "surfaceDestroyed...wait surface release");
            }
        }

        @Override
        public void run() {
            // 创建画笔
            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setTextSize(30);

            // 创建path
            mPath = new Path();

            long counter = 0;
            while (canDraw) {
                synchronized (mSurfaceHolder) {
                    draw(counter);
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        }

        private void draw(long counter) {
            // 具体绘制工作
            Canvas canvas = null;
            try {
                // 获取Canvas对象，并锁定之
                canvas = mSurfaceHolder.lockCanvas();

                // 设定Canvas对象的背景颜色
                canvas.drawColor(Color.WHITE);

                // 创建一个Rect对象rect
                Rect rect = new Rect(100, 50, 380, 330);
                // 在canvas上绘制rect
                canvas.drawRect(rect, mPaint);

                // 在canvas上绘制文本
                canvas.drawText("Draw count = " + counter, 100, 410, mPaint);
                canvas.drawText("You can touch draw path!", 100, 600, mPaint);

                // 在canvas上绘制path
                canvas.drawPath(mPath, mPaint);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    // 解除锁定，并提交修改内容
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        /**
         * 绘制触摸滑动路径
         *
         * @param event MotionEvent
         * @return true
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onTouchEvent: down");
                    mPath.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "onTouchEvent: move");
                    mPath.lineTo(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onTouchEvent: up");
                    break;
            }
            return true;
        }

        /**
         * 清屏
         *
         * @return true
         */
        public boolean reDraw() {
            mPath.reset();
            return true;
        }

    }

}
