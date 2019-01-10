package com.wtz.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;

public class ReceiverUtils {

    public static void unRegisterReceiver(Context context, BroadcastReceiver receiver) {
        if (context != null && receiver != null) {
            try {
                // 加异常捕获是防止注销与注册不对称造成异常
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
            } catch (Exception e) {
            }
        }
    }

}
