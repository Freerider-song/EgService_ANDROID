package com.enernet.eg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReceiverReboot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, ServiceRestarter.class);
            context.startForegroundService(in);
        }
        else {
            Intent in = new Intent(context, ServiceRestarter.class);
            context.startService(in);
        }
    }
}
