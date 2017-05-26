package com.wtz.tools.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

import com.wtz.tools.ShellUtils;
import com.wtz.tools.ShellUtils.CommandResult;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class NetworkDeviceUtils {

    private static final String TAG = NetworkDeviceUtils.class.getName();

    public static boolean isNetConnected(Context cxt) {
        if (cxt == null) {
            Log.d(TAG, "isNetConnected: Context is null");
            return false;
        }

        ConnectivityManager connectivity = (ConnectivityManager) cxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.d(TAG, "isNetConnected: true");
                        return true;
                    }
                }
            }
        }

        Log.d(TAG, "isNetConnected: false");
        return false;
    }

    public static String getLocalIPAddress() {
        String ipAddress = null;
        try {
            for (Enumeration mEnumeration = NetworkInterface.getNetworkInterfaces(); mEnumeration
                    .hasMoreElements();) {
                NetworkInterface netInterface = (NetworkInterface) mEnumeration.nextElement();
                if (netInterface.getName().toLowerCase().equals("eth0")
                        || netInterface.getName().toLowerCase().equals("wlan0")) {
                    for (Enumeration enumIPAddr = netInterface.getInetAddresses(); enumIPAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = (InetAddress) enumIPAddr.nextElement();
                        // 如果不是回环地址
                        if (!inetAddress.isLoopbackAddress()) {
                            ipAddress = inetAddress.getHostAddress().toString();
                            if (!ipAddress.contains("::")) {// 如果不是ipV6的地址
                                return ipAddress;
                            }
                        }
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String readDevMac() {
        String mac = readDevMacFromEth0();
        if (TextUtils.isEmpty(mac)) {
            mac = readDevMacFromWlan0();
        }
        return mac;
    }

    /**
     * 从设备配置文件"/sys/class/net/eth0/address"中读取设备mac地址。
     * 
     * @return 设备的mac地址
     */
    public static String readDevMacFromEth0() {
        final String path = "/sys/class/net/eth0/address";
        return readDevMac(path);
    }

    /**
     * 从设备配置文件"/sys/class/net/wlan0/address"中读取设备mac地址。
     * 
     * @return 设备的mac地址
     */
    public static String readDevMacFromWlan0() {
        final String path = "/sys/class/net/wlan0/address";
        return readDevMac(path);
    }

    private static String readDevMac(final String path) {

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        String devMac = "";
        try {
            fis = new FileInputStream(new File(path));
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();
            buffer.append(br.readLine());
            devMac = buffer.toString().trim();
            Log.d(TAG, "read mac from " + path + "-" + devMac);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // just ignore
                e.printStackTrace();
            }
        }
        return devMac;
    }

    public static String getBSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getBSSID() != null
                && !wifiInfo.getBSSID().equals("00:00:00:00:00:00")) {
            return wifiInfo.getBSSID();
        } else {
            List<ScanResult> list = wifiManager.getScanResults();
            int len = list.size();
            int index = -1;
            int maxLevel = -10000;
            for (int i = 0; i < len; i++) {
                ScanResult result = list.get(i);
                if (result.level > maxLevel) {
                    index = i;
                    maxLevel = result.level;
                }
            }

            if (index >= 0) {
                return list.get(index).BSSID;
            } else {
                return null;
            }
        }
    }

    public static String getSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getSSID() != null) {
            return wifiInfo.getSSID();
        }

        List<ScanResult> list = wifiManager.getScanResults();
        if (null == list) {
            return null;
        }
        int len = list.size();
        int index = -1;
        int maxLevel = -10000;
        for (int i = 0; i < len; i++) {
            ScanResult result = list.get(i);
            if (null != result && result.level > maxLevel) {
                index = i;
                maxLevel = result.level;
            }
        }

        if (index >= 0) {
            return list.get(index).SSID;
        }

        return null;
    }

    /**
     * @param context
     * @return ANDROID_ID
     */
    public static String getAndroidID(Context context) {
        try {
            return Settings.System.getString(context.getContentResolver(),
                    Settings.System.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getGateWay(Context context) {
        String gateWay = null;
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
            if (null != activeInfo && activeInfo.isConnected()) {
                if (ConnectivityManager.TYPE_WIFI == activeInfo.getType()) {
                    gateWay = getWifiGateway(context);
                } else {
                    gateWay = getEth0Gateway();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gateWay;
    }

    private static String getWifiGateway(Context context) {
        String gateWay = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            DhcpInfo di = wifiManager.getDhcpInfo();
            gateWay = int2ip(di.gateway);
        }
        return gateWay;
    }

    private static String getEth0Gateway() {
        String gateWay = null;
        String prefix = "default via ";
        String suffix = " dev eth0";
        String cmd = "ip route show | grep \"default via\" | grep \"dev eth0\"";
        CommandResult ret = ShellUtils.execCommand(cmd, false);
        Log.d(TAG, ret.toString());
        if (!TextUtils.isEmpty(ret.successMsg)) {
            int start = ret.successMsg.indexOf(prefix);
            int end = ret.successMsg.indexOf(suffix);
            if (start != -1 && end != -1) {
                start = start + prefix.length();
                gateWay = ret.successMsg.substring(start, end);
            }
        }
        return gateWay;
    }

    private static String int2ip(long ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf((int) (ip & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
        return sb.toString();
    }
}
