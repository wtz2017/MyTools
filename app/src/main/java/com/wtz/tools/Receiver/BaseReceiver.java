package com.wtz.tools.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

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
