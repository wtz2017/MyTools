package com.wtz.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class FileUtil {
    
    private static DecimalFormat fileIntegerFormat = new DecimalFormat("#0");
    private static DecimalFormat fileDecimalFormat = new DecimalFormat("#0.0#");

    /**
     * 单位换算
     * 
     * @param size
     *            单位为B
     * @param isInteger
     *            是否返回取整的单位
     * @return 转换后的单位
     */
    public static String formatFileSize(long size, boolean isInteger) {
        DecimalFormat df = isInteger ? fileIntegerFormat : fileDecimalFormat;
        String fileSizeString = "0M";
        if (size < 1024 && size > 0) {
            fileSizeString = size + "B";
        } else if (size < 1024 * 1024) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1024 * 1024 * 1024) {
            fileSizeString = df.format((double) size / (1024 * 1024)) + "M";
        } else {
            fileSizeString = df.format((double) size / (1024 * 1024 * 1024)) + "G";
        }
        return fileSizeString;
    }
    
    public static long calculateFileLength(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        
        if (file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            return calculateDirectorySize(file);
        }

        return 0;
    }

    private static long calculateDirectorySize(File directory) {
        long totalLength = 0;
        File[] files = directory.listFiles();

        if (files == null || files.length == 0) {
            return totalLength;
        }

        for (File file : files) {
            if (file.exists()) {
                if (file.isFile()) {
                    totalLength += file.length();
                } else {
                    totalLength += calculateDirectorySize(file);
                }
            }
        }

        return totalLength;
    }
    
    public static void deleteFiles(File[] files) {
        for (File f : files) {
            if (f != null && f.exists()) {
                deleteFile(f);
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children) {
                boolean success = deleteFile(child);
                if (!success) {
                    return false;
                }
            }
        }

        // delete file or empty folder
        return file.delete();
    }
    
    /**
     * @param cxt
     * @param from asset路径
     * @param to sdcard具体需要存放的路径
     * @return
     */
    public static boolean copyAssetsFileToSdcard(Context cxt, String from, String to) {

        if (cxt == null || TextUtils.isEmpty(from) || TextUtils.isEmpty(to)) {
            return false;
        }

        int separatorIndex = to.lastIndexOf(File.separator);
        if (-1 == separatorIndex) {
            return false;
        }

        String toFolderPath;
        if (0 == separatorIndex) {
            toFolderPath = File.separator;
        } else {
            toFolderPath = to.substring(0, separatorIndex);
        }

        File toFolder = new File(toFolderPath);
        boolean bOk = false;
        if (!toFolder.exists() || !toFolder.isDirectory()) {
            bOk = toFolder.mkdirs();
        } else {
            bOk = true;
        }
        if (!bOk) {
            return false;
        }

        int byteRead = 0;
        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = cxt.getResources().getAssets().open(from);
            outStream = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, byteRead);
            }
            outStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != inStream) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String readFile(String filePath) {
        String content = "";
        BufferedReader reader = null;
        try {
            FileInputStream fin = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fin);
            reader = new BufferedReader(new InputStreamReader(bis));
            String tempRead = reader.readLine();
            while (tempRead != null) {
                content += tempRead;
                tempRead = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return content;
    }
    
    public static void writeStringToFile(String content, String fileName, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(fileName, append);
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
}
