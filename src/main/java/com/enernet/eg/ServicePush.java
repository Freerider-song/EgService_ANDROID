package com.enernet.eg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.enernet.eg.activity.ActivityAlarm;
import com.enernet.eg.activity.ActivityLogin;
import com.enernet.eg.activity.ActivityPopUpLocked;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;

public class ServicePush extends FirebaseMessagingService implements IaResultHandler {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.e("Firebase", "FirebaseInstanceIDService : " + s);
    }



    @Override
    public void onMessageReceived(RemoteMessage rm) {
        Log.d("ServicePush", "onMessageReceived called...");

        if (rm==null || rm.getData().size()==0) {
            Log.i("ServicePush", "no data available....");
            return;
        }

       // String strFrom=rm.getFrom();
        Map<String, String> data=rm.getData();

        int nSeqMemberAckRequester=0;
        String strSeqMemberAckRequester=data.get("seq_member_ack_requester");
        if (strSeqMemberAckRequester!=null) nSeqMemberAckRequester=Integer.parseInt(strSeqMemberAckRequester);

        String strTitle=data.get("title");
        String strBody=data.get("body");
        String strPushType=data.get("push_type");
        String strImageURL = data.get("image");


        int nPushType=Integer.parseInt(strPushType);

        //Log.d("ServicePush", "from : " + strFrom);
        Log.d("ServicePush", "seq_member_ack_requester : " + nSeqMemberAckRequester);
        Log.d("ServicePush", "title : " + strTitle);
        Log.d("ServicePush", "body : " + strBody);
        Log.d("ServicePush", "push_type : " + nPushType);
        Log.d("ServicePush", "image : " + strImageURL);


        CaApplication.m_Engine.GetAlarmList(CaApplication.m_Info.m_nSeqMember, 20, this, this);

        switch (nPushType) {
            case CaEngine.ALARM_TYPE_REQUEST_ACK_MEMBER:
            {
                Log.d("ServicePush", "Request Ack Push received...");
                notifyRequestAckMember(strTitle, strBody, nSeqMemberAckRequester);
            }
            break;

            case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_ACCEPTED: {
                Log.d("ServicePush", "Ack Accepted Push received...");
                notifyResponseAckMemberAccepted(strTitle, strBody);
            }
            break;

            case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_REJECTED: {
                Log.d("ServicePush", "Ack Rejected Push received...");
                notifyResponseAckMemberRejected(strTitle, strBody);

            }
            break;

            case CaEngine.ALARM_TYPE_RESPONSE_ACK_MEMBER_CANCELED: {
                Log.d("ServicePush", "Ack Canceled Push received...");
                notifyResponseAckMemberCanceled(strTitle, strBody);

            }
            break;

            case CaEngine.ALARM_TYPE_NOTI_KWH: {
                Log.d("ServicePush", "AlarmKwh Push received...");
                notifyAlarmKwh(strTitle, strBody, strImageURL);
            }
            break;

            case CaEngine.ALARM_TYPE_NOTI_WON: {
                Log.d("ServicePush", "AlarmWon Push received...");
                notifyAlarmWon(strTitle, strBody, strImageURL);
            }
            break;

            case CaEngine.ALARM_TYPE_NOTI_PRICE_LEVEL: {
                Log.d("ServicePush", "AlarmPriceLevel Push received...");
                notifyAlarmPriceLevel(strTitle, strBody, strImageURL);
            }
            break;

            case CaEngine.ALARM_TYPE_NOTI_USAGE: {
                Log.d("ServicePush", "AlarmUsage Push received...");
                notifyAlarmUsage(strTitle, strBody, strImageURL);
            }
            break;

            case CaEngine.ALARM_TYPE_NOTI_TRANS: {
                Log.d("ServicePush", "AlarmTrans Push received...");
                notifyAlarmTrans(strTitle, strBody, strImageURL);
            }
            break;

            case 1200: {
                Log.d("ServicePush", "AlarmImage Push received...");
                notifyImage(strTitle, strBody, strImageURL);
            }
            break;

            default: {
                Log.i("ServicePush", "Unknown push type : " + nPushType);
            }
            break;
        }

    }

    public static Bitmap getImageFromURL(String imageURL){
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;

        try
        {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e){
            e.printStackTrace();
        } finally{
            if(bis != null) {
                try {bis.close();} catch (IOException e) {}
            }
            if(conn != null ) {
                conn.disconnect();
            }
        }

        return imgBitmap;
    }


    private void notifyImage(String strTitle, String strBody, String strImageUrl) {
        Log.d("ServicePush", "notifyAlarmImage called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();


        Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

        itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
        itPop.addCategory(Intent.CATEGORY_LAUNCHER);
        itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itPop.putExtra("content", strBody);
        itPop.putExtra("image", strImageUrl);

        //ctx.startActivity(itPop);

        PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pitPop.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }


        Intent it=new Intent(ctx, ActivityAlarm.class);
        it.setAction(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String strChannelId="1234";

        Bitmap myBitmap = getImageFromURL(strImageUrl);

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
            if (mChannel == null) {
                //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(custom_layout)
                            .setCustomBigContentView(expanded_layout)
                            .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                            .setContentText(strBody)
                            .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                            //.setCategory(NotificationCompat.CATEGORY_CALL)  계속 폰콜처럼 떠있음
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            /* bigpicture style
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap))

                             */
                            //set background color
                            .setColor(getResources().getColor(R.color.white))
                            .setColorized(true)
                            //.setFullScreenIntent(pitPop, true)

                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());

        }
        else {
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(custom_layout)
                            .setCustomBigContentView(expanded_layout)
                            .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                            .setContentText(strBody)
                            .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                            //.setCategory(NotificationCompat.CATEGORY_CALL)  계속 폰콜처럼 떠있음
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            /* bigpicture style
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap))

                             */
                            //set background color
                            .setColor(getResources().getColor(R.color.white))
                            .setColorized(true)
            //.setFullScreenIntent(pitPop, true)
            ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }


    }

    private void notifyRequestAckMember(String strTitle, String strBody, int nSeqMemberAckRequester) {
        Log.d("ServicePush", "notifyRequestAckMember called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

       // Intent it=new Intent(ctx, ActivityLogin.class);
        Intent it=new Intent(ctx, ActivityAlarm.class);
        it.setAction(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

        Intent itActionAccept=new Intent(ctx, ActionReceiver.class);
        itActionAccept.putExtra("NotiId", nNotiId);
        itActionAccept.putExtra("action", "accept");
        itActionAccept.putExtra("seq_member_ack_requester", nSeqMemberAckRequester);
        PendingIntent pitAccept=PendingIntent.getBroadcast(ctx, 1, itActionAccept, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent itActionReject=new Intent(ctx, ActionReceiver.class);
        itActionReject.putExtra("NotiId", nNotiId);
        itActionReject.putExtra("action", "reject");
        itActionReject.putExtra("seq_member_ack_requester", nSeqMemberAckRequester);
        PendingIntent pitReject=PendingIntent.getBroadcast(ctx, 2, itActionReject, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String strChannelId="1234";

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
               // mChannel.enableVibration(true);
               // mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .addAction(R.drawable.speaker_icon, "승인", pitAccept)
                            .addAction(R.drawable.speaker_icon, "거절", pitReject)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }
        else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .addAction(R.drawable.speaker_icon, "승인", pitAccept)
                            .addAction(R.drawable.speaker_icon, "거절", pitReject)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }


    }


    private void notifyResponseAckMemberAccepted(String strTitle, String strBody) {
        Log.d("ServicePush", "notifyResponseAckMemberAccepted called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        Intent it=new Intent(ctx, ActivityLogin.class);
        it.setAction(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String strChannelId="1234";

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }
        else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }


    }

    private void notifyResponseAckMemberRejected(String strTitle, String strBody) {
        Log.d("ServicePush", "notifyResponseAckMemberRejected called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        //Intent it=new Intent(ctx, ActivityLogin.class);
        Intent it=new Intent(ctx, ActivityAlarm.class);
        it.setAction(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String strChannelId="1234";

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }
        else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }


    }

    private void notifyResponseAckMemberCanceled(String strTitle, String strBody) {
        Log.d("ServicePush", "notifyResponseAckMemberCanceled called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        //Intent it=new Intent(ctx, ActivityLogin.class);
        Intent it=new Intent(ctx, ActivityAlarm.class);
        it.setAction(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String strChannelId="1234";

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

            NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
            if (mChannel == null) {
                mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }
        else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, strChannelId)
                            .setSmallIcon(R.drawable.push_icon)
                            .setContentTitle(strTitle)
                            .setContentText(strBody)
                            .setContentIntent(pit)
                            .setAutoCancel(true)
                            .setOngoing(false)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    ;

            notificationManager.notify(nNotiId, notificationBuilder.build());
        }


    }

    private void notifyAlarmKwh(String strTitle, String strBody, String strImageURL) {


        Log.d("ServicePush", "notifyAlarmKwh called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        if(strImageURL !=null){

            Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

            itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
            itPop.addCategory(Intent.CATEGORY_LAUNCHER);
            itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itPop.putExtra("content", strBody);
            itPop.putExtra("image", strImageURL);
            PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pitPop.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }


            Intent it=new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId="1234";

            Bitmap myBitmap = getImageFromURL(strImageURL);
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }



                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)
                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());

            }
            else {

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)

                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }

        }
        else {


            //Intent it=new Intent(ctx, ActivityLogin.class);
            Intent it = new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId = "1234";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }
        }


    }

    private void notifyAlarmWon(String strTitle, String strBody, String strImageURL) {
        Log.d("ServicePush", "notifyAlarmWon called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        if(strImageURL !=null){

            Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

            itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
            itPop.addCategory(Intent.CATEGORY_LAUNCHER);
            itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itPop.putExtra("content", strBody);
            itPop.putExtra("image", strImageURL);
            PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pitPop.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }


            Intent it=new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId="1234";

            Bitmap myBitmap = getImageFromURL(strImageURL);
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }



                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)
                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());

            }
            else {

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)

                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }

        }
        else {


            //Intent it=new Intent(ctx, ActivityLogin.class);
            Intent it = new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId = "1234";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }
        }


    }

    private void notifyAlarmPriceLevel(String strTitle, String strBody, String strImageURL) {
        Log.d("ServicePush", "notifyAlarmPriceLevel called...");

        final int nNotiId=3186;
        Context ctx=getApplicationContext();

        if(strImageURL !=null){

            Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

            itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
            itPop.addCategory(Intent.CATEGORY_LAUNCHER);
            itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itPop.putExtra("content", strBody);
            itPop.putExtra("image", strImageURL);
            PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pitPop.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }


            Intent it=new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId="1234";

            Bitmap myBitmap = getImageFromURL(strImageURL);
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }



                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)
                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());

            }
            else {

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)

                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }

        }
        else {


            //Intent it=new Intent(ctx, ActivityLogin.class);
            Intent it = new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId = "1234";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }
        }

    }

    private void notifyAlarmUsage(String strTitle, String strBody, String strImageURL) {
        Log.d("ServicePush", "notifyAlarmUsage called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        if(strImageURL !=null){

            Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

            itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
            itPop.addCategory(Intent.CATEGORY_LAUNCHER);
            itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itPop.putExtra("content", strBody);
            itPop.putExtra("image", strImageURL);
            PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pitPop.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }


            Intent it=new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId="1234";

            Bitmap myBitmap = getImageFromURL(strImageURL);
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }



                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)
                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());

            }
            else {

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)

                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }

        }
        else {


            //Intent it=new Intent(ctx, ActivityLogin.class);
            Intent it = new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId = "1234";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }
        }


    }


    private void notifyAlarmTrans(String strTitle, String strBody, String strImageURL) {
        Log.d("ServicePush", "notifyAlarmTrans called...");

        final int nNotiId=3186;

        Context ctx=getApplicationContext();

        if(strImageURL !=null){

            Intent itPop=new Intent(ctx, ActivityPopUpLocked.class);

            itPop.setAction(Intent.ACTION_NEW_OUTGOING_CALL);
            itPop.addCategory(Intent.CATEGORY_LAUNCHER);
            itPop.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            itPop.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itPop.putExtra("content", strBody);
            itPop.putExtra("image", strImageURL);
            PendingIntent pitPop = PendingIntent.getActivity(this,0,itPop,PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pitPop.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }


            Intent it=new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit=PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId="1234";

            Bitmap myBitmap = getImageFromURL(strImageURL);
            RemoteViews custom_layout = new RemoteViews(getPackageName(), R.layout.custom_notification);
            RemoteViews expanded_layout = new RemoteViews(getPackageName(), R.layout.custom_expanded);

            custom_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            custom_layout.setTextViewText(R.id.tv_push_title, strTitle);
            custom_layout.setTextViewText(R.id.tv_push_content, strBody);

            expanded_layout.setImageViewBitmap(R.id.iv_push, myBitmap);
            expanded_layout.setTextViewText(R.id.tv_push_title, strTitle);
            expanded_layout.setTextViewText(R.id.tv_push_content, strBody);

            if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    //IMPORTANCE_HIGH 여야 헤드업 알림 가능
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }



                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)
                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());

            }
            else {

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(custom_layout)
                                .setCustomBigContentView(expanded_layout)
                                .setContentTitle(strTitle) //헤드업 메시지 제목과 내용 표시
                                .setContentText(strBody)
                                .setContentIntent(pitPop) // 알림 클릭 시 이벤트
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .setColor(getResources().getColor(R.color.white))
                                .setColorized(true)

                        ;

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }

        }
        else {


            //Intent it=new Intent(ctx, ActivityLogin.class);
            Intent it = new Intent(ctx, ActivityAlarm.class);
            it.setAction(Intent.ACTION_MAIN);
            it.addCategory(Intent.CATEGORY_LAUNCHER);

            PendingIntent pit = PendingIntent.getActivity(this, 0, it, 0);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String strChannelId = "1234";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel mChannel = notificationManager.getNotificationChannel(strChannelId);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(strChannelId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            } else {
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, strChannelId)
                                .setSmallIcon(R.drawable.push_icon)
                                .setContentTitle(strTitle)
                                .setContentText(strBody)
                                .setContentIntent(pit)
                                .setAutoCancel(true)
                                .setOngoing(false)
                                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                notificationManager.notify(nNotiId, notificationBuilder.build());
            }
        }


    }


    @Override
    public void onResult(CaResult Result) {

        if (Result.object==null) {
            Toast.makeText(getApplicationContext(), StringUtil.getString(this, R.string.tv_label_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (Result.m_nCallback) {
            case CaEngine.CB_GET_ALARM_LIST: {
                Log.i("ServicePush", "Result of GetAlarmList received...");

                try {
                    JSONObject jo = Result.object;
                    JSONArray jaAlarm = jo.getJSONArray("alarm_list");
                    CaApplication.m_Info.setAlarmList(jaAlarm);
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


