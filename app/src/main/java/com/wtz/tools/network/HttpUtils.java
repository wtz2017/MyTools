package com.wtz.tools.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    private final static String TAG = HttpUtils.class.getSimpleName();

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";

    private static final int CONNECT_TIME_OUT = 1000 * 5;
    private static final int READ_TIME_OUT = 1000 * 5;

    private static final String CHARTSET = "UTF-8";
    private static final int HTTP_OK = 200;
    private static final int BUFFER = 1024 * 8;

    private HttpUtils() {
    }

    public static String postJson(String urlStr, Map<String, String> headers, byte[] body) throws Exception {
        Log.d(TAG, "http postJson: " + urlStr + "; headers: " + headers);
        if (TextUtils.isEmpty(urlStr) || body == null || body.length == 0) {
            return "url or body is null";
        }

        URL url = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        DataOutputStream outputStream = null;
        String response = null;

        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(METHOD_POST);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", CHARTSET);
            conn.setRequestProperty("Content-Length", String.valueOf(body.length));
            conn.setRequestProperty("Content-Type", "application/json");
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.connect();

            outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.write(body);
            outputStream.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            }
        }
        return response;
    }

    public static String postFile(String urlStr, Map<String, String> headers, Map<String, String> params, File file) throws Exception {
        Log.d(TAG, "http postFile:" + urlStr + "; file:" + file + "; parms:" + params);
        if (TextUtils.isEmpty(urlStr) || file == null || !file.exists() || file.isDirectory()) {
            return "url is empty or file is not valid";
        }

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        InputStream inStream = null;
        int responseCode;
        String response = null;

        try {
            URI uri = new URI(urlStr);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.connect();

            // 发送参数
            if (params != null) {
                StringBuilder stringBuilder1 = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuilder1.append(PREFIX);
                    stringBuilder1.append(BOUNDARY);
                    stringBuilder1.append(LINEND);
                    stringBuilder1.append("Content-Disposition: form-data; name=\"" + entry.getKey()
                            + "\"" + LINEND);
                    stringBuilder1.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                    stringBuilder1.append("Content-Transfer-Encoding: 8bit" + LINEND);
                    stringBuilder1.append(LINEND);
                    stringBuilder1.append(entry.getValue());
                    stringBuilder1.append(LINEND);
                }

                outStream = new DataOutputStream(conn.getOutputStream());
                outStream.write(stringBuilder1.toString().getBytes());
            }

            // 发送文件
            if (outStream == null) {
                outStream = new DataOutputStream(conn.getOutputStream());
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(PREFIX);
            stringBuilder2.append(BOUNDARY);
            stringBuilder2.append(LINEND);
            stringBuilder2.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + file.getName() + "\"" + LINEND);
            stringBuilder2.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
            stringBuilder2.append(LINEND);
            outStream.write(stringBuilder2.toString().getBytes());

            // 二进制数据
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
            outStream.write(LINEND.getBytes());

            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();

            responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

    public static String postImageBytes(String urlStr, Map<String, String> headers, Map<String, String> params, byte[] imageData) throws Exception {
        Log.d(TAG, "http postFile:" + urlStr + "; imageData:" + imageData + "; parms:" + params);
        if (TextUtils.isEmpty(urlStr) || imageData == null || imageData.length == 0) {
            return "url is empty or imageData is not valid";
        }

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        InputStream inStream = null;
        int responseCode;
        String response = null;

        try {
            URI uri = new URI(urlStr);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.connect();

            // 发送参数
            if (params != null) {
                StringBuilder stringBuilder1 = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuilder1.append(PREFIX);
                    stringBuilder1.append(BOUNDARY);
                    stringBuilder1.append(LINEND);
                    stringBuilder1.append("Content-Disposition: form-data; name=\"" + entry.getKey()
                            + "\"" + LINEND);
                    stringBuilder1.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                    stringBuilder1.append("Content-Transfer-Encoding: 8bit" + LINEND);
                    stringBuilder1.append(LINEND);
                    stringBuilder1.append(entry.getValue());
                    stringBuilder1.append(LINEND);
                }

                outStream = new DataOutputStream(conn.getOutputStream());
                outStream.write(stringBuilder1.toString().getBytes());
            }

            // 发送 image bytes
            if (outStream == null) {
                outStream = new DataOutputStream(conn.getOutputStream());
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(PREFIX);
            stringBuilder2.append(BOUNDARY);
            stringBuilder2.append(LINEND);
            stringBuilder2.append("Content-Disposition: form-data; name=\"file\"; filename=\"image\"" + LINEND);
            stringBuilder2.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
            stringBuilder2.append(LINEND);
            outStream.write(stringBuilder2.toString().getBytes());

            // 二进制数据
            outStream.write(imageData);
            outStream.write(LINEND.getBytes());

            // 请求结束标志
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();

            responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            if (outStream != null) {
                outStream.close();
            }
            if (inStream != null) {
                inStream.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

    public static boolean downloadFile(String urlStr, String saveDir, String fileName) {
        Log.d(TAG, "http downloadFile:" + urlStr + "; save:" + saveDir + File.separator + fileName);
        boolean result = false;

        HttpURLConnection conn = null;
        InputStream inStream = null;
        FileOutputStream fos = null;

        try {
            File dir = new File(saveDir);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }

            File target = new File(saveDir, fileName);
            if (target.exists() && target.isFile()) {
                target.delete();
            }

            URL url = new URL(new URI(urlStr).toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setRequestMethod(METHOD_GET);
            conn.setRequestProperty("accept", "*/*");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
                fos = new FileOutputStream(target);

                long totalSize = conn.getContentLength();
                long currentSize = 0;

                byte buffer[] = new byte[BUFFER];
                int readSize = 0;
                while ((readSize = inStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, readSize);
                    fos.flush();
                    currentSize += readSize;
                }

                if (totalSize < 0 || currentSize == totalSize) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    /**
     * GET方式发送请求
     *
     * @param urlStr 请求URL地址
     * @param params 请求URL参数
     * @return
     * @throws Exception
     */
    public static String get(String urlStr, Map<String, String> params) throws Exception {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        String response = null;

        try {
            url = transformUrl(urlStr, params);
            Log.d(TAG, "http get:" + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod(METHOD_GET);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setRequestProperty("accept", "*/*");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            }
        }
        return response;
    }

    /**
     * POST方式发送数据
     *
     * @param urlStr 请求URL地址
     * @param params 请求URL参数
     * @param body   要post的数据body
     * @return
     * @throws Exception
     */
    public static String post(String urlStr, Map<String, String> params, String body) throws Exception {
        URL url = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        DataOutputStream outputStream = null;
        String response = null;
        byte[] data = body.getBytes();

        try {
            url = transformUrl(urlStr, params);
            Log.d(TAG, "http post:" + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(METHOD_POST);
            conn.setConnectTimeout(CONNECT_TIME_OUT);
            conn.setReadTimeout(READ_TIME_OUT);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", CHARTSET);
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

            outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            }
        }
        return response;
    }

    private static URL transformUrl(String urlStr, Map<String, String> params) throws MalformedURLException {
        HashMap<String, String> map = new HashMap<>();
        URI uri = URI.create(urlStr);
        String query = uri.getQuery();
        String url = urlStr;

        // split internal params in the url
        if (query != null) {
            url = url.replaceAll("\\?" + query, "");
            String[] queryParams = query.split("\\&");
            for (int i = 0; i < queryParams.length; i++) {
                String[] pair = queryParams[i].split("=");
                if (pair.length == 2) {
                    map.put(pair[0], pair[1]);
                }
            }
        }

        // add external params
        if (params != null) {
            map.putAll(params);
        }

        // encode params
        StringBuffer paramBuilder = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            paramBuilder.append(entry.getKey());
            paramBuilder.append("=");
            paramBuilder.append(encode(entry.getValue()));
            paramBuilder.append("&");
        }

        // append params
        String paramStr = paramBuilder.toString();
        if (!TextUtils.isEmpty(paramStr)) {
            url = url + "?" + paramStr.substring(0, paramStr.length() - 1);
        }

        return new URL(url);
    }

    private static String encode(String s) {
        if (s == null) {
            return "";
        }
        String encoded = "";
        try {
            encoded = URLEncoder.encode(s, CHARTSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            if (c == '+') {
                sBuilder.append("%20");
            } else if ((c == '%') && ((i + 1) < encoded.length()) && ((i + 2) < encoded.length()) & (encoded.charAt(i + 1) == '7') && (encoded.charAt(i + 2) == 'E')) {
                sBuilder.append("~");
                i += 2;
            } else {
                sBuilder.append(c);
            }
        }
        return sBuilder.toString();
    }

    /**
     * 获取输入流中信息
     *
     * @param inStream 输入流
     * @return
     * @throws IOException
     */
    private static String getResponse(InputStream inStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[BUFFER];
        while ((len = inStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        byte[] data = outputStream.toByteArray();
        outputStream.close();
        return new String(data);
    }

}
