package com.wtz.tools.network;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
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
