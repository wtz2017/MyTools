package com.wtz.tools.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class BaseReceiver extends BroadcastReceiver {

    private Context context;

    public BaseReceiver(Context context) {
        this.context = context;
    }

    public void register(IntentFilter filter) {
        context.registerReceiver(this, filter);
    }

    public void unRegister() {
        ReceiverUtils.unRegisterReceiver(context, this);
    }

}
