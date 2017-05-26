package com.wtz.tools;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

public class DLUtils {

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

    public static int getAppVersionCode(Context context, String apkFilepath) {
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
        PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(dexPath, 1);
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
