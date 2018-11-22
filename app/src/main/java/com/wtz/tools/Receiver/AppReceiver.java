package com.wtz.tools.Receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class AppReceiver extends BaseReceiver {

    private final String TAG = AppReceiver.class.getSimpleName();

    public AppReceiver(Context context) {
        super(context);
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        super.register(filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive:" + action);

        int type = -1;
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            type = 0;
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            type = 1;
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            type = 2;
        }

        if (type != -1) {
            try {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.d(TAG, "onReceive app event: " + packageName + "," + type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
