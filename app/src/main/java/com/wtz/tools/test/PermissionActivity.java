package com.wtz.tools.test;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wtz.tools.R;
import com.wtz.tools.utils.permission.PermissionChecker;
import com.wtz.tools.utils.permission.PermissionHandler;


/**
 * 参考：https://blog.csdn.net/yanzhenjie1003/article/details/52503533/
 */
public class PermissionActivity extends Activity implements View.OnClickListener {
    private static final String TAG = PermissionActivity.class.getSimpleName();


    private PermissionHandler mPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.permission_layout);

        findViewById(R.id.btn_static_permission).setOnClickListener(this);
        findViewById(R.id.btn_runtime_permission).setOnClickListener(this);
        findViewById(R.id.btn_write_settings).setOnClickListener(this);
        findViewById(R.id.btn_system_alert_window).setOnClickListener(this);
        findViewById(R.id.btn_install_packages).setOnClickListener(this);
        findViewById(R.id.btn_notification).setOnClickListener(this);
        findViewById(R.id.btn_notification_listener).setOnClickListener(this);

        mPermissionHandler = new PermissionHandler(this, mPermissionHandleListener);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mPermissionHandler != null) {
            mPermissionHandler.destroy();
            mPermissionHandler = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_static_permission:
                mPermissionHandler.handleCommonPermission(Manifest.permission.INTERNET);
                break;
            case R.id.btn_runtime_permission:
                mPermissionHandler.handleCommonPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case R.id.btn_write_settings:
                mPermissionHandler.handleWriteSettingsPermission();
                break;
            case R.id.btn_system_alert_window:
                mPermissionHandler.handleOverlayPermission();
                break;
            case R.id.btn_install_packages:
                mPermissionHandler.handleInstallPackagesPermission();
                break;
            case R.id.btn_notification:
                mPermissionHandler.handleNotifyPermission();
                break;
            case R.id.btn_notification_listener:
                mPermissionHandler.handleListenNotificationPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult requestCode=" + requestCode);
        mPermissionHandler.handleActivityRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode
                + ", data=" + data);
        mPermissionHandler.handleActivityResult(requestCode);
    }

    private PermissionHandler.PermissionHandleListener mPermissionHandleListener =
            new PermissionHandler.PermissionHandleListener() {
                @Override
                public void onPermissionResult(String permission, PermissionChecker.PermissionState state) {
                    Toast.makeText(PermissionActivity.this,
                            "Permission " + permission + " state is " + state,
                            Toast.LENGTH_SHORT).show();
                }
            };

}
