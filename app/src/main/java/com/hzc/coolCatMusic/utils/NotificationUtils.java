package com.hzc.coolCatMusic.utils;

import static com.hzc.coolCatMusic.service.MusicConnection.musicInterface;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaSession2;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;

import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.main.HomeActivity;

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

        Intent intentUp = new Intent(context,MusicService.class);
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

        if(musicInterface != null){
            KLog.d("musicInterface.isPlaying():" + musicInterface.isPlaying());
            if(musicInterface.isPlaying()){
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
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(Uri.EMPTY)//声音
                .setColor(Color.RED)
                .setDefaults(NotificationCompat.DEFAULT_ALL)//统一消除声音和震动
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)// 所有情况下显示，包括锁屏
                .build();
        return notification;
    }

    public static void initCreateChannel(NotificationManager manager,String channelId,String channelName){
        NotificationChannel channel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setSound(null,null);
        channel.enableLights(true);//通知灯
        channel.setLightColor(Color.BLUE);//通知灯颜色
        channel.setShowBadge(true);//角标
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);//所有情况下显示，包括锁屏
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
