package com.wtz.tools.Receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.wtz.tools.utils.network.NetworkDeviceUtils;

import java.util.List;

public class NetReceiver extends BaseReceiver {

    private final String TAG = NetReceiver.class.getSimpleName();

    public NetReceiver(Context context) {
        super(context);
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        // net change
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        // wifi
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        super.register(filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive:" + action);
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkDeviceUtils.isNetworkConnect(context)) {
                Log.d(TAG, "Network is connected");
            } else {
                Log.d(TAG, "Network is disconnected");
            }
        } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            onWifiScanResult(context);
        } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            onWifiStateChange(context, intent);
        }
    }

    private void onWifiScanResult(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult sr : scanResults) {
            Log.d(TAG, sr.toString());
        }
    }

    private void onWifiStateChange(Context context, Intent intent) {
        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
            Log.d(TAG, "连接已断开");
        } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            Log.d(TAG, "已连接到网络:" + wifiInfo.getSSID());
        } else {
            NetworkInfo.DetailedState state = info.getDetailedState();
            if (state == state.CONNECTING) {
                Log.d(TAG, "连接中...");
            } else if (state == state.AUTHENTICATING) {
                Log.d(TAG, "正在验证身份信息...");
            } else if (state == state.OBTAINING_IPADDR) {
                Log.d(TAG, "正在获取IP地址...");
            } else if (state == state.FAILED) {
                Log.d(TAG, "连接失败");
            }
        }
    }
    
}
