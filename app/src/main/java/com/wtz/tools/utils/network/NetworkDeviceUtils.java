package com.wtz.tools.utils.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkDeviceUtils {

    private static final String TAG = NetworkDeviceUtils.class.getName();

    public static boolean isNetworkConnect(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            return info != null && info.isAvailable();
        }

        return false;
    }

    public static String getWlan0Mac(Context context) {
        String mac = null;
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                mac = wifiInfo.getMacAddress();
            }
        }
        if (TextUtils.isEmpty(mac) || mac.equals("02:00:00:00:00:00")) {
            mac = getLocalMac("wlan0");
        }
        if (TextUtils.isEmpty(mac) || mac.equals("02:00:00:00:00:00")) {
            mac = readDevMac("/sys/class/net/wlan0/address");
        }
        return mac;
    }

    public static String getEth0Mac() {
        String mac = getLocalMac("eth0");
        if (TextUtils.isEmpty(mac)) {
            mac = readDevMac("/sys/class/net/eth0/address");
        }
        return mac;
    }

    private static String getLocalMac(String name) {
        String mac = "";

        try {
            NetworkInterface ni = NetworkInterface.getByName(name);
            byte[] address = ni.getHardwareAddress();
            StringBuffer sb = new StringBuffer();
            if (address != null && address.length == 6) {
                sb.append(parseByteToHex(address[0])).append(":").append(
                        parseByteToHex(address[1])).append(":").append(
                        parseByteToHex(address[2])).append(":").append(
                        parseByteToHex(address[3])).append(":").append(
                        parseByteToHex(address[4])).append(":").append(
                        parseByteToHex(address[5]));
                mac = sb.toString();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac;
    }

    private static String parseByteToHex(byte b) {
        // 把0写成00
        String s = "00" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
            }
        }
        return devMac;
    }

    public static JSONArray getWiFiNearbyList(Context context) {
        JSONArray jsonArray = new JSONArray();
        TreeSet<String> wifiNearby = getWiFiNearby(context);
        if (wifiNearby != null && wifiNearby.size() > 0) {
            for (String wifi : wifiNearby) {
                try {
                    String[] temp = wifi.split("_");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("ssid", URLEncoder.encode(temp[0], "UTF-8"));
                    jsonObject.put("bssid", temp[1]);
                    jsonArray.put(jsonObject);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }

    public static TreeSet<String> getWiFiNearby(Context context) {
        if (context != null && isWiFiActive(context)) {
            TreeSet<String> wifiInfoSet = new TreeSet<String>();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();// 搜索到的设备列表
            int count = 0;
            while (scanResults.size() == 0 && count < 5) {
                scanResults = wifiManager.getScanResults();
                count++;
            }
            for (ScanResult scanResult : scanResults) {
                wifiInfoSet.add(scanResult.SSID + "_" + scanResult.BSSID);
            }
            return wifiInfoSet;
        }
        return null;
    }

    public static List<ScanResult> getWiFiNearbyResults(Context context) {
        if (context != null && isWiFiActive(context)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
            int count = 0;
            while (scanResults.size() == 0 && count < 5) {
                scanResults = wifiManager.getScanResults();
                count++;
            }
            return scanResults;
        }
        return null;
    }

    public static boolean isWiFiActive(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (ni.getTypeName().equals("WIFI") && ni.isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    public static String getConnectedBSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getBSSID() != null
                && !wifiInfo.getBSSID().equals("00:00:00:00:00:00")) {
            return wifiInfo.getBSSID();
        }
        return "";
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

    public static Map<String, String> getNetworkInfo(Context context) {
        if (context == null) {
            return null;
        }

        String ip = "";
        String net_type = "";
        String mask = "";
        String gateway = "";
        String dns1 = "";
        String dns2 = "";
        String ssid = "";
        String bssid = "";
        Map<String, String> results = new HashMap<String, String>();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo connectedInfo = null;
        if (cm != null) {
            NetworkInfo[] infos = cm.getAllNetworkInfo();
            for (NetworkInfo ni : infos) {
                if (ni.getState() == NetworkInfo.State.CONNECTED) {
                    connectedInfo = ni;
                    break;
                }
            }
        }

        if (connectedInfo != null) {
            // 已连接
            switch (connectedInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    net_type = "wifi";
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    if (wifiInfo != null) {
                        ip = int2ip(wifiInfo.getIpAddress());
                        ssid = wifiInfo.getSSID();
                        bssid = wifiInfo.getBSSID();
                        DhcpInfo di = wifiManager.getDhcpInfo();
                        if (di != null) {
                            gateway = int2ip(di.gateway);
                            mask = int2ip(di.netmask);
                            dns1 = int2ip(di.dns1);
                            dns2 = int2ip(di.dns2);
                        }
                    }
                    if (TextUtils.isEmpty(ip) || !isIpString(ip)) {
                        ip = androidGetProp("dhcp.wlan0.ipaddress", "");
                    }
                    if (TextUtils.isEmpty(mask)) {
                        mask = androidGetProp("dhcp.wlan0.mask", "");
                    }
                    if (TextUtils.isEmpty(gateway)) {
                        gateway = androidGetProp("dhcp.wlan0.gateway", "");
                    }
                    if (TextUtils.isEmpty(dns1)) {
                        dns1 = androidGetProp("dhcp.wlan0.dns1", "");
                    }
                    if (TextUtils.isEmpty(dns2)) {
                        dns2 = androidGetProp("dhcp.wlan0.dns2", "");
                    }
                    break;
                case ConnectivityManager.TYPE_ETHERNET:
                    net_type = "ethernet";
                    try {
                        for (Enumeration<NetworkInterface> en = NetworkInterface
                                .getNetworkInterfaces(); en.hasMoreElements(); ) {
                            {
                                NetworkInterface netInterface = en.nextElement();
                                List<InterfaceAddress> mList = netInterface.getInterfaceAddresses();
                                for (InterfaceAddress interfaceAddress : mList) {
                                    InetAddress inetAddress = interfaceAddress.getAddress();
                                    if (!inetAddress.isLoopbackAddress()) {
                                        String hostAddress = inetAddress.getHostAddress();
                                        if (!hostAddress.contains("::")) {
                                            ip = hostAddress;
                                            mask = calcMaskByPrefixLength(interfaceAddress
                                                    .getNetworkPrefixLength());
                                        }
                                    }
                                }
                            }
                        }

                        Field cmServiceField = Class.forName(ConnectivityManager.class.getName())
                                .getDeclaredField("mService");
                        cmServiceField.setAccessible(true);
                        // connectivitymanager.mService
                        Object cmService = cmServiceField.get(cm);
                        // get IConnectivityManager class
                        Class cmServiceClass = Class.forName(cmService.getClass().getName());
                        Method methodGetLinkp = cmServiceClass.getDeclaredMethod("getLinkProperties",
                                new Class[]{int.class});
                        methodGetLinkp.setAccessible(true);
                        Object linkProperties = methodGetLinkp.invoke(cmService, ConnectivityManager.TYPE_ETHERNET);

                        Class<?> classLinkp = Class.forName("android.net.LinkProperties");
                        Method methodGetRoutes = classLinkp.getDeclaredMethod("getRoutes");
                        Method methodGetDnses = classLinkp.getDeclaredMethod("getDnses");

                        Collection<InetAddress> inetAddresses = (Collection<InetAddress>) methodGetDnses.invoke(linkProperties);

                        String inetAddressString = inetAddresses.toString();
                        if (inetAddressString.contains(",")) {
                            dns1 = inetAddressString.substring(2,
                                    inetAddressString.lastIndexOf(","));
                            dns2 = inetAddressString.substring(
                                    inetAddressString.lastIndexOf(",") + 3,
                                    inetAddressString.length() - 1);
                        }

                        Collection<Object> routeInfos = (Collection<Object>) methodGetRoutes.invoke(linkProperties);
                        String routeInfoString = routeInfos.toString();
                        if (routeInfoString.contains(">")) {
                            gateway = routeInfoString.substring(
                                    routeInfoString.lastIndexOf('>') + 2,
                                    routeInfoString.length() - 1);
                        }
                    } catch (NoSuchFieldError e) {
                    } catch (NoSuchMethodError e) {
                    } catch (IllegalAccessError e) {
                    } catch (Exception e) {
                    }
                    if (TextUtils.isEmpty(ip) || !isIpString(ip)) {
                        ip = androidGetProp("dhcp.eth0.ipaddress", "");
                    }
                    if (TextUtils.isEmpty(mask)) {
                        mask = androidGetProp("dhcp.eth0.mask", "");
                    }
                    if (TextUtils.isEmpty(gateway)) {
                        gateway = androidGetProp("dhcp.eth0.gateway", "");
                    }
                    if (TextUtils.isEmpty(dns1)) {
                        dns1 = androidGetProp("dhcp.eth0.dns1", "");
                    }
                    if (TextUtils.isEmpty(dns2)) {
                        dns2 = androidGetProp("dhcp.eth0.dns2", "");
                    }

                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    net_type = "mobile";
                    break;
                default:
                    net_type = "unknown";
                    break;
            }
        }

        results.put("net_type", net_type);
        results.put("ip", ip);
        results.put("mask", mask);
        results.put("gateway", gateway);
        results.put("dns1", dns1);
        results.put("dns2", dns2);
        results.put("ssid", ssid);
        results.put("bssid", bssid);
        return results;
    }

    private static String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;


        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }


        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
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

    public static boolean isIpString(String target) {
        String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        return isMatch(regex, target);
    }

    private static boolean isMatch(String regex, String target) {
        if (target == null || target.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(target);
        return matcher.matches();
    }

    public static String getGatewayMac(String gatewayIp) {
        if (TextUtils.isEmpty(gatewayIp)) {
            return "";
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";

            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    if (ip.contains(gatewayIp)) {
                        return mac;
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return "";
    }

    public static String androidGetProp(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
    //================================

    public static String getLocalIPAddress() {
        String ipAddress = null;
        try {
            for (Enumeration mEnumeration = NetworkInterface.getNetworkInterfaces(); mEnumeration
                    .hasMoreElements(); ) {
                NetworkInterface netInterface = (NetworkInterface) mEnumeration.nextElement();
                if (netInterface.getName().toLowerCase().equals("eth0")
                        || netInterface.getName().toLowerCase().equals("wlan0")) {
                    for (Enumeration enumIPAddr = netInterface.getInetAddresses(); enumIPAddr
                            .hasMoreElements(); ) {
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

    public static Map<String, String> readArp() {
        BufferedReader br = null;
        Map<String, String> devices = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String mac = "";
            devices = new HashMap<String, String>();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() < 63) {
                    continue;
                }
                if (line.toUpperCase(Locale.US).contains("IP")) {
                    continue;
                }
                if (line.contains("00:00:00:00:00:00")) {
                    continue;
                }

                ip = line.substring(0, 17).trim();
                mac = line.substring(41, 63).trim();
                devices.put(ip, mac);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return devices;
    }

}
