package com.hzc.coolcatmusic.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;

import me.goldze.mvvmhabit.utils.KLog;

public class NotificationUtils {


    public static final String NOTIFICATION_TYPE = "notificationType";
    public static final String NOTIFICATION_UP_SONG = "notificationUpSong";
    public static final String NOTIFICATION_PLAY_SONG = "notificationPlaySong";
    public static final String NOTIFICATION_NEXT_SONG = "notificationNextSong";

    public static Notification musicNotification(Context context,String songName,String singer,Bitmap bitmap){
        String channelId = "0";
        String channelName = "name";
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initCreateChannel(manager,channelId,channelName);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.notification);

        Intent intentUp = new Intent(context, MusicService.class);
        intentUp.setAction("1");
        intentUp.putExtra(NOTIFICATION_TYPE,NOTIFICATION_UP_SONG);
        PendingIntent pendingIntentUp = PendingIntent.getService(context.getApplicationContext(),1,intentUp,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_upsong,pendingIntentUp);

        Intent intentPlay = new Intent(context,MusicService.class);
        intentPlay.setAction("2");
        intentPlay.putExtra(NOTIFICATION_TYPE,NOTIFICATION_PLAY_SONG);
        PendingIntent pendingIntentPlay = PendingIntent.getService(context.getApplicationContext(),2,intentPlay,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_playsong,pendingIntentPlay);

        Intent intentNext = new Intent(context,MusicService.class);
        intentNext.setAction("3");
        intentNext.putExtra(NOTIFICATION_TYPE,NOTIFICATION_NEXT_SONG);
        PendingIntent pendingIntentNext = PendingIntent.getService(context.getApplicationContext(),3,intentNext,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_nextsong,pendingIntentNext);

        if(MusicConnection.musicInterface != null){
            KLog.d("musicInterface.isPlaying():" + MusicConnection.musicInterface.isPlaying());
            if(MusicConnection.musicInterface.isPlaying()){
                remoteViews.setImageViewResource(R.id.notification_playsong,R.drawable.home_music_stop_uncheck);
            }else{
                remoteViews.setImageViewResource(R.id.notification_playsong,R.drawable.home_music_play_uncheck);
            }
        }
        if(songName != null){
            remoteViews.setTextViewText(R.id.notification_songName,songName);
        }
        if(singer != null){
            remoteViews.setTextViewText(R.id.notification_singer,singer);
        }
        if(bitmap != null){
            remoteViews.setImageViewBitmap(R.id.notification_image,bitmap);
        }
        Notification notification = new NotificationCompat.Builder(AppApplication.getInstance(),"0")
                .setSmallIcon(R.mipmap.ic_launcher)//最顶部的小图标
                .setContentTitle("emailObject.getSenderName()")
                .setContentText("emailObject.getSubject()")
                .setContent(remoteViews)
                .setOngoing(true)
                //.setPriority(NotificationCompat.PRIORITY_MAX)
                //.setSound(Uri.EMPTY)//声音
                .setColor(Color.RED)
                .setDefaults(NotificationCompat.DEFAULT_ALL)//统一消除声音和震动
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)// 所有情况下显示，包括锁屏
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
        return notification;
    }

    public static void initCreateChannel(NotificationManager manager,String channelId,String channelName){
        //取消自动弹出
        NotificationChannel channel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_MIN);
        channel.setSound(null,null);
        channel.enableLights(true);//通知灯
        channel.setLightColor(Color.BLUE);//通知灯颜色
        channel.setShowBadge(true);//角标
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setDescription("description");
        manager.createNotificationChannel(channel);
    }

    public static void sendMusicNotification(Context context,String songName,String singer,Bitmap bitmap){
        KLog.d("createForeNotification");
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initCreateChannel(manager,"0","name");
        manager.notify(1,musicNotification(context,songName,singer,bitmap));


    }
}
