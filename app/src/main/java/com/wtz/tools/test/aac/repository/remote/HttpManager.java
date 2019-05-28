package com.wtz.tools.test.aac.repository.remote;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wtz.tools.utils.network.SSLUtil;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();

    private static final String BASE_URL_RELEASE = "https://xxx.xxx/";
    private static final String BASE_URL_DEBUG = "https://xxx.test.xxx/";
    private String BASE_URL;

    private static final boolean DEBUG = false;
    private NetApi mNetApi;

    public static final Gson GSON = new Gson();

    private static volatile HttpManager INSTANCE;

    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    private HttpManager() {
        if (DEBUG) {
            BASE_URL = BASE_URL_DEBUG;
        } else {
            BASE_URL = BASE_URL_RELEASE;
        }

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor())
                .followRedirects(true);

        if (true) {// 测试是否要忽略校验https的域名和证书
            httpClientBuilder.hostnameVerifier(SSLUtil.getIgnoredHostnameVerifier());
            SSLContext ignoredSSLContext = SSLUtil.getIgnoredSSLContext();
            if (ignoredSSLContext != null) {
                httpClientBuilder.sslSocketFactory(ignoredSSLContext.getSocketFactory());
            }
        }

        Gson gson = new GsonBuilder()
                //配置你的Gson
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                // baseUrl 中的路径(path)必须以 / 结束
                .baseUrl(BASE_URL)
                // 这里先添加了一个StringConverter，否则后边获取string结果时会报错：IllegalStateException: Expected a string but was BEGIN_OBJECT
                // 如果有多个ConverterFactory都支持同一种类型，那么就是只有第一个才会被使用；
                // 如果有GsonConverter，一定要放在它前面，因为 GsonConverterFactory是不判断是否支持的
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mNetApi = retrofit.create(NetApi.class);
    }

    static class HttpLoggingInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.d(TAG, String.format("Sending request %s %s %n%s",
                    request.method(), request.url(), request.headers()));

            okhttp3.Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d(TAG, String.format("Received response for %s %s in %.1fms%n%s",
                    response.request().method(), response.request().url(), (t2 - t1) / 1e6d
                    , response.headers()));
            // 注意，不要在这里使用response.body().string()，因为response.body().string()只能请求一次，
            // 请求过后就会关闭，再次调用response.body().string()就会报异常：IllegalStateException: closed at okio.RealBufferedSource.read

            return response;
        }
    }

    /**
     * 自定义Converter实现RequestBody到String的转换
     */
    static class StringConverter implements Converter<ResponseBody, String> {

        public static final StringConverter INSTANCE = new StringConverter();

        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }

    /**
     * 用于向Retrofit提供StringConverter
     */
    static class StringConverterFactory extends Converter.Factory {

        private static final StringConverterFactory INSTANCE = new StringConverterFactory();

        public static StringConverterFactory create() {
            return INSTANCE;
        }

        // 我们只关实现从ResponseBody 到 String 的转换，所以其它方法可不覆盖
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (type == String.class) {
                return StringConverter.INSTANCE;
            }
            //其它类型我们不处理，返回null就行
            return null;
        }
    }

    public NetApi getApi() {
        return mNetApi;
    }

}
