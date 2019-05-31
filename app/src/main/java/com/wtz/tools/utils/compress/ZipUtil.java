package com.wtz.tools.utils.compress;

import com.wtz.tools.utils.file.FileUtil;
import com.wtz.tools.utils.compress.zip.ZipListener;
import com.wtz.tools.utils.compress.zip.ZipRecorder;
import com.wtz.tools.utils.compress.zip.apache.ZipEntry;
import com.wtz.tools.utils.compress.zip.apache.ZipFile;
import com.wtz.tools.utils.compress.zip.apache.ZipOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

/**
 * 可以解决中文名字压缩乱码和解压缩异常问题；
 * 提供压缩和解压缩进度回调
 */
public class ZipUtil {
    private static final int BUFFER_SIZE = 1024 * 2;

    /**
     * 解压文件到当前文件夹
     *
     * @param zipPath  待解压目标文件路径
     * @param listener
     * @throws Exception
     */
    public static void unzip(String zipPath, ZipListener listener) throws Exception {
        if (!verifyFile(zipPath, listener)) {
            return;
        }

        String unzipPath = zipPath.substring(0, zipPath.lastIndexOf(File.separator));
        unzip(zipPath, unzipPath, listener, true);
    }

    /**
     * 解压文件到指定文件夹
     *
     * @param zipPath   待解压目标文件路径
     * @param unzipPath 解压保存文件夹
     * @param listener
     * @throws Exception
     */
    public static void unzip(String zipPath, String unzipPath, ZipListener listener) throws Exception {
        unzip(zipPath, unzipPath, listener, false);
    }

