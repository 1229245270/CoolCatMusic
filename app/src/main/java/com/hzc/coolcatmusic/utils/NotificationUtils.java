package com.hzc.coolcatmusic.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.LocusIdCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.ChatGPTFormEntity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.service.ReplyReceiver;
import com.hzc.coolcatmusic.ui.chatgpt.ChatFloating;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTActivity;
import com.hzc.coolcatmusic.ui.chatgpt.ChatGPTFormActivity;
import com.hzc.coolcatmusic.ui.main.HomeActivity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.goldze.mvvmhabit.utils.KLog;

public class NotificationUtils {


    public static final String NOTIFICATION_TYPE = "notificationType";
    public static final String NOTIFICATION_UP_SONG = "notificationUpSong";
    public static final String NOTIFICATION_PLAY_SONG = "notificationPlaySong";
    public static final String NOTIFICATION_NEXT_SONG = "notificationNextSong";
    private static final String CHANNEL_NEW_MESSAGES = "new_messages";
    private static final int REQUEST_CONTENT = 1;
    private static final int REQUEST_BUBBLE = 1;

    public static Notification musicNotification(String songName,String singer,Bitmap bitmap){
        RemoteViews remoteViews = new RemoteViews(AppApplication.getInstance().getPackageName(),R.layout.notification);
        Intent intentUp = new Intent(AppApplication.getInstance(), MusicService.class);
        intentUp.setAction("1");
        intentUp.putExtra(NOTIFICATION_TYPE,NOTIFICATION_UP_SONG);
        PendingIntent pendingIntentUp = PendingIntent.getService(AppApplication.getInstance(),1,intentUp,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_upsong,pendingIntentUp);

        Intent intentPlay = new Intent(AppApplication.getInstance(),MusicService.class);
        intentPlay.setAction("2");
        intentPlay.putExtra(NOTIFICATION_TYPE,NOTIFICATION_PLAY_SONG);
        PendingIntent pendingIntentPlay = PendingIntent.getService(AppApplication.getInstance(),2,intentPlay,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_playsong,pendingIntentPlay);

        Intent intentNext = new Intent(AppApplication.getInstance(),MusicService.class);
        intentNext.setAction("3");
        intentNext.putExtra(NOTIFICATION_TYPE,NOTIFICATION_NEXT_SONG);
        PendingIntent pendingIntentNext = PendingIntent.getService(AppApplication.getInstance(),3,intentNext,PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notification_nextsong,pendingIntentNext);

        if(MusicConnection.musicInterface != null){
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
        return new NotificationCompat.Builder(AppApplication.getInstance(),MUSIC_CHANNEL_ID)
                //最顶部的小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("contentTitle")
                .setContentText("contentText")
                .setContent(remoteViews)
                //禁止滑动删除
                .setOngoing(true)
                //.setPriority(NotificationCompat.PRIORITY_MAX)
                //声音
                //.setSound(Uri.EMPTY)
                .setColor(Color.RED)
                //统一消除声音和震动
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // 所有情况下显示，包括锁屏
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build();
    }

    private static int getPendingIntentFlags(boolean mutable){
        if(mutable){
            if(Build.VERSION.SDK_INT >= 31){
                return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE;
            }else{
                return PendingIntent.FLAG_UPDATE_CURRENT;
            }
        }else{
            return PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }
    }

    private static Uri getUri(ChatGPTFormEntity chatGPTFormEntity){
        return Uri.parse(AppApplication.getInstance().getString(R.string.chat_notification_host)
                + AppApplication.getInstance().getString(R.string.chat_notification_pathPattern)
                + chatGPTFormEntity.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static Notification chatNotification(ChatGPTFormEntity chatGPTFormEntity,boolean fromUser,boolean update){
        updateShortcuts(chatGPTFormEntity);

        IconCompat iconCompat = IconCompat.createWithResource(AppApplication.getInstance(),R.mipmap.ic_launcher);
        Intent target = new Intent(AppApplication.getInstance(), ChatGPTActivity.class);
        target.setAction(Intent.ACTION_VIEW);
        Uri uri = getUri(chatGPTFormEntity);
        target.setData(uri);
        //聊天窗行为
        final int REQUEST_CONTENT = 1;
        PendingIntent bubbleIntent = PendingIntent.getActivity(AppApplication.getInstance(), REQUEST_CONTENT, target, getPendingIntentFlags(true));
        Person chatPartner = new Person.Builder()
                .setName("Chat " + chatGPTFormEntity.getId())
                .setKey(String.valueOf(chatGPTFormEntity.getId()))
                .build();

        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(chatPartner);
        for(ChatGPTEntity chatGPTEntity : chatGPTFormEntity.getList()){
            Person person;
            if(chatGPTEntity.getRole().contains("user")){
                person = new Person.Builder()
                        .setName("user")
                        .build();
            }else{
                person = new Person.Builder()
                        .setName("chatGPT")
                        .build();
            }
            NotificationCompat.MessagingStyle.Message message =
                    new NotificationCompat.MessagingStyle.Message(chatGPTEntity.getContent(),chatGPTEntity.getCreateDate(),person);
            style.addMessage(message);
        }

        NotificationCompat.BubbleMetadata.Builder bubbleMetadataBuilder = new NotificationCompat.BubbleMetadata.Builder(bubbleIntent,iconCompat)
                .setDesiredHeight(AppApplication.getInstance().getResources().getDimensionPixelSize(me.goldze.mvvmhabit.R.dimen.dp_400));
        if(fromUser){
            bubbleMetadataBuilder.setAutoExpandBubble(true);
        }
        if(fromUser || update){
            bubbleMetadataBuilder.setSuppressNotification(true);
        }
        //通知的编辑栏
        NotificationCompat.Action.Builder actionBuild = new NotificationCompat.Action.Builder(
                IconCompat.createWithResource(AppApplication.getInstance(),R.mipmap.ic_launcher),
                "回复",
                PendingIntent.getBroadcast(
                        AppApplication.getInstance(),
                        REQUEST_CONTENT,
                        new Intent(AppApplication.getInstance(), ReplyReceiver.class).setData(uri),
                        getPendingIntentFlags(true)
                )
        )
                .addRemoteInput(
                new RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
                        .setLabel("label")
                        .build()
                )
                .setAllowGeneratedReplies(true);

        NotificationCompat.Builder compatBuild = new NotificationCompat.Builder(AppApplication.getInstance(), CHAT_CHANNEL_ID)
                .setBubbleMetadata(
                    bubbleMetadataBuilder.build()
                )
                .setContentTitle(chatPartner.getName())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setShortcutId(chatPartner.getKey())
                .setLocusId(new LocusIdCompat(chatPartner.getKey()))
                .addPerson(chatPartner)
                .setShowWhen(true)
                //点击通知事件
                .setContentIntent(
                        PendingIntent.getActivity(
                                AppApplication.getInstance(),
                                REQUEST_CONTENT,
                                new Intent(AppApplication.getInstance(), HomeActivity.class)
                                        .setAction(Intent.ACTION_VIEW)
                                        .setData(uri),
                                getPendingIntentFlags(false)
                        )
                )
                //.addAction(actionBuild.build())
                .setStyle(style);
                //.setWhen(chatPartner.getKey());
        if(update){
            compatBuild.setOnlyAlertOnce(true);
        }
        return compatBuild.build();
    }

    public static Notification downloadNotification(int max,int progress){
        return new NotificationCompat.Builder(AppApplication.getInstance(),DOWNLOAD_CHANNEL_ID)
                //设置通知标题
                .setContentTitle("正在下载...")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                //设置通知的大图标
                .setLargeIcon(BitmapFactory.decodeResource(AppApplication.getInstance().getResources(), R.mipmap.ic_launcher_round))
                //设置通知的提醒方式： 呼吸灯
                .setDefaults(Notification.DEFAULT_LIGHTS)
                //设置通知的优先级：最大
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //设置通知被点击一次是否自动取消
                .setAutoCancel(false)
                .setContentText("下载进度:" + (int)(progress * 1.0 / max * 100) + "%")
                .setProgress(max, progress, false)
                .build();
    }

    /**
     * 气泡通知
     * @param chatGPTFormEntity
     */
    public static void updateShortcuts(ChatGPTFormEntity chatGPTFormEntity){
        IconCompat iconCompat;
        try{
            InputStream inputStream = AppApplication.getInstance().getResources().getAssets().open("");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            iconCompat = IconCompat.createWithAdaptiveBitmap(bitmap);
        }catch (Exception e){

        }
        for(int i = 0;i < chatGPTFormEntity.getList().size();i++){
            Set<String> set = new HashSet<>();
            set.add("com.example.android.bubbles.category.TEXT_SHARE_TARGET");
            ShortcutInfoCompat shortcutInfoCompat = new ShortcutInfoCompat.Builder(AppApplication.getInstance(),String.valueOf(chatGPTFormEntity.getId()))
                    .setLocusId(new LocusIdCompat(String.valueOf(chatGPTFormEntity.getId())))
                    .setActivity(new ComponentName(AppApplication.getInstance(),HomeActivity.class))
                    .setShortLabel("name")
                    .setIcon(IconCompat.createWithResource(AppApplication.getInstance(),R.mipmap.ic_launcher))
                    .setLongLived(true)
                    .setCategories(set)
                    .setIntent(new Intent(AppApplication.getInstance(), HomeActivity.class)
                            .setAction(Intent.ACTION_VIEW)
                            .setData(getUri(chatGPTFormEntity))
                    )
                    .setPerson(
                            new Person.Builder()
                                    .setName("name")
                                    .setIcon(IconCompat.createWithResource(AppApplication.getInstance(),R.drawable.ceshi))
                                    .build()
                    )
                    .build();
            //compats.add(shortcutInfoCompat);
            ShortcutManagerCompat.pushDynamicShortcut(AppApplication.getInstance(),shortcutInfoCompat);
        }
    }


    /**
     * 安卓 8.0 系统要求必须创建渠道才能展示通知
     * IMPORTANCE_HIGH 收到通知发出提示语，并且会浮动提示用户(小米手机表示紧急)
     * IMPORTANCE_DEFAULT 收到通知发出提示语，不会浮动提示（小米手机表示高）
     * IMPORTANCE_LOW 收到通知不会发出声音，状态栏有小图标展示（小米手机表示中）
     * IMPORTANCE_MIN 根本看不到通知（所以你压根就别用就 ok 了），不过似乎可以用于禁用通知的场景（小米手机表示低）
     * @param manager
     * @param channelId
     * @param channelName
     */
    public static void initCreateChannel(NotificationManager manager,String channelId,String channelName){
        //取消自动弹出
        NotificationChannel channel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_MIN);
        channel.setSound(null,null);
        //通知灯
        channel.enableLights(true);
        //通知灯颜色
        channel.setLightColor(Color.BLUE);
        //角标
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        channel.setDescription("description");
        manager.createNotificationChannel(channel);
    }

    public static final String MUSIC_CHANNEL_ID = "1";
    public static final String MUSIC_CHANNEL_NAME = "音乐条通知";

    /**
     * 创建音乐条通知渠道
     */
    public static void initMusicNotificationChannel(){
        //取消自动弹出
        NotificationChannel channel = new NotificationChannel(MUSIC_CHANNEL_ID,MUSIC_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
        channel.setSound(null,null);
        //通知灯
        channel.enableLights(true);
        //通知灯颜色
        channel.setLightColor(Color.BLUE);
        //角标
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        //channel.setDescription("description");
        AppApplication.notificationManager.createNotificationChannel(channel);
    }

    public static final String CHAT_CHANNEL_ID = "2";
    public static final String CHAT_CHANNEL_NAME = "聊天通知";
    /**
     * 创建聊天通知渠道
     */
    private static void initChatNotificationChannel(){
        //取消自动弹出
        NotificationChannel channel = new NotificationChannel(CHAT_CHANNEL_ID,CHAT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null,null);
        //通知灯
        channel.enableLights(true);
        //通知灯颜色
        channel.setLightColor(Color.BLUE);
        //角标
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        //channel.setDescription("description");
        AppApplication.notificationManager.createNotificationChannel(channel);
    }

    public static final String DOWNLOAD_CHANNEL_ID = "3";
    public static final String DOWNLOAD_CHANNEL_NAME = "下载通知";
    /**
     * 创建下载通知渠道
     */
    private static void initDownloadNotificationChannel(){
        //取消自动弹出
        NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_ID,DOWNLOAD_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null,null);
        //通知灯
        channel.enableLights(true);
        //通知灯颜色
        channel.setLightColor(Color.BLUE);
        //角标
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        //channel.setDescription("description");
        AppApplication.notificationManager.createNotificationChannel(channel);
    }

    /**
     * 发送音乐条通知
     * @param songName 歌手
     * @param singer 歌名
     * @param bitmap 图片
     */
    public static void sendMusicNotification(String songName,String singer,Bitmap bitmap){
        initMusicNotificationChannel();
        AppApplication.notificationManager.notify(Integer.parseInt(MUSIC_CHANNEL_ID),musicNotification(songName,singer,bitmap));
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void sendChatNotification(ChatGPTFormEntity chatGPTFormEntity, boolean fromUser, boolean update){
        initChatNotificationChannel();
        AppApplication.notificationManager.notify(Integer.parseInt(CHAT_CHANNEL_ID),chatNotification(chatGPTFormEntity, fromUser, update));
    }

    public static void sendDownloadNotification(int max,int progress){
        initDownloadNotificationChannel();
        AppApplication.notificationManager.notify(Integer.parseInt(DOWNLOAD_CHANNEL_ID),downloadNotification(max, progress));
    }

    public static boolean requestNotification(Context context){
        boolean isEnabled = AppApplication.notificationManager.areNotificationsEnabled();
        if(!isEnabled){
            DialogUtils.openNotificationSettingDialog(context);
        }
        return isEnabled;
    }
}
