package com.wtz.tools.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class SystemInfoUtils {

    public static String androidGetProp(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 获取手机品牌
     *
     * @return 手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机serial number
     *
     * @return serial number
     */
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    public static String getAndroidID(Context context) {
        try {
            return Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
                return tm.getDeviceId();
            } else {
                return tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取CPU信息
     *
     * @return CPU信息
     */
    public static String getCpuInfo(String separator) {
        String cpuName;
        String processor = null;
        String BogoMIPS = null;
        String Features = null;
        String model_name = null;
        String CPU_implementer = null;
        String CPU_architecture = null;
        String CPU_variant = null;
        String CPU_part = null;
        String CPU_revision = null;
        String Hardware = null;

        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            String readLine;
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));

            while ((readLine = responseReader.readLine()) != null) {
                String[] splitResult = readLine.split("\t:");

                if (processor == null && splitResult[0].toLowerCase().equals("processor")) {
                    processor = splitResult[1];
                } else if (BogoMIPS == null && splitResult[0].equals("BogoMIPS")) {
                    BogoMIPS = splitResult[1];
                } else if (Features == null && splitResult[0].toLowerCase().equals("features")) {
                    Features = splitResult[1];
                } else if (model_name == null && splitResult[0].equals("model name")) {
                    model_name = splitResult[1];
                } else if (CPU_implementer == null && splitResult[0].equals("CPU implementer")) {
                    CPU_implementer = splitResult[1];
                } else if (CPU_architecture == null && splitResult[0].equals("CPU architecture")) {
                    CPU_architecture = splitResult[1];
                } else if (CPU_variant == null && splitResult[0].equals("CPU variant")) {
                    CPU_variant = splitResult[1];
                } else if (CPU_part == null && splitResult[0].equals("CPU part")) {
                    CPU_part = splitResult[1];
                } else if (CPU_revision == null && splitResult[0].equals("CPU revision")) {
                    CPU_revision = splitResult[1];
                } else if (Hardware == null && splitResult[0].toLowerCase().equals("hardware")) {
                    Hardware = splitResult[1];
                }
            }

            responseReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cpuName = (processor == null ? "" : processor) + separator +
                (BogoMIPS == null ? "" : BogoMIPS) + separator +
                (Features == null ? "" : Features) + separator +
                (model_name == null ? "" : model_name) + separator +
                (CPU_implementer == null ? "" : CPU_implementer) + separator +
                (CPU_architecture == null ? "" : CPU_architecture) + separator +
                (CPU_variant == null ? "" : CPU_variant) + separator +
                (CPU_part == null ? "" : CPU_part) + separator +
                (CPU_revision == null ? "" : CPU_revision) + separator +
                (Hardware == null ? "" : Hardware);

        return cpuName;
    }

    /**
     * Reads the number of CPU cores from {@code /sys/devices/system/cpu/}.
     *
     * @return Number of CPU cores in the phone, or DEVICEINFO_UKNOWN = -1 in the event of an error.
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = -1;
        } catch (NullPointerException e) {
            cores = -1;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 获取cpu最大频率
     */
    public static int getMaxCpuFreq() {
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }

        return result;
    }

    /**
     * 获取CPU最小频率（单位KHZ）
     */
    public static int getMinCpuFreq() {
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                }
        }
        return result;
    }

    /**
     * 获取CPU当前频率（单位KHZ）
     */
    public static int getCurrentCpuFreq() {
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public static String getCPU_ABI(String separator) {
        try {
            StringBuffer buffer = new StringBuffer();

            if (Build.VERSION.SDK_INT >= 21) {
                String[] ABIs_surported = Build.SUPPORTED_ABIS;

                if (ABIs_surported.length > 0) {
                    for (int i = 0; i < ABIs_surported.length; i++) {
                        buffer.append(ABIs_surported[i]);
                        if (i < ABIs_surported.length - 1) {
                            buffer.append(separator);
                        }
                    }
                }
            } else {
                String abi = Build.CPU_ABI;
                String abi2 = Build.CPU_ABI2;

                buffer.append(abi);
                buffer.append(separator);
                buffer.append(abi2);
            }
            return buffer.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取手机内存信息
     *
     * @return 手机内存信息
     */
    public static long getTotalMemoryBytes(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && c != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(memoryInfo);
            if (memoryInfo != null) {
                return memoryInfo.totalMem;
            }
        }

        return readTotalRAMFromFile();
    }

    private static long readTotalRAMFromFile() {
        String path = "/proc/meminfo";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            String line = null;
            if ((line = br.readLine()) != null) {
                return getRamValueBytes(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
            }
        }
        return -1;
    }

    private static long getRamValueBytes(String line) {
        int start = line.indexOf(":") + 1;
        int end = line.indexOf("kB");
        String result = line.substring(start, end).trim();
        return Long.parseLong(result) * 1024;
    }

    /**
     * 获取可用内存信息
     *
     * @return 可用内存
     */
    public static long getAvailMemoryBytes(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            return mi.availMem;
        }
        return -1;
    }

    public static long getTotalInternalStorageSize(Context context) {
        String path = Environment.getDataDirectory().getPath();
        return getStorageSize(context, path, true);
    }

    public static long getAvailableInternalStorageSize(Context context) {
        String path = Environment.getDataDirectory().getPath();
        return getStorageSize(context, path, false);
    }

    public static long getTotalExternalStorageSize(Context context) {
        return getExternalStorageSize(context, true);
    }

    public static long getAvailableExternalStorageSize(Context context) {
        return getExternalStorageSize(context, false);
    }

    private static long getExternalStorageSize(Context context, boolean total) {
        if (!isExternalMemoryAvailable()) {
            return 0;
        }
        try {
            String path = Environment.getExternalStorageDirectory().getPath();
            return getStorageSize(context, path, total);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) || !isExternalStorageRemovable();
    }

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * 获取指定路径存储大小字节数，total为true表示获取总大小，false表示可用大小
     *
     * @param context
     * @param path
     * @param total
     * @return
     */
    private static long getStorageSize(Context context, String path, boolean total) {
        StatFs mStatFs = new StatFs(path);
        long blockSize = mStatFs.getBlockSize();
        long blockCount;
        if (total) {
            blockCount = mStatFs.getBlockCount();
        } else {
            blockCount = mStatFs.getAvailableBlocks();
        }
        //return Formatter.formatFileSize(context, blockCount * blockSize);
        // 数据分析用，不要带单位，统一返回字节数
        return blockCount * blockSize;
    }

    /**
     * 获取手机屏幕分辨率
     *
     * @return 手机屏幕分辨率
     */
    public static int[] getScreenPixels(Context context) {
        if (context == null) {
            return new int[]{0, 0};
        }

        int widthPixels;
        int heightPixels;

        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        widthPixels = dm.widthPixels;
        heightPixels = dm.heightPixels;

        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Method method = display.getClass().getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, dm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            heightPixels = dm.heightPixels;
        } else {
            try {
                Method method = display.getClass().getMethod("getRawHeight");
                heightPixels = (Integer) method.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new int[]{widthPixels, heightPixels};
    }


    /**
     * 获取时区
     *
     * @return 时区
     */
    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return createGmtOffsetString(true, true, tz.getRawOffset());
    }

    private static String createGmtOffsetString(boolean includeGmt, boolean includeMinuteSeparator, int offsetMillis) {
        int offsetMinutes = offsetMillis / 60000;
        char sign = '+';
        if (offsetMinutes < 0) {
            sign = '-';
            offsetMinutes = -offsetMinutes;
        }
        StringBuilder builder = new StringBuilder(9);
        if (includeGmt) {
            builder.append("GMT");
        }
        builder.append(sign);
        appendNumber(builder, 2, offsetMinutes / 60);
        if (includeMinuteSeparator) {
            builder.append(':');
        }
        appendNumber(builder, 2, offsetMinutes % 60);
        return builder.toString();
    }

    private static void appendNumber(StringBuilder builder, int count, int value) {
        String string = Integer.toString(value);
        for (int i = 0; i < count - string.length(); i++) {
            builder.append('0');
        }
        builder.append(string);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 获取开机时间
     *
     * @return 开机时间
     */
    public static String getBootUpTime() {
        long time = new Date().getTime() - SystemClock.elapsedRealtime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");
        return formatter.format(new Date(time));
    }

}
