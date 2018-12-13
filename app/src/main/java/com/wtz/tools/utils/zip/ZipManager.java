package com.wtz.tools.utils.zip;

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

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipManager {
    public static final int BUFFER_SIZE = 1024 * 2;

    public static List<String> getZipEntryName(File zipFile) throws Exception {
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
                    throw e;
                }
            }
        }

        return entryList;
    }

    public static void unzip(String sourceZip) throws Exception {
        String destPath = getDefaultUnzipPath(sourceZip);
        unzip(sourceZip, destPath, null);
    }

    public static void unzip(String sourceZip, String outputPath, ZipListener listener)
            throws Exception {
        ZipFile zip = null;
        FileOutputStream fileOut = null;
        File file;
        InputStream inputStream = null;

        try {
            long total = 0;
            ZipRecorder recorder = new ZipRecorder();
            ZipFile zip1 = new ZipFile(sourceZip);
            Enumeration<ZipEntry> entries1 = zip1.getEntries();
            while (entries1.hasMoreElements()) {
                ZipEntry zipEntry = entries1.nextElement();
                total += zipEntry.getSize();
            }
            recorder.setTotalLength(total);

            if (listener != null) {
                listener.onStart(outputPath);
            }

            File outputFolder = new File(outputPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            zip = new ZipFile(sourceZip);
            Enumeration<ZipEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                file = new File(outputFolder, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    inputStream = zip.getInputStream(zipEntry);
                    fileOut = new FileOutputStream(file);
                    int length = 0;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOut.write(buffer, 0, length);
                        fileOut.flush();

                        recorder.addCurrentLength(length);
                        if (listener != null) {
                            listener.onProgress(recorder.getCurrentLength(),
                                    recorder.getTotalLength());
                        }
                    }
                    fileOut.close();
                    inputStream.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } finally {
            if (null != fileOut) {
                try {
                    fileOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != zip) {
                try {
                    zip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (listener != null) {
                listener.onEnd(outputPath);
            }
        }
    }

    private static String getDefaultUnzipPath(String sourceZip) throws Exception {
        File srcFile = new File(sourceZip);
        String absolutePath = srcFile.getAbsolutePath() + "";
        String parentPath = "";
        if (-1 != absolutePath.lastIndexOf(File.separator)) {
            parentPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        }
        String separator = parentPath.endsWith(File.separator) ? "" : File.separator;
        String unzipPath = parentPath + separator
                + srcFile.getName().substring(0, srcFile.getName().lastIndexOf('.'))
                + File.separator;
        return unzipPath;
    }

    public static void zip(File sourceFile) throws Exception {
        zip(sourceFile.getAbsolutePath());
    }

    public static void zip(String sourcePath) throws Exception {
        String destPath = getDefaultZipPath(sourcePath);
        zip(sourcePath, destPath, null, null);
    }

    private static String getDefaultZipPath(String sourcePath) throws Exception {
        File srcFile = new File(sourcePath);
        // String destPath = srcFile.getParent() + File.separator +
        // srcFile.getName() + ".zip";

        String absolutePath = srcFile.getAbsolutePath() + "";
        String parentPath = "";
        if (-1 != absolutePath.lastIndexOf(File.separator)) {
            parentPath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        }
        String separator = parentPath.endsWith(File.separator) ? "" : File.separator;
        String zipPath = parentPath + separator + srcFile.getName() + ".zip";
        return zipPath;
    }

    public static void zip(String sourcePath, String zipPathName, String comment, ZipListener listener)
            throws Exception {
        List<String> list = new ArrayList<String>();
        list.add(sourcePath);
        zip(list, zipPathName, comment, listener);
    }

    public static void zip(List<String> sourcePathList, String zipPathName, String comment,
            ZipListener listener) throws Exception {
        CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(new File(zipPathName)),
                new CRC32());
        ZipOutputStream zos = new ZipOutputStream(cos);

        // zos.setEncoding("gb2312");
        zos.setEncoding("utf8");

        ZipRecorder recorder = new ZipRecorder();

        long total = 0;
        for (String sourcePath : sourcePathList) {
            total += calculateFileLength(new File(sourcePath));
        }
        recorder.setTotalLength(total);

        if (listener != null) {
            listener.onStart(zipPathName);
        }

        try {
            for (String path : sourcePathList) {
                zip(path, zos, recorder, listener);
            }

            if (comment != null) {
                zos.setComment(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } finally {
            try {
                if (null != zos) {
                    zos.closeEntry();
                    zos.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            if (listener != null) {
                listener.onEnd(zipPathName);
            }
        }
    }

    private static void zip(String sourcePath, ZipOutputStream zipOut, ZipRecorder recorder,
            ZipListener listener) throws Exception {
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
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } finally {
            if (fileIput != null) {
                try {
                    fileIput.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static long calculateFileLength(File file) throws Exception {
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

        if (files == null) {
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
}
