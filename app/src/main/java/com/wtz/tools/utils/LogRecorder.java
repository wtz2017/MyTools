package com.wtz.tools.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class LogRecorder {
    private final String TAG = LogRecorder.class.getSimpleName();

    private static final String ERROR_NOT_INIT = "LogRecorder must be init by service-context or app-context before using";

    public final static String CUSTOM_LOG_NAME = "custom.log";
    public final static String SYSTEM_LOG_NAME = "system.log";
    
    private static LogRecorder mInstance;

    private Context mContext;
    
    private LogRecorder() {
    }

    public static LogRecorder instance() {
        if (mInstance == null) {
            synchronized (LogRecorder.class) {
                if (mInstance == null) {
                    mInstance = new LogRecorder();
                }
            }
        }
        return mInstance;
    }

    /**
     * @param context
     *            service-context or app-context
     */
    public void init(Context context) {
        mContext = context;
    }
    
    public boolean isInited(Context context) {
        return mContext != null;
    }
    
    public static void destroy() {
        mInstance = null;
    }

    /**
     * LogRecorder must be init by service-context or app-context before using
     * 
     * @param context
     *            service-context or app-context
     * @param log
     */
    public void recordCustomLog(final String log) {
        if (mContext == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }

        new RecordCustomLogTask().execute(log);
    }
    
    public void recordSystemLog() {
        if (mContext == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                Log.d(TAG, "recordSystemLog...new Thread start...");
                recordCustomLog(TAG + ": recordSystemLog...new Thread start...");
                
                List<String> commandList = new ArrayList<String>();
                commandList.add("logcat");
                
                commandList.add("-v");
                commandList.add("time");
                
                commandList.add("-f");
                commandList.add(getSystemLogPath());
                
                try {
                    Process p = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
//                    p.waitFor();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                } finally {
                }
                
                Log.d(TAG, "recordSystemLog...Thread end!");
                recordCustomLog(TAG + ": recordSystemLog...Thread end!");
            }
        }).start();
    }
    
    private synchronized void writeCustomLog(String log) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\r\n");
        
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("[yyyy-MM-dd_HH:mm:ss] ");
        String nowTime = df.format(date);
        stringBuilder.append(nowTime);
        
        String pid = "[pid = " + android.os.Process.myPid() + "]";
        stringBuilder.append(pid);
        stringBuilder.append(" ");
        
        stringBuilder.append(log);
        stringBuilder.append("\r\n");

        appendToFile(getCustomLogPath(), stringBuilder.toString());
    }

    private String getCustomLogPath() {
        String sub = "custom";
        File folder = StorageUtil.getStorageDir(mContext, sub);
        File file = new File(folder, CUSTOM_LOG_NAME);
        return file.getAbsolutePath();
    }
    
    private String getSystemLogPath() {
        String sub = "system";
        File folder = StorageUtil.getStorageDir(mContext, sub);
        File file = new File(folder, SYSTEM_LOG_NAME);
        return file.getAbsolutePath();
    }

    private void appendToFile(String fileName, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    writer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class RecordCustomLogTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... args) {
            Log.d(TAG, "RecordLogTask...doInBackground---args = " + args);
            String log = args[0];

            synchronized (RecordCustomLogTask.class) {
                writeCustomLog(log);
            }

            return 0;
        }

    }
    
}
