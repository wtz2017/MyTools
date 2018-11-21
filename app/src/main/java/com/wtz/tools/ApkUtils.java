package com.wtz.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class ApkUtils {

    private static final String TAG = ApkUtils.class.getSimpleName();

    public static void install(Context context, String apkPath, String authorityFor7) {
        Log.i(TAG, "开始执行安装: " + apkPath);
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 26) {
            Log.w(TAG, "API>=26 ，先检查权限再进行安装");
            // 建议不使用判断和Intent跳转.而是直接使用Intent里带apk的安装,会有提示,
            // 然后直接进入权限开关的界面,这样的体验相对好
//            if (context.getPackageManager().canRequestPackageInstalls()) {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//            } else {
//                Intent intent1 = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                context.startActivityForResult(intent1, 10012);
//                return;
//            }
        } else if (Build.VERSION.SDK_INT >= 24) {
            Log.w(TAG, "API:24-25 ，使用 fileProvider 进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, authorityFor7, apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.w(TAG, "API<24，直接安装");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
