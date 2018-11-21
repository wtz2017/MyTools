package com.wtz.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }
        ByteArrayOutputStream out;
        GZIPOutputStream gzip = null;
        try {
            out = new ByteArrayOutputStream();
            gzip = new GZIPOutputStream(out);
            gzip.write(data);
        } finally {
            if (gzip != null) {
                gzip.close();
            }
        }
        return out.toByteArray();//out.toString("ISO-8859-1");
    }

    public static byte[] compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return null;
        }
        return compress(str.getBytes("utf-8"));
    }

    public static byte[] uncompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }
        ByteArrayOutputStream out;
        ByteArrayInputStream in = null;
        GZIPInputStream gunzip = null;
        try {
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(data);
            gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } finally {
            if (gunzip != null) {
                gunzip.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return out.toByteArray();
    }

    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        byte[] data = uncompress(str.getBytes("utf-8")); // ISO-8859-1
        return new String(data);
    }

    public static void compress(File srcFile, File targetFile) throws Exception {
        FileInputStream fis = null;
        GZIPOutputStream gos = null;
        try {
            fis = new FileInputStream(srcFile);
            gos = new GZIPOutputStream(new FileOutputStream(targetFile));

            int count;
            int BUFFER = 1024;
            byte data[] = new byte[BUFFER];
            while ((count = fis.read(data, 0, BUFFER)) != -1) {
                gos.write(data, 0, count);
            }
            gos.finish();
            gos.flush();
        } finally {
            if (gos != null) {
                gos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

}
