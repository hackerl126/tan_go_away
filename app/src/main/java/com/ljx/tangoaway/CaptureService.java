package com.ljx.tangoaway;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;


public class CaptureService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String CHANNEL_ONE_ID = "com.ljx.tangoaway";
        String CHANNEL_ONE_NAME = "Channel One";
        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        Intent nfIntent = new Intent(this, MainActivity.class);
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()); //获取一个Notification构造器
        builder.setContentIntent(PendingIntent.getActivity(this,0,nfIntent,PendingIntent.FLAG_IMMUTABLE))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setContentTitle("TanGoAway前台服务")
                .setContentText("保证TanGoAway正常运行")
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ONE_ID);
        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        startForeground(110,notification);
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void  onDestroy(){
        super.onDestroy();
    }
}

