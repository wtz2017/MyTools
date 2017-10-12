package com.wtz.tools.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

public class HttpUtils {
    private final static String TAG = HttpUtils.class.getSimpleName();

    private static final String METHOD_GET = "GET";
    private static final int HTTP_OK = 200;
    private static final int BUFFER = 1024 * 8;

    public static String get(String urlStr, int connectTimeout, int readTimeout, long id)
            throws Exception {
        Log.d(TAG, "http get: [id = " + id + "]...url = " + urlStr);

        URL url = null;
        HttpURLConnection conn = null;
        InputStream inStream = null;
        String response = null;
        try {
            URI uri = new URI(urlStr);
            url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(1000 * connectTimeout);
            conn.setReadTimeout(1000 * readTimeout);
            conn.setRequestMethod(METHOD_GET);
            conn.setRequestProperty("accept", "*/*");
            conn.connect();
            int responseCode = conn.getResponseCode();
            Log.d(TAG, "http get: responseCode = " + responseCode);
            if (responseCode == HTTP_OK) {
                inStream = conn.getInputStream();
            } else {
                inStream = conn.getErrorStream();
            }
            response = getResponse(inStream);
        } finally {
            if (inStream != null)
                inStream.close();
            if (conn != null) {
                conn.disconnect();
            }
        }

        Log.d(TAG, "http get response: [id = " + id + "]...response = " + response);
        return response;
    }

    private static String getResponse(InputStream inStream) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = -1;
            byte[] buffer = new byte[BUFFER];
            while ((len = inStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            byte[] data = outputStream.toByteArray();
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return "getResponse error!";
        }
    }
    
    public static String postJson(String actionUrl, String jsonStr) throws Exception {
        HttpURLConnection conn = null;
        OutputStreamWriter outStream = null;
        InputStream inStream = null;
        String response = null;

        try {
            URI uri = new URI(actionUrl);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);// TODO
            conn.setReadTimeout(5000);// TODO
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");

            if (jsonStr != null) {
                outStream = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                outStream.write(jsonStr);
                outStream.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("http post: responseCode = " + responseCode + ", url:" + actionUrl);
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
    
    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * 
     * @param actionUrl
     *            访问的服务器URL
     * @param params
     *            普通参数
     * @param files
     *            文件参数
     * @return
     * @throws IOException
     */
    public static String postFile(String actionUrl, Map<String, String> params,
            Map<String, File> files) throws Exception {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        InputStream inStream = null;
        String response = null;

        try {
            URI uri = new URI(actionUrl);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);// TODO
            conn.setReadTimeout(5000); // TODO
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);

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
            if (files != null) {
                if (outStream == null) {
                    outStream = new DataOutputStream(conn.getOutputStream());
                }
                for (Map.Entry<String, File> file : files.entrySet()) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(PREFIX);
                    stringBuilder2.append(BOUNDARY);
                    stringBuilder2.append(LINEND);
                    // TODO name是post中传参的键, filename是文件的名称
                    stringBuilder2
                            .append("Content-Disposition: form-data; name=\"" + file.getKey() + "\"; filename=\""
                                    + file.getValue().getName() + "\"" + LINEND);
                    stringBuilder2.append(
                            "Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    stringBuilder2.append(LINEND);
                    outStream.write(stringBuilder2.toString().getBytes());

                    InputStream is = new FileInputStream(file.getValue());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    is.close();
                    outStream.write(LINEND.getBytes());
                }

                // 请求结束标志
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
                outStream.write(end_data);
                outStream.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("http post: responseCode = " + responseCode + ", url:" + actionUrl);
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

    /**
     * 通过拼接的方式构造请求内容，实现参数传输以及文件传输
     * 
     * @param actionUrl
     *            访问的服务器URL
     * @param params
     *            普通参数
     * @param files
     *            数据字节流
     * @return
     * @throws IOException
     */
    public static String postBytes(String actionUrl, Map<String, String> params,
            Map<String, byte[]> files) throws Exception {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--";
        String LINEND = "\r\n";
        String MULTIPART_FORM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";

        HttpURLConnection conn = null;
        DataOutputStream outStream = null;
        InputStream inStream = null;
        String response = null;

        try {
            URI uri = new URI(actionUrl);
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);// TODO
            conn.setReadTimeout(5000); // TODO
            conn.setDoInput(true);// 允许输入
            conn.setDoOutput(true);// 允许输出
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + ";boundary=" + BOUNDARY);

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
            if (files != null) {
                if (outStream == null) {
                    outStream = new DataOutputStream(conn.getOutputStream());
                }
                for (Map.Entry<String, byte[]> file : files.entrySet()) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(PREFIX);
                    stringBuilder2.append(BOUNDARY);
                    stringBuilder2.append(LINEND);
                    // TODO name是post中传参的键, filename是文件的名称
                    stringBuilder2
                            .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                    + file.getKey() + "\"" + LINEND);
                    stringBuilder2.append(
                            "Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    stringBuilder2.append(LINEND);
                    outStream.write(stringBuilder2.toString().getBytes());

                    outStream.write(file.getValue());
                    outStream.write(LINEND.getBytes());
                }

                // 请求结束标志
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
                outStream.write(end_data);
                outStream.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("http post: responseCode = " + responseCode + ", url:" + actionUrl);
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

    public static int postFile(String urlStr, Map<String, String> paramsMap,
            Map<String, File> filesMap, int connectTimeout, int soTimeout) throws Exception {
        Log.d(TAG, "httpPostFiles...url = " + urlStr);
        Log.d(TAG, "httpPostFiles...paramsMap = " + paramsMap);
        if (TextUtils.isEmpty(urlStr) || paramsMap == null || filesMap == null) {
            return -1;
        }

        String result = "unKnow result!";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                HttpVersion.HTTP_1_1);
        httpclient.getParams()
                .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);

        HttpPost httppost = new HttpPost(urlStr);
        addStringHeader(paramsMap, httppost);
        addFileEntity(filesMap, httppost);

        HttpResponse response = httpclient.execute(httppost);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
            result = EntityUtils.toString(resEntity, "utf-8");
            resEntity.consumeContent();
        }

        httpclient.getConnectionManager().shutdown();

        Log.d(TAG, result);
        return statusCode;
    }

    private static void addStringHeader(Map<String, String> map, HttpPost httppost)
            throws Exception {
        Set<String> keySet = map.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String key = it.next();
            String value = map.get(key);
            httppost.addHeader(key, value);
        }
    }

    private static void addFileEntity(Map<String, File> map, HttpPost httppost) throws Exception {
        MultipartEntity mpEntity = new MultipartEntity();
        Set<String> keySet = map.keySet();
        for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
            String key = it.next();
            File value = map.get(key);
            ContentBody cbFile = new FileBody(value);
            mpEntity.addPart("file", cbFile);
        }
        httppost.setEntity(mpEntity);
    }
}
