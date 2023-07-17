package com.hzc.coolcatmusic.app;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.entity.DaoMaster;
import com.hzc.coolcatmusic.entity.DaoSession;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.service.MusicService;
import com.hzc.coolcatmusic.ui.main.HomeActivity;
import com.hzc.coolcatmusic.utils.NotificationUtils;
import com.liulishuo.okdownload.core.Util;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import skin.support.SkinCompatManager;
import skin.support.app.SkinAppCompatViewInflater;
import skin.support.app.SkinCardViewInflater;
import skin.support.constraint.app.SkinConstraintViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * Created by goldze on 2017/7/16.
 */
public class AppApplication extends BaseApplication {

    public static DaoSession daoSession;
    /**
     * 判断是否拖动进度条
     */
    public static boolean isUserPressThumb = false;

    /**
     * 判断播放是否结束
     */
    public static Boolean isPlayComplete = true;

    /**
     * 上一次播放动作
     */
    public static Boolean isOldPlaying = false;

    /**
     * sd地址
     */
    public static String PATH_SD_DIR;

    /**
     * 缓存图片地址
     */
    public static String PATH_CACHE_SONG_IMAGE;

    /**
     * 缓存歌曲地址
     */
    public static String PATH_CACHE_SONG;

    public static NotificationManager notificationManager;

    /**
     * 记录chatGPT是否可聊天
     */
    public static Map<Long,Boolean> chatGPTRead;

    public static boolean hasNetWork = false;

    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(GlobalData.isDebug);
        initPushMessage();
        initData();
        initCrash();
        initDataBase();
        initService();
        initPlayer();
        initFont();
        initSkin();
    }

    private void initData(){
        PATH_CACHE_SONG_IMAGE = getCacheDir().getPath() + "/songImage/";
        PATH_CACHE_SONG = getCacheDir().getPath() + "/song/";
        PATH_SD_DIR = findSDCardRoot(this.getExternalFilesDir(null)).getPath();
        chatGPTRead = new HashMap<>();
        //Util.enableConsoleLog();
        //Util.setLogger();
    }

    private void initPushMessage(){
        //JPushInterface.setDebugMode(true);
        //JPushInterface.init(this);
    }

    private File findSDCardRoot(File externalFilesDir) {
        File parent;
        boolean equals = "0".equals((Objects.requireNonNull(parent = externalFilesDir.getParentFile())).getName());
        if (!equals) {
            return findSDCardRoot(parent);
        } else {
            return parent;
        }
    }

    private void initFont(){
        try {
            if(SPUtils.getInstance().getLong(SPUtilsConfig.THEME_TEXT_FONT_ID) == -2L){
                return;
            }
            Typeface typeface = Typeface.createFromFile(SPUtils.getInstance().getString(SPUtilsConfig.THEME_TEXT_FONT_PATH));
            //Field field = Typeface.class.getDeclaredField("SERIF");
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null,typeface);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("initTypeface:" + e.toString());
        }
    }

    private void initSkin(){
        SkinCompatManager.withoutActivity(this)
                .addInflater(new SkinAppCompatViewInflater())   // 基础控件换肤
                .addInflater(new SkinMaterialViewInflater())    // material design
                .addInflater(new SkinConstraintViewInflater())  // ConstraintLayout
                .addInflater(new SkinCardViewInflater())        // CardView v7
                //.addInflater(new SeekArcInflater())
                //.setSkinStatusBarColorEnable(true)              // 关闭状态栏换肤
//                .setSkinWindowBackgroundEnable(false)           // 关闭windowBackground换肤
//                .setSkinAllActivityEnable(false)                // true: 默认所有的Activity都换肤; false: 只有实现SkinCompatSupportable接口的Activity换肤
                .loadSkin();
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(HomeActivity.class) //重新启动后的activity
                .errorActivity(HomeActivity.class) //崩溃后的错误activity
                //.eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    private void initDataBase(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,"music-db");
        Database database = helper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();
        //初始化播放顺序
        int i = SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_ORDER,-1);
        if(i == -1){
            SPUtils.getInstance().put(SPUtilsConfig.PLAYING_ORDER,SPUtilsConfig.ORDER_PLAY);
        }
    }

    private void initService(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationUtils.initMusicNotificationChannel();
        Intent musicServiceIntent = new Intent(this, MusicService.class);
        startService(musicServiceIntent);
        MusicConnection musicConnection = new MusicConnection();
        bindService(musicServiceIntent,musicConnection, Context.BIND_AUTO_CREATE);

    }

    //视频内核
    private void initPlayer(){
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        //IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
    }

}
