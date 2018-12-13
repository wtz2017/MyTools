package com.wtz.tools.utils.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.util.Log;

public class FtpManager {
    private final String TAG = FtpManager.class.getName();

    private FTPClient mCurrentFtp;

    private String mHost;
    private int mPort;
    private String mUserName;
    private String mPassWord;

    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int DATA_TIMEOUT_MIN = 5 * 60 * 1000;// mili-second
    private static final int DATA_SPEED = 60 * 1024;// Byte/Second
    private static final String FTP_PATH_SEPERATOR = "/";

    public FtpManager(String host, int port, String userName, String passWord) {
        mHost = host;
        mPort = port;
        mUserName = userName;
        mPassWord = passWord;
    }

    /**
     * @param fileSize
     *            byteSize
     * @return
     */
    private int getDataTimeout(long fileSize) {
        int timeout = (int) (fileSize / DATA_SPEED * 1000);
        if (timeout < DATA_TIMEOUT_MIN) {
            timeout = DATA_TIMEOUT_MIN;
        }

        return timeout;
    }

    public boolean upload(Context context, File file, String targetPath, int maxRetryNum) {
        for (int i = 0; i < maxRetryNum; i++) {
            Log.d(TAG, "uploadByFtp...for i = " + i + ", target file: " + file);
            if (!NetworkDeviceUtils.isNetworkConnect(context)) {
                String log = "uploadByFtp...net is not connected! for i = " + i + ", target file: "
                        + file;
                Log.d(TAG, log);
                return false;
            }

            boolean success = upload(mHost, mPort, targetPath, mUserName, mPassWord, file);
            if (success) {
                return true;
            }
        }

        String log = "uploadByFtp...failed after try times = " + maxRetryNum;
        Log.d(TAG, log);
        return false;
    }

    /**
     * 通过ftp上传文件
     * 
     * @param host
     *            地址
     * @param port
     *            端口号
     * @param path
     *            上传到ftp服务器哪个路径下
     * @param username
     *            用户名
     * @param password
     *            密码
     * @param file
     *            目标文件
     * @return 成功与否
     * 
     */
    private boolean upload(String host, int port, String path, String username, String password,
            File file) {
        mCurrentFtp = new FTPClient();

        try {
            mCurrentFtp.setConnectTimeout(CONNECT_TIMEOUT);
            int dataTimeout = getDataTimeout(file.length());
            Log.d(TAG, "to set dataTimeout = " + dataTimeout);
            mCurrentFtp.setDataTimeout(dataTimeout);

            Log.d(TAG, "to connect " + host + ":" + port);
            mCurrentFtp.connect(host, port);
            Log.d(TAG, mCurrentFtp.getReplyString());

            if (!mCurrentFtp.login(username, password)) {
                String log = "ftp login failed";
                Log.d(TAG, log);
                return false;
            }

            if (!mCurrentFtp.setFileType(FTPClient.BINARY_FILE_TYPE)) {
                String log = "ftp setFileType failed";
                Log.d(TAG, log);
                return false;
            }

            int reply = mCurrentFtp.getReplyCode();
            Log.d(TAG, "getReplyCode...reply = " + reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                String log = "FTP server refused connection.";
                Log.d(TAG, log);
                return false;
            }

            if (!mCurrentFtp.changeWorkingDirectory(path)) {
                String log = "ftp changeWorkingDirectory failed: " + path;
                Log.d(TAG, log);

                String dirName = new String(path.getBytes(), "iso-8859-1");
                Log.d(TAG, "to make dir: " + dirName);
                if (!makeMutiLevelDirectory(mCurrentFtp, dirName)) {
                    Log.d(TAG, "ftp makeDirectory failed: " + dirName);
                    return false;
                }
                if (!mCurrentFtp.changeWorkingDirectory(path)) {
                    Log.d(TAG, "ftp changeWorkingDirectory secondly failed: " + path);
                    return false;
                }
            }

            // Use passive mode to pass firewalls.
            mCurrentFtp.enterLocalPassiveMode();

            if (!upload(mCurrentFtp, file)) {
                Log.d(TAG, "upload failed");
                return false;
            }

            mCurrentFtp.logout();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            disconnectFtp();
        }

        return true;
    }

    private boolean makeMutiLevelDirectory(FTPClient ftp, String path) {
        // 创建多层目录
        StringTokenizer tokens = new StringTokenizer(path, FTP_PATH_SEPERATOR);
        tokens.countTokens();
        String pathName = "";
        while (tokens.hasMoreElements()) {
            pathName = pathName + FTP_PATH_SEPERATOR + (String) tokens.nextElement();
            try {
                ftp.mkd(pathName);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @param file
     *            上传的文件或文件夹
     * @throws Exception
     */
    private boolean upload(FTPClient ftp, File file) {
        String targetCharset = "iso-8859-1";
        FileInputStream input = null;

        try {
            if (file.isDirectory()) {
                Log.d(TAG, "to store directory " + file.getName());
                String dirName = new String(file.getName().getBytes(), targetCharset);
                if (!ftp.changeWorkingDirectory(dirName)) {
                    if (!ftp.makeDirectory(dirName)) {
                        String log = "ftp makeDirectory failed: " + dirName;
                        Log.d(TAG, log);
                        return false;
                    }
                    if (!ftp.changeWorkingDirectory(dirName)) {
                        String log = "ftp changeWorkingDirectory failed: " + dirName;
                        Log.d(TAG, log);
                        return false;
                    }
                }
                String[] childFiles = file.list();
                for (int i = 0; i < childFiles.length; i++) {
                    File file1 = new File(file.getPath() + File.separator + childFiles[i]);
                    if (file1.isDirectory()) {
                        if (!upload(ftp, file1)) {
                            return false;
                        }
                        if (!ftp.changeToParentDirectory()) {
                            String log = "ftp changeToParentDirectory failed";
                            Log.d(TAG, log);
                            return false;
                        }
                    } else {
                        File file2 = new File(file.getPath() + File.separator + childFiles[i]);
                        input = new FileInputStream(file2);
                        String storeName = new String(file2.getName().getBytes(), targetCharset);
                        if (!ftp.storeFile(storeName, input)) {
                            String log = "ftp storeFile failed: " + storeName;
                            Log.d(TAG, log);
                            return false;
                        }
                    }
                }
            } else {
                Log.d(TAG, "to store file " + file.getName());
                input = new FileInputStream(file);
                String storeName = new String(file.getName().getBytes(), targetCharset);
                if (!ftp.storeFile(storeName, input)) {
                    String log = "ftp storeFile failed: " + storeName;
                    Log.d(TAG, log);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "upload success! ");
        return true;
    }

    public void disconnectFtp() {
        Log.d(TAG, "to disconnectFtp");
        if (mCurrentFtp != null && mCurrentFtp.isConnected()) {
            try {
                mCurrentFtp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
