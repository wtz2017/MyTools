package com.wtz.tools.test;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.wtz.tools.R;

public class ScreenSaverActivity extends Activity {
    private static final String TAG = "ScreenSaverActivity";

    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager.KeyguardLock mKeyguardLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

//        // 拿到键盘守护锁，用来解锁屏幕
//        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        mKeyguardLock = mKeyguardManager.newKeyguardLock("ScreenLockService");

        // 解锁屏幕，允许在锁屏上显示
        // 对于小米手机，还需要用户在设置里找到本应用权限允许在锁屏上显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        setContentView(R.layout.activity_screen_saver);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        // 用来控制屏幕常亮
        mWakeLock = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.SCREEN_DIM_WAKE_LOCK |
                        PowerManager.ON_AFTER_RELEASE,
                this.getClass().getCanonicalName());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
//        mKeyguardLock.disableKeyguard();// 关闭屏幕锁定功能
        mWakeLock.acquire();// 保持屏幕常亮
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mWakeLock.release();// 取消屏幕常亮
//        mKeyguardLock.reenableKeyguard();// 恢复屏幕锁定功能
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 菜单键
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
        // 搜索键
        finish();
        return super.onSearchRequested();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

}
