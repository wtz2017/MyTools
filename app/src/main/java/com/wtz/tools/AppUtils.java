package com.wtz.tools;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppUtils {

    public static String getSelfVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static JSONArray getAppList(Context context) {
        JSONArray jsonArray = new JSONArray();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo info : listAppcations) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // non-system app
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", URLEncoder.encode(info.loadLabel(pm).toString(), "UTF-8"));
                    jsonObject.put("package_name", info.packageName);
                    PackageInfo packageInfo = pm.getPackageInfo(info.packageName, 0);
                    jsonObject.put("version_name", packageInfo.versionName);
                    jsonObject.put("version_code", packageInfo.versionCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray;
    }

    public static JSONArray getTopRunningAppList(Context context, int topNums) {
        if (context == null) {
            return null;
        }

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        JSONArray jsonArray = new JSONArray();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = null;
            try {
                runningTaskInfos = manager.getRunningTasks(topNums);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
                int size = runningTaskInfos.size();
                for (int i = 0; i < size; i++) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("order", i);
                        jsonObject.put("package_name", runningTaskInfos.get(i).topActivity.getPackageName());
                        jsonObject.put("activity_num", runningTaskInfos.get(i).numActivities);
                        jsonObject.put("top_activity", runningTaskInfos.get(i).topActivity.getClassName());
                        jsonObject.put("base_activity", runningTaskInfos.get(i).baseActivity.getClassName());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = null;
            try {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60, time);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    int i = 0;
                    for (UsageStats usageStats : mySortedMap.values()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("order", i);
                            jsonObject.put("package_name", usageStats.getPackageName());
                            jsonObject.put("last_time_used", usageStats.getLastTimeUsed());
                            jsonObject.put("total_time_in_foreground", usageStats.getTotalTimeInForeground());
                            jsonObject.put("first_time_stamp", usageStats.getFirstTimeStamp());
                            jsonObject.put("last_time_stamp", usageStats.getLastTimeStamp());
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                }
            }
        }
        return jsonArray;
    }

    /**
     * 判断自己是否在前台：0 表示未知；1 表示在前台；-1 表示在后台
     * 当没有GET_TASKS权限时，无法判断结果，返回值是0
     * @param context
     * @return
     */
    public static int isSelfAtforeground(Context context) {
        int result = 0;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
            if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
                String topPkg = runningTaskInfos.get(0).topActivity.getPackageName();
                if (context.getPackageName().equalsIgnoreCase(topPkg)) {
                    result = 1;
                } else {
                    result = -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ActivityManager.RunningTaskInfo getTopRunningAppInfo(Context context) {
        ActivityManager.RunningTaskInfo rti = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null && runningTaskInfos.size() > 0) {
            rti = runningTaskInfos.get(0);
            runningTaskInfos.clear();
        }
        return rti;
    }

    public static UsageStats getTopActivtyFromLolipop(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    return mySortedMap.get(mySortedMap.lastKey());
                }
            }
            return null;
        }
        return null;
    }

}