    private static void unzip(String zipPath, String unzipPath, ZipListener listener, boolean Verified) throws Exception {
        if (!Verified && !verifyFile(zipPath, listener)) {
            return;
        }

        if (listener != null) {
            listener.onStart(unzipPath);
        }

        ZipFile zip = null;
        try {
            ZipRecorder recorder = new ZipRecorder();
            recorder.setTotalLength(getZipSize(zipPath));

            File outputFolder = new File(unzipPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdirs();
            }

            zip = new ZipFile(zipPath);
            Enumeration<ZipEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                File file = new File(outputFolder, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (file.exists() && file.isFile()) {
                        FileUtil.deleteFile(file);
                    }
                    file.mkdirs();
                } else {
                    if (file.exists()) {
                        FileUtil.deleteFile(file);
                    }
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    InputStream inputStream = null;
                    FileOutputStream fileOut = null;
                    try {
                        inputStream = zip.getInputStream(zipEntry);
                        fileOut = new FileOutputStream(file);
                        int length = 0;
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while ((length = inputStream.read(buffer)) != -1) {
                            fileOut.write(buffer, 0, length);
                            fileOut.flush();

                            recorder.addCurrentLength(length);
                            if (listener != null) {
                                listener.onProgress(recorder.getCurrentLength(), recorder.getTotalLength());
                            }
                        }
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                            }
                        }
                        if (fileOut != null) {
                            try {
                                fileOut.close();
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e.toString());
            } else {
                throw e;
            }
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                }
            }
            if (listener != null) {
                listener.onEnd(unzipPath);
            }
        }
    }

    private static long getZipSize(String zipPath) throws IOException {
        long total = 0;
        ZipFile zip = new ZipFile(zipPath);
        Enumeration<ZipEntry> entries = zip.getEntries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            total += zipEntry.getSize();
        }
        return total;
    }

    /**
     * 压缩文件到当前目录
     *
     * @param sourcePath 待压缩文件路径
     * @param listener
     * @throws Exception
     */
    public static void zip(String sourcePath, ZipListener listener) throws Exception {
        String destPath = sourcePath + ".zip";
        zip(sourcePath, destPath, null, listener);
    }

    /**
     * 压缩文件到指定目录
     *
     * @param sourcePath 待压缩文件路径
     * @param zipPath    压缩文件保存路径
     * @param comment    压缩注释说明
     * @param listener
     * @throws Exception
     */
    public static void zip(String sourcePath, String zipPath, String comment, ZipListener listener) throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(sourcePath);
        zip(list, zipPath, comment, listener);
    }

    /**
     * 压缩文件列表到指定目录
     *
     * @param sourcePathList 待压缩文件路径列表
     * @param zipPath    压缩文件保存路径
     * @param comment        压缩注释说明
     * @param listener
     * @throws Exception
     */
    public static void zip(List<String> sourcePathList, String zipPath, String comment, ZipListener listener) throws Exception {
        for (String sourcePath : sourcePathList) {
            if (!verifyFile(sourcePath, listener)) {
                return;
            }
        }

        if (listener != null) {
            listener.onStart(zipPath);
        }

        File saveZip = new File(zipPath);
        if (saveZip.exists()) {
            FileUtil.deleteFile(saveZip);
        }

        ZipOutputStream zos = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(saveZip), new CRC32()));
        // zos.setEncoding("gb2312");
        zos.setEncoding("utf8");

        ZipRecorder recorder = new ZipRecorder();
        long total = 0;
        for (String sourcePath : sourcePathList) {
            total += FileUtil.getFileSize(new File(sourcePath));
        }
        recorder.setTotalLength(total);

        try {
            for (String path : sourcePathList) {
                zip(path, zos, recorder, listener);
            }

            if (comment != null) {
                zos.setComment(comment);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onError(e.toString());
            } else {
                throw e;
            }
        } finally {
            try {
                if (null != zos) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (Exception e) {
            }
            if (listener != null) {
                listener.onEnd(zipPath);
            }
        }
    }

    private static void zip(String sourcePath, ZipOutputStream zipOut, ZipRecorder recorder, ZipListener listener) throws Exception {
        File file = new File(sourcePath);
        if (file.isDirectory()) {
            zipFolder(sourcePath, zipOut, file.getName() + File.separator, recorder, listener);
        } else {
            zipFile(sourcePath, zipOut, file.getName(), recorder, listener);
        }
    }

    private static void zipFolder(String sourceFolder, ZipOutputStream zipOut, String entryPath,
                                  ZipRecorder recorder, ZipListener listener) throws Exception {
        File file = new File(sourceFolder);
        File[] fileList = file.listFiles();

        if (fileList != null) {
            if (fileList.length < 1) {// the folder is empty
                ZipEntry entry = new ZipEntry(entryPath);
                zipOut.putNextEntry(entry);
            } else {
                for (File f : fileList) {
                    if (f.isDirectory()) {
                        zipFolder(f.getPath(), zipOut, entryPath + f.getName() + File.separator,
                                recorder, listener);
                    } else {
                        zipFile(f.getPath(), zipOut, entryPath + f.getName(), recorder, listener);
                    }
                }
            }
        }
    }

    private static void zipFile(String sourceFilePath, ZipOutputStream zipOut, String entryName,
                                ZipRecorder recorder, ZipListener listener) throws Exception {
        ZipEntry entry = new ZipEntry(entryName);
        zipOut.putNextEntry(entry);

        FileInputStream fileIput = new FileInputStream(new File(sourceFilePath));
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = 0;
        try {
            while ((length = fileIput.read(buffer)) != -1) {
                zipOut.write(buffer, 0, length);
                zipOut.flush();

                recorder.addCurrentLength(length);
                if (listener != null) {
                    long total = recorder.getTotalLength();
                    long current = recorder.getCurrentLength();
                    listener.onProgress(current, total);
                }
            }
        } finally {
            if (fileIput != null) {
                try {
                    fileIput.close();
                } catch (Exception e) {
                }
            }
        }

    }

    private static boolean verifyFile(String filePath, ZipListener listener) {
        if (filePath == null || !filePath.contains(File.separator)) {
            if (listener != null) {
                listener.onError("target path is invalid");
            }
            return false;
        }

        File target = new File(filePath);
        if (!target.exists()) {
            if (listener != null) {
                listener.onError("target file does not exist");
            }
            return false;
        }

        return true;
    }

    public static List<String> getZipEntryNames(File zipFile) throws Exception {
        List<String> entryList = new ArrayList<String>();
        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);
            Enumeration<?> entries = zf.getEntries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String name = entry.getName();
                entryList.add(name);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (Exception e) {
                }
            }
        }

        return entryList;
    }

}
