package com.wtz.tools.utils.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wtz.tools.utils.file.FileUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class RetrofitTest {
    //    private static final String BASE_URL = "http://localhost:3003/";
    private static final String BASE_URL = "http://192.168.0.100:3003/";
    //    private static final String SAVE_FILE_DIR = "d:\\WTZ\\Desktop\\test";
    private static final String SAVE_FILE_DIR = "/sdcard/";

    /**
     * 使用json-server部署rest-api来测试:
     * json-server db.json -p 3003
     * <p>
     * Retrofit的Url组合规则：
     * +-----------------------------+------------------------+---------------------------------+
     * | BaseUrl                     | 注解中提供的URL相关值   | 合并结果                         |
     * +-----------------------------+------------------------+---------------------------------+
     * | http://localhost:3003/path/ | /test                  | http://localhost:3003/test      |
     * +-----------------------------+------------------------+---------------------------------+
     * | http://localhost:3003/path/ | test                   | http://localhost:3003/path/test |
     * +-----------------------------+------------------------+---------------------------------+
     * | http://localhost:3003/path/ | https://www.baidu.com/ | https://www.baidu.com/          |
     * +-----------------------------+------------------------+---------------------------------+
     * <p>
     * 关于 Converter
     * Converter 是对响应 Call<T> 中 T 进行转换
     * <p>
     * 关于 CallAdapter
     * CallAdapter 是对响应 Call<T> 中 Call 进行转换，比如使用 RxJava 可以把 Call 转换为 Observable
     * <p>
     * 使用 RxJava 时注意：
     * 使用 Observable<T> 作为响应体时，无法获取到返回的 Header和响应码，有两种方案代替：
     * 1、用 Observable<Response<T>> 代替 Observable<T> ，这里的 Response 指 retrofit2.Response；
     * 2、用 Observable<Result<T>> 代替 Observable<T> ，这里的 Result 是指 retrofit2.adapter.rxjava.Result，
     * 这个 Result 中包含了 Response 的实例
     * <p>
     * 注意：Call.enqueue在android中会回调到UI线程
     */
    public static void test() {
        System.out.println("test...Thread ID = " + Thread.currentThread().getId());
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(new BaseInterceptor())
                .addInterceptor(new HttpLoggingInterceptor())
                .followRedirects(true);

        if (false) {// 测试是否要忽略校验https的域名和证书
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

        ApiService service = retrofit.create(ApiService.class);

        //演示 @Url 和 @Query
        Call<ResponseBody> callUrlQuery1 = service.testUrlAndQuery1("blog", 2);
        callUrlQuery1.enqueue(getDefaultCallback());

        //演示 @Url 和 @QueryMap
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", 2);
        params.put("likes", 505);
        Call<ResponseBody> callUrlQuery2 = service.testUrlAndQuery2("blog", params);
        callUrlQuery2.enqueue(getDefaultCallback());

        //演示 @Headers 和 @Header
        Call<ResponseBody> callHeader = service.testHeader("customHeaderValue3");
        callHeader.enqueue(getDefaultCallback());

        // 演示 @Path
        //Call<ResponseBody> call = service.testPath(1);
        Call<ResponseBody> callPath = service.testPath2(1);
        callPath.enqueue(getDefaultCallback());

        // 演示下载文件 和 @Streaming
        String url = "http://pic18.photophoto.cn/20110106/0020032817703440_b.jpg";
        Call<ResponseBody> callDownload = service.downloadFile(url);
        callDownload.enqueue(getDownloadCallback());

        // 演示 @FormUrlEncoded 和 @Field
        Call<ResponseBody> call1 = service.testFormUrlEncoded1("李四", 24);
        call1.enqueue(getDefaultCallback());

        // 演示 @FormUrlEncoded 和 @FieldMap
        Map<String, Object> map = new HashMap<>();
        map.put("name", "王五");
        map.put("age", 22);
        Call<ResponseBody> call2 = service.testFormUrlEncoded2(map);
        call2.enqueue(getDefaultCallback());

        MediaType textType = MediaType.parse("text/plain");
        RequestBody name = RequestBody.create(textType, "小明");
        RequestBody age = RequestBody.create(textType, "23");
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "这里是模拟文件的内容");

        // 演示 @Multipart 和 @Part
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
        Call<ResponseBody> call3 = service.testFileUpload1(name, age, filePart);
        call3.enqueue(getDefaultCallback());

        // 演示 @Multipart 和 @PartMap
        // 实现和上面同样的效果
        Map<String, RequestBody> fileUpload2Args = new HashMap<>();
        fileUpload2Args.put("name", name);
        fileUpload2Args.put("age", age);
        // 还有一种不推荐的方式上传文件：即文件名变成 表单键名"; filename="文件名 （两端的引号会自动加，所以这里不加）
        // fileUpload2Args.put("file\"; filename=\"test.txt", file);
        Call<ResponseBody> call4 = service.testFileUpload2(fileUpload2Args, filePart); //单独处理文件
        call4.enqueue(getDefaultCallback());

        // 演示 @Body
        FormBean formBean = new FormBean();
        formBean.name = "小酒窝";
        formBean.age = 6;
        Call<ResponseBody> call5 = service.testBody(formBean);
        call5.enqueue(getDefaultCallback());

        // 演示 RxJava2CallAdapter 使用 Observable<T> 作为响应体
        Observable<String> stringObservable = service.testRxjavaAdapter1(2);
        stringObservable.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(getStringObserver());

        // 演示 RxJava2CallAdapter 使用 Observable<Response<T>> 作为响应体
        Observable<Response<String>> responseObservable = service.testRxjavaAdapter2(2);
        responseObservable.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(getResponseObserver());

        // 演示 RxJava2CallAdapter 使用 Observable<Result<T>> 作为响应体
        Observable<Result<String>> resultObservable = service.testRxjavaAdapter3(2);
        resultObservable.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .subscribe(getResultObserver());

        // 演示 RxJava2CallAdapter 使用 Observable<ResponseBody> 作为响应体 和 下载文件
        Observable<ResponseBody> downloadObservable = service.testRxjavaAdapter4Download(url);
        downloadObservable.subscribeOn(Schedulers.io()) // 指定线程请求文件
                .observeOn(Schedulers.io()) //指定线程保存文件
                .subscribe(getDownloadObserver());
    }

    public interface ApiService {

        /**
         * 当GET、POST...HTTP等方法中没有设置Url时，则必须使用 {@link Url}提供；
         * url后的请求参数可以使用Query和QueryMap；
         * 对于Query和QueryMap，如果不是String（或Map的第二个泛型参数不是String）时，
         * 会被默认会调用toString转换成String类型；
         * Url支持的类型有 okhttp3.HttpUrl, String, java.net.URI, android.net.Uri
         * {@link QueryMap} 用法和 {@link FieldMap} 用法一样
         */
        @GET
        //当有URL注解时，这里的URL就省略了
        Call<ResponseBody> testUrlAndQuery1(@Url String url, @Query("id") int id);

        @GET
        Call<ResponseBody> testUrlAndQuery2(@Url String url, @QueryMap Map<String, Object> params);

        @GET("/blog")
        @Headers({"CustomHeader1: customHeaderValue1", "CustomHeader2: customHeaderValue2"})
        Call<ResponseBody> testHeader(@Header("CustomHeader3") String customHeaderValue3);

        @GET("blog/{id}")
        Call<ResponseBody> testPath(@Path("id") int id);

        /**
         * method 表示请求的方法，区分大小写
         * path表示路径；{占位符}尽量只用在URL的path部分
         * hasBody表示是否有请求体
         */
        @HTTP(method = "GET", path = "blog/{id}", hasBody = false)
        Call<ResponseBody> testPath2(@Path("id") int id);

        /**
         * @Streaming 表示响应体的数据用流的形式返回，常用于下载大文件；
         * 如果没有使用该注解，默认会把所有数据载入内存，容易造成OOM
         */
        @Streaming
        @GET
        Call<ResponseBody> downloadFile(@Url String fileUrl);

        /**
         * 注意 @Field 和 @FieldMap 需要与 @FormUrlEncoded 结合使用
         * {@link FormUrlEncoded} 表明是一个表单格式的请求（Content-Type:application/x-www-form-urlencoded）
         * <code>Field("username")</code> 表示将后面的 <code>String name</code> 中name的取值作为 username 的值
         */
        @POST("/form")
        @FormUrlEncoded
        Call<ResponseBody> testFormUrlEncoded1(@Field("name") String name, @Field("age") int age);

        /**
         * Map的key作为表单的键
         */
        @POST("/form")
        @FormUrlEncoded
        Call<ResponseBody> testFormUrlEncoded2(@FieldMap Map<String, Object> map);

        /**
         * {@link Part} 后面支持三种类型，{@link RequestBody}、{@link MultipartBody.Part} 、任意类型
         * 除 {@link MultipartBody.Part} 以外，其它类型都必须带上表单字段({@link MultipartBody.Part} 中已经包含了表单字段的信息)，
         */
        @POST("/form")
        @Multipart
        Call<ResponseBody> testFileUpload1(@Part("name") RequestBody name, @Part("age") RequestBody age, @Part MultipartBody.Part file);

        /**
         * PartMap 注解支持一个Map作为参数，支持 {@link RequestBody } 类型，
         * 如果有其它的类型，会被{@link Converter}转换，如使用{@link Gson} 的 {@link retrofit2.converter.gson.GsonRequestBodyConverter}
         * 所以{@link MultipartBody.Part} 就不适用了,所以文件只能用<b> @Part MultipartBody.Part </b>
         */
        @POST("/form")
        @Multipart
        Call<ResponseBody> testFileUpload2(@PartMap Map<String, RequestBody> args, @Part MultipartBody.Part file);

        /**
         * 使用 @Body 注解可以把参数放到请求体中，适用于 POST/PUT请求；
         * 相当于多个 @Field ，能以对象的方式提交，但需要 addConverterFactory，否则会报错类似如下：
         * Could not locate RequestBody converter for class ...
         */
        @POST("/form")
        Call<ResponseBody> testBody(@Body FormBean formBean);

        @GET("blog/{id}")
        Observable<String> testRxjavaAdapter1(@Path("id") int id);

        @GET("blog/{id}")
        Observable<Response<String>> testRxjavaAdapter2(@Path("id") int id);

        @GET("blog/{id}")
        Observable<Result<String>> testRxjavaAdapter3(@Path("id") int id);

        @Streaming
        @GET
        Observable<ResponseBody> testRxjavaAdapter4Download(@Url String fileUrl);
    }

    private static Callback<ResponseBody> getDefaultCallback() {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("DefaultCallback onResponse...Thread ID = " + Thread.currentThread().getId()
                        + ", tid = " + android.os.Process.myTid());
                try {
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("DefaultCallback onFailure...Thread ID = " + Thread.currentThread().getId()
                        + ", tid = " + android.os.Process.myTid());
                t.printStackTrace();
            }
        };
    }

    private static Callback<ResponseBody> getDownloadCallback() {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                System.out.println("DownloadCallback onResponse...Thread ID = " + Thread.currentThread().getId()
                        + ", tid = " + android.os.Process.myTid());
                // Call.enqueue在android中会回调到UI线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBody responseBody = response.body();
                        boolean ret = FileUtil.saveFileFromSteam(responseBody.byteStream(), responseBody.contentLength(),
                                SAVE_FILE_DIR, "download.jpg");
                        System.out.println("DownloadCallback file download was success? " + ret);
                    }
                }).start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("DownloadCallback onFailure...Thread ID = " + Thread.currentThread().getId()
                        + ", tid = " + android.os.Process.myTid());
                t.printStackTrace();
            }
        };
    }

    private static Observer<String> getStringObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("testRxjavaAdapter1...onSubscribe");
            }

            @Override
            public void onNext(String s) {
                System.out.println("testRxjavaAdapter1...onNext:" + s);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("testRxjavaAdapter1...onError：" + e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("testRxjavaAdapter1...onComplete");
            }
        };
    }

    private static Observer<Response<String>> getResponseObserver() {
        return new Observer<Response<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("testRxjavaAdapter2...onSubscribe");
            }

            @Override
            public void onNext(Response<String> stringResponse) {
                System.out.println("testRxjavaAdapter2...onNext:" + stringResponse
                        + ";\n" + stringResponse.body().toString());
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("testRxjavaAdapter2...onError:" + e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("testRxjavaAdapter2...onComplete");
            }
        };
    }

    private static Observer<Result<String>> getResultObserver() {
        return new Observer<Result<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("testRxjavaAdapter3...onSubscribe");
            }

            @Override
            public void onNext(Result<String> stringResult) {
                if (stringResult != null && !stringResult.isError()) {
                    System.out.println("testRxjavaAdapter3...onNext ok:" + stringResult.response()
                            + ";\n" + stringResult.response().body().toString());
                } else {
                    System.out.println("testRxjavaAdapter3...onNext error:" + stringResult.error());
                }
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("testRxjavaAdapter3...onError：" + e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("testRxjavaAdapter3...onComplete");
            }
        };
    }

    private static Observer<ResponseBody> getDownloadObserver() {
        return new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("testRxjavaAdapter4Download...onSubscribe");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                System.out.println("testRxjavaAdapter4Download...onNext");
                boolean ret = FileUtil.saveFileFromSteam(responseBody.byteStream(), responseBody.contentLength(),
                        SAVE_FILE_DIR, "download2.jpg");
                System.out.println("DownloadObserver file download was success? " + ret);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("testRxjavaAdapter4Download...onError:" + e.toString());
            }

            @Override
            public void onComplete() {
                System.out.println("testRxjavaAdapter4Download...onComplete");
            }
        };
    }

    static class FormBean {
        int id;
        String name;
        int age;
    }

    static class BaseInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originRequest = chain.request();
            HttpUrl url = originRequest.url();
            // 可以对url做一些处理，或者其它参数处理
            Request newRequest = originRequest.newBuilder()
                    .method(originRequest.method(), originRequest.body())
                    .url(url)
                    .build();
            return chain.proceed(newRequest);
        }
    }

    static class HttpLoggingInterceptor implements Interceptor {

        String tag = "HttpLoggingInterceptor";

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
//            Log.d(tag, String.format("Sending request %s %s %n%s",
//                    request.method(), request.url(), request.headers()));
            System.out.println(String.format("Sending request %s %s %n%s",
                    request.method(), request.url(), request.headers()));

            okhttp3.Response response = chain.proceed(request);

            long t2 = System.nanoTime();
//            Log.d(tag, String.format("Received response for %s %s in %.1fms%n%s",
//                    response.request().method(), response.request().url(), (t2 - t1) / 1e6d
//                    , response.headers()));
            System.out.println(String.format("Received response for %s %s in %.1fms%n%s",
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

}
