package com.enernet.eg;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.enernet.eg.activity.ActivityLogin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ServiceMonitor extends Service implements IaResultHandler{
    private Thread m_Thread;
    public static Intent s_Intent=null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        s_Intent=intent;
//        showToast(getApplication(), "ServiceMonitor.onStartCommand");

        m_Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean bRun = true;
                while (bRun) {
                    try {
                        Thread.sleep(1000 * 60);

                        getMinuteSinceLastLp();
                    }
                    catch (InterruptedException e) {
                        bRun = false;
      //                  showToast(getApplication(), "ServiceMonitor.m_Thread exception...");
                        e.printStackTrace();
                    }
                }
            }
        });

        m_Thread.start();

    //    showToast(getApplication(), "After Thread Start called");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        s_Intent=null;
        setAlarmTimer();
        Thread.currentThread().interrupt();

        if (m_Thread!=null) {
            m_Thread.interrupt();
            m_Thread=null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    public void showToast(final Application application, final String msg) {
        Handler h = new Handler(application.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(application, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setAlarmTimer() {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.SECOND, 1);
        Intent intent = new Intent(this, ReceiverAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
    }

    public void getMinuteSinceLastLp() {
        CaEngine Engine=new CaEngine();
        Engine.GetMinuteSinceLastLp(this, this);
    }

    private void notifyEgServerState(String messageBody) {

        Intent intent = new Intent(this, ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.push_icon)
                        .setContentTitle("EG 서비스")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            notifyEgServerState("서버접속에 실패했습니다");
            //Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_MINUTE_SINCE_LAST_LP: {
                Log.i("ServicePush", "Result of GetMinuteSinceLastLp received...");

                try {
                    JSONObject jo = Result.object;
                    int nMin=jo.getInt("min_since_last_lp");

                    if (nMin>31) {
                        notifyEgServerState("LP 수집 이후 " + nMin + " 분을 초과했습니다");
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            default: {
                Log.i("ServicePush", "Unknown type result received : " + Result.m_nCallback);
            }
            break;

        } // end of switch
    }



}
