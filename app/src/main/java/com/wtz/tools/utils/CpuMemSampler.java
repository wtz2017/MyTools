package com.wtz.tools.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Usage:
 * CPUMemSampler.getInstance().init(getApplicationContext(), 1000L);
 * CPUMemSampler.getInstance().start();
 * CpuMemSampler.getInstance().stop();
 */
public class CpuMemSampler implements Runnable {

    private volatile static CpuMemSampler instance = null;
    private ScheduledExecutorService scheduler;
    private ActivityManager activityManager;
    private long freq = 1000L;
    private Long lastCpuTime;
    private Long lastAppCpuTime;
    private RandomAccessFile procStatFile;
    private RandomAccessFile appStatFile;

    private static final DecimalFormat CPU_FORMAT = new DecimalFormat("CPU:0.0%");
    private static final DecimalFormat MEM_FORMAT = new DecimalFormat("MEM:0.0MB");

    private CpuMemSampler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static CpuMemSampler getInstance() {
        if (instance == null) {
            synchronized (CpuMemSampler.class) {
                if (instance == null) {
                    instance = new CpuMemSampler();
                }
            }
        }
        return instance;
    }

    // freq为采样周期
    public void init(Context context, long freq) {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        this.freq = freq;
    }

    public void start() {
        scheduler.scheduleWithFixedDelay(this, 0L, freq, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        synchronized (CpuMemSampler.class) {
            instance = null;
            scheduler.shutdown();
            activityManager = null;
        }
    }

    @Override
    public void run() {
        double cpu = sampleCPU();
        double mem = sampleMemory();
        Log.e("CpuMemSampler", CPU_FORMAT.format(cpu) + ", " + MEM_FORMAT.format(mem));
    }

    private double sampleCPU() {
        long cpuTime;
        long appTime;
        double sampleValue = 0.0D;
        try {
            if (procStatFile == null || appStatFile == null) {
                procStatFile = new RandomAccessFile("/proc/stat", "r");
                appStatFile = new RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r");
            } else {
                procStatFile.seek(0L);
                appStatFile.seek(0L);
            }
            String procStatString = procStatFile.readLine();
            String appStatString = appStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (lastCpuTime == null && lastAppCpuTime == null) {
                lastCpuTime = cpuTime;
                lastAppCpuTime = appTime;
                return sampleValue;
            }
            sampleValue = (double) (appTime - lastAppCpuTime) / (double) (cpuTime - lastCpuTime);
            lastCpuTime = cpuTime;
            lastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sampleValue;
    }

    private double sampleMemory() {
        double mem = 0.0D;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(new int[]{android.os.Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    // Mem in MB
                    mem = totalPss / 1024.0D;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }
}
