package com.wtz.tools.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wtz.tools.R;


public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "TEST-WEBVIEW";

    private TextView mtitle;

    private View mProgressLayout;
    private ProgressBar mProgressBar;
    private TextView mProgressText;

    private ViewGroup mWebviewContainer;
    private WebView mWebView;
    private WebSettings mWebSettings;

    private Button mBaseLoadButton;
    private Button mCallJsButton;
    private Button mCallAndroidButton;

    private static final String HOME_URL = "http://www.baidu.com/";
    private static final String CALL_JS_PATH = "file:///android_asset/call_js.html";
    private static final String CALL_ANDROID_PATH = "file:///android_asset/js_call_android.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onDestroy");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);

        mtitle = (TextView) findViewById(R.id.title);

        initProgress();

        initWebview();

        initTestButton();

        mWebView.loadUrl(HOME_URL);
    }

    private void initTestButton() {
        mBaseLoadButton = (Button) findViewById(R.id.btn_base_load);
        mBaseLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(HOME_URL);
            }
        });

        mCallJsButton = (Button) findViewById(R.id.btn_android_call_js);
        mCallJsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(CALL_JS_PATH);
                // JS代码在 onPageFinished 回调之后调用
            }
        });

        // JsCallAndroid 类对象映射到js的test对象
        // 此方法在Android4.2以下版本存在严重漏洞问题
        // 4.2以下建议使用onJsPrompt来调用
        mWebView.addJavascriptInterface(new JsCallAndroid(), "test");
        mCallAndroidButton = (Button) findViewById(R.id.btn_js_call_android);
        mCallAndroidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(CALL_ANDROID_PATH);
            }
        });
    }

    private void initWebview() {
        mWebviewContainer = (ViewGroup) findViewById(R.id.fl_webview_container);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mWebviewContainer.addView(mWebView);

        mWebSettings = mWebView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8"); //设置编码格式

        // 屏幕适应与缩放
        mWebSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
        mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWebSettings.setSupportZoom(true); // 支持缩放，默认为true，是下面的前提
        mWebSettings.setBuiltInZoomControls(true); // 设置内置的缩放控件。若为 false，则该WebView不可缩放
        mWebSettings.setDisplayZoomControls(false); // 隐藏原生的缩放控件

        // 默认情况html代码下载到WebView后，webkit开始解析网页各个节点，发现有外部样式文件或者外部脚本文件时，
        // 会异步发起网络请求下载文件，但如果在这之前也有解析到image节点，那势必也会发起网络请求下载相应的图片。
        // 在网络情况较差的情况下，过多的网络请求就会造成带宽紧张，影响到css或js文件加载完成的时间，
        // 造成页面空白loading过久。
        // 解决的方法就是告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 因为4.4以上系统在onPageFinished时再恢复图片加载时，如果存在多张图片引用的是相同的src时，
            // 会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
            mWebSettings.setLoadsImagesAutomatically(true);
        } else {
            mWebSettings.setLoadsImagesAutomatically(false);
        }

        // JS设置
        mWebSettings.setJavaScriptEnabled(true);// 设置与Js交互的权限
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口

        mWebSettings.setAllowFileAccess(true); // 设置可以访问文件
        mWebSettings.setDomStorageEnabled(true); // 开启 DOM storage API 功能
        mWebSettings.setDatabaseEnabled(true); // 开启 database storage API 功能

        // 缓存设置
        mWebSettings.setAppCacheEnabled(true); // 开启 Application Caches 功能
        mWebSettings.setAppCachePath(getCacheDir().getAbsolutePath());// 设置 Application Caches 缓存目录
        if (isNetworkConnect(getApplicationContext())) {
            // 根据cache-control决定是否从网络上取数据
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            // 没网，则从本地获取，即离线加载
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        // 5.1以上默认禁止了https和http混用，以下方式是开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 解决问题：ERR_UNKNOWN_URL_SCHEME
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                // 设置不用系统浏览器打开，直接显示在当前 WebView
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d(TAG, "onPageStarted:" + url);
                showProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished:" + url);
                if (!mWebSettings.getLoadsImagesAutomatically()) {
                    mWebSettings.setLoadsImagesAutomatically(true);
                }
                hideProgress();

                if (CALL_JS_PATH.equals(url)) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        mWebView.loadUrl("javascript:callJS()");
                    } else {
                        mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                //此处为 js 返回的结果
                                Log.d(TAG, "evaluateJavascript onReceiveValue:" + value);
                            }
                        });
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e(TAG, "onReceivedError:" + request.toString() + ";" + error.toString());
//                switch (errorCode()) {
//                    case HttpStatus.SC_NOT_FOUND:
//                        view.loadUrl("file:///android_assets/error_handle.html");
//                        break;
//                }
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e(TAG, "onReceivedHttpError:" + request.toString() + ";" + errorResponse.toString());
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.e(TAG, "onReceivedSslError:" + error.toString());
                handler.proceed(); // 表示等待证书响应
                // handler.cancel(); // 表示挂起连接，为默认方式
                // handler.handleMessage(null); // 可做其他处理
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                // 获取网站标题
                Log.d(TAG, "onReceivedTitle:" + title);
                mtitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 获取加载进度
                updateProgress(newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("JsAlert")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("JsConfirm")
                        .setMessage(message)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
                Uri uri = Uri.parse(message);
                // 特殊自定义js回调操作
                if (uri.getScheme().equals("js")) {
                    // 如果url的协议 = 预先约定的 js 协议，就解析往下解析参数
                    // 例如：url = "js://webview?arg1=111&arg2=222"
                    // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");

                        // 可以在协议上带有参数并传递到Android上
                        //Set<String> collection = uri.getQueryParameterNames();

                        //参数result:代表消息框的返回值(输入值)
                        result.confirm("js通过onJsPrompt调用了Android的方法");
                    }
                    return true;
                }

                // 一般提示编辑操作
                final EditText et = new EditText(WebViewActivity.this);
                et.setText(defaultValue);
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle(message)
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm(et.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .setCancelable(false)
                        .show();

                return true;
            }
        });
    }

    private void initProgress() {
        mProgressLayout = findViewById(R.id.ll_progress);
        mProgressLayout.setVisibility(View.INVISIBLE);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
        mProgressBar.setMax(100);
        mProgressText = (TextView) findViewById(R.id.tv_progress);
        mProgressText.setText("");
    }

    private void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    private void updateProgress(int newProgress) {
        mProgressBar.setProgress(newProgress);
        mProgressText.setText(newProgress + "%");
    }

    private void hideProgress() {
        mProgressLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        mWebSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        mWebSettings.setJavaScriptEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "Keycode:" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mWebView.canGoBack()) {
                    // 点击返回上一页面而不是退出浏览器
                    mWebView.goBack();
                } else {
                    finish();
                }
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mWebView != null) {
            // 销毁Webview
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroy();
    }

    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            return info != null && info.isAvailable();
        }

        return false;
    }

    public class JsCallAndroid extends Object {
        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void hello(String msg) {
            System.out.println(msg);
            Toast.makeText(WebViewActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
