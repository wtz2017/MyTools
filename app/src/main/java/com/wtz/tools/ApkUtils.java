package com.wtz.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

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

    public static PackageInfo getPackageInfo(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageArchiveInfo(apkFilepath, PackageManager.GET_ACTIVITIES);
        } catch (Exception e) {
            // should be something wrong with parse
            e.printStackTrace();
        }

        return pkgInfo;
    }

    public static Drawable getAppIcon(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }

        // Workaround for http://code.google.com/p/android/issues/detail?id=9151
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }

        return pm.getApplicationIcon(appInfo);
    }

    public static int getVersionCode(Context context, String apkFilepath) {
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return -1;
        }
        return pkgInfo.versionCode;
    }

    public static CharSequence getAppLabel(Context context, String apkFilepath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
        if (pkgInfo == null) {
            return null;
        }

        // Workaround for http://code.google.com/p/android/issues/detail?id=9151
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= 8) {
            appInfo.sourceDir = apkFilepath;
            appInfo.publicSourceDir = apkFilepath;
        }

        return pm.getApplicationLabel(appInfo);
    }

    public static String getMainClass(Context context, String dexPath) {
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        return getMainClass(context, packageInfo);
    }

    public static String getMainClass(Context context, PackageInfo packageInfo) {
        if (packageInfo == null) {
            Log.e("DLUtils", "getMainClass failed packageInfo is null:");
            return null;
        }

        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {

            return packageInfo.activities[0].name;
        }
        return null;
    }

    public static ActivityInfo getMainActivityInfo(Context context, PackageInfo packageInfo) {
        if (packageInfo == null) {
            Log.e("DLUtils", "getMainClass failed packageInfo is null:");
            return null;
        }
        if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {

            return packageInfo.activities[0];
        }
        return null;
    }

    public static void loadResources(Resources superRes, Resources.Theme superTheme,
                                     String dexPath, AssetManager currentAssetManager, Resources currentResources,
                                     Resources.Theme currentTheme) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, dexPath);
            currentAssetManager = assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentResources = new Resources(currentAssetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
        currentTheme = currentResources.newTheme();
        currentTheme.setTo(superTheme);
    }

}
