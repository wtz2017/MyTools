package com.wtz.tools.test.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wtz.tools.R;
import com.wtz.tools.utils.DateTimeUtil;
import com.wtz.tools.utils.SystemInfoUtils;
import com.wtz.tools.utils.network.NetworkDeviceUtils;
import com.wtz.tools.view.ToastUtils;

import java.util.List;
import java.util.Map;

public class DrawerLayoutFragment extends Fragment {

    private static final String TAG = DrawerLayoutFragment.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private TextView mTitleView;
    private TextView mContentView;

    private StringBuffer mBuffer = new StringBuffer();

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_drawerlayout, container, false);

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawerLayout);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mNavigationView);
            }
        });

        mTitleView = view.findViewById(R.id.tv_title);
        mContentView = view.findViewById(R.id.tv_content);

        mNavigationView = (NavigationView) view.findViewById(R.id.navigationView);
        mNavigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showTopToast(getActivity(), "head is clicked!");
            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.basic_info:
                        showBasicInfo();
                        break;
                    case R.id.version:
                        showVersionInfo();
                        break;
                    case R.id.chip:
                        showChipInfo();
                        break;
                    case R.id.storage:
                        showStorageInfo();
                        break;
                    case R.id.net:
                        showNetworkInfo();
                        break;
                    case R.id.download:
                        ToastUtils.showTopToast(getActivity(), "download is clicked!");
                        break;
                    case R.id.bike:
                        ToastUtils.showTopToast(getActivity(), "bike is clicked!");
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mNavigationView.setCheckedItem(R.id.basic_info);
        showBasicInfo();

        return view;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "onDetach");
        super.onDetach();
    }

    private void showBasicInfo() {
        mBuffer.setLength(0);
        readBasicInfo(mBuffer);
        showInfo("基本信息", mBuffer.toString());
    }

    private void showVersionInfo() {
        mBuffer.setLength(0);
        readVersions(mBuffer);
        showInfo("版本信息", mBuffer.toString());
    }

    private void showChipInfo() {
        mBuffer.setLength(0);
        readChipInfo(mBuffer);
        showInfo("芯片信息", mBuffer.toString());
    }

    private void showNetworkInfo() {
        mBuffer.setLength(0);
        readNetwork(mBuffer, getActivity());
        showInfo("网络信息", mBuffer.toString());
    }

    private void showStorageInfo() {
        mBuffer.setLength(0);
        readStorage(mBuffer);
        showInfo("内存信息", mBuffer.toString());
    }

    private void showInfo(String title, String content) {
        mTitleView.setText(title);
        mContentView.setText(content);
    }

    private void readNetwork(StringBuffer buffer, Context context) {
        buffer.append("MAC(有线):");
        buffer.append(NetworkDeviceUtils.getEth0Mac());
        buffer.append("\r\n");
        buffer.append("MAC(无线):");
        buffer.append(NetworkDeviceUtils.getWlan0Mac(context));
        buffer.append("\r\n");

        Map<String, String> networkInfos = NetworkDeviceUtils.getNetworkInfo(getActivity());

        buffer.append("Net Type:");
        buffer.append(networkInfos.get("net_type"));
        buffer.append("\r\n");

        buffer.append("IP:");
        buffer.append(networkInfos.get("ip"));
        buffer.append("\r\n");

        buffer.append("Mask:");
        buffer.append(networkInfos.get("mask"));
        buffer.append("\r\n");

        buffer.append("Gateway:");
        buffer.append(networkInfos.get("gateway"));
        buffer.append("\r\n");

        buffer.append("DNS1:");
        buffer.append(networkInfos.get("dns1"));
        buffer.append("\r\n");

        buffer.append("DNS2:");
        buffer.append(networkInfos.get("dns2"));
        buffer.append("\r\n");

        if (!TextUtils.isEmpty(networkInfos.get("ssid"))) {
            buffer.append("SSID:");
            buffer.append(networkInfos.get("ssid"));
            buffer.append("\r\n");

            buffer.append("BSSID:");
            buffer.append(networkInfos.get("bssid"));
            buffer.append("\r\n");
        }

        List<ScanResult> bssidList = NetworkDeviceUtils.getWiFiNearbyResults(getActivity());
        if (bssidList != null && bssidList.size() > 0) {
            buffer.append("around ap:");
            buffer.append("\r\n");
            for (ScanResult scanResult : bssidList) {
                buffer.append(scanResult.SSID);
                buffer.append("_");
                buffer.append(scanResult.BSSID);
                buffer.append("/");
            }
            buffer.append("\r\n");
        }
    }

    private void readVersions(StringBuffer buffer) {
        buffer.append("ROM版本增量:");
        buffer.append(Build.VERSION.INCREMENTAL);
        buffer.append("\r\n");

        buffer.append("ROM描述:");
        buffer.append(SystemInfoUtils.getAndroidProp("ro.build.description", ""));
        buffer.append("\r\n");

        buffer.append("Android系统Platform:");
        buffer.append(Build.VERSION.RELEASE);
        buffer.append("\r\n");

        buffer.append("Android系统SDK API Level:");
        buffer.append(Build.VERSION.SDK_INT);
        buffer.append("\r\n");
    }

    private void readBasicInfo(StringBuffer buffer) {
        buffer.append("机型:");
        buffer.append(Build.MODEL);
        buffer.append("（此为参考，具体以厂商接口为准）\r\n");

        buffer.append("制造商:");
        buffer.append(Build.MANUFACTURER);
        buffer.append("\r\n");

        buffer.append("品牌:");
        buffer.append(Build.BRAND);
        buffer.append("\r\n");

        buffer.append("产品:");
        buffer.append(Build.PRODUCT);
        buffer.append("\r\n");

        buffer.append("硬件:");
        buffer.append(Build.HARDWARE);
        buffer.append("\r\n");

        buffer.append("芯片类型:");
        buffer.append(SystemInfoUtils.getAndroidProp("persist.sys.chiptype", ""));
        buffer.append("\r\n");

        buffer.append("AndroidID:");
        buffer.append(SystemInfoUtils.getAndroidID(getActivity()));
        buffer.append("\r\n");

        int[] screenPixels = SystemInfoUtils.getScreenPixels(getActivity());
        buffer.append("分辨率:");
        buffer.append(screenPixels[0] + "x" + screenPixels[1]);
        buffer.append("\r\n");

        long bootPassTime = SystemClock.elapsedRealtime();
        buffer.append("已开机时间:");
        buffer.append(DateTimeUtil.changeRemainTimeToHms(bootPassTime));
        buffer.append("\r\n");
    }

    private void readChipInfo(StringBuffer buffer) {
        buffer.append("NumberOfCPUCores:");
        buffer.append(SystemInfoUtils.getNumberOfCPUCores());
        buffer.append("\r\n");

        buffer.append("MaxCpuFreq:");
        buffer.append(SystemInfoUtils.getMaxCpuFreq());
        buffer.append("\r\n");

        buffer.append("MinCpuFreq:");
        buffer.append(SystemInfoUtils.getMinCpuFreq());
        buffer.append("\r\n");

        buffer.append("CPU_ABI:");
        buffer.append(SystemInfoUtils.getCPU_ABI("/"));
        buffer.append("\r\n");

        buffer.append("CpuInfo:");
        buffer.append(SystemInfoUtils.getCpuInfo("#"));
        buffer.append("\r\n");
    }

    private void readStorage(StringBuffer buffer) {
        String[] ramList = SystemInfoUtils.readRAMFromFile();
        buffer.append("内存(RAM)：\r\n");
        buffer.append("◆MemTotal:\r\n");
        buffer.append("Read from file:");
        buffer.append(ramList[0]);
        buffer.append("\r\n");
        buffer.append("Get by AMS:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getTotalMemoryBytes(getActivity())));
        buffer.append("\r\n");

        buffer.append("◆MemFree:\r\n");
        buffer.append("Read from file:");
        buffer.append(ramList[1]);
        buffer.append("\r\n");

        buffer.append("◆MemAvailable:\r\n");
        buffer.append("Read from file:");
        buffer.append(ramList[2]);
        buffer.append("\r\n");
        buffer.append("Get by AMS:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getAvailMemoryBytes(getActivity())));
        buffer.append("\r\n");

        buffer.append("\r\n");
        buffer.append("存储(ROM)：\r\n");
        buffer.append("◆内部存储总大小:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getTotalInternalStorageSize(getActivity())));
        buffer.append("\r\n");

        buffer.append("◆内部存储可用大小:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getAvailableInternalStorageSize(getActivity())));
        buffer.append("\r\n");

        buffer.append("◆外部存储总大小:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getTotalExternalStorageSize(getActivity())));
        buffer.append("\r\n");

        buffer.append("◆外部存储可用大小:");
        buffer.append(Formatter.formatFileSize(getActivity(), SystemInfoUtils.getAvailableExternalStorageSize(getActivity())));
        buffer.append("bytes");
        buffer.append("\r\n");
    }

}
