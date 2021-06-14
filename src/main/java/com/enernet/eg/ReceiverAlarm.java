package com.enernet.eg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ReceiverAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service", "Alarm received");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, ServiceRestarter.class);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, ServiceMonitor.class);
            context.startService(in);
        }
    }
}
