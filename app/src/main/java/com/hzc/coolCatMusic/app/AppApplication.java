package com.hzc.coolCatMusic.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.hzc.coolCatMusic.BuildConfig;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.entity.DaoMaster;
import com.hzc.coolCatMusic.entity.DaoSession;
import com.hzc.coolCatMusic.service.MusicConnection;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.main.HomeActivity;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;

import org.greenrobot.greendao.database.Database;

import java.lang.reflect.Field;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * Created by goldze on 2017/7/16.
 */

public class AppApplication extends BaseApplication {
    public static DaoSession daoSession;
    public static boolean isUserPressThumb = false;//判断是否拖动进度条
    public static Boolean isPlayComplete = true;//判断播放是否结束
    public static Boolean isOldPlaying = false;//上一次播放动作

    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(GlobalData.isDebug);
        //初始化全局异常崩溃
        initCrash();
        initDataBase();
        initService();
        initPlayer();
        AssetManager assetManager = getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "font/mi_sans_normal.ttf");
        try {
            //Field field = Typeface.class.getDeclaredField("SERIF");
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null,typeface);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("initTypeface:" + e.toString());
        }
        //内存泄漏检测
        /*if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }*/
    }


    public void initTheme(){
        if(SPUtils.getInstance().getString(SPUtilsConfig.Theme_TEXT_FONT).equals(SPUtilsConfig.THEME_TEXT_FONT_MI_SANS_NORMAL)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppTheme2);
        }
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
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
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
        Intent MusicServiceIntent = new Intent(this, MusicService.class);
        startService(MusicServiceIntent);
        MusicConnection musicConnection = new MusicConnection();
        bindService(MusicServiceIntent,musicConnection, Context.BIND_AUTO_CREATE);

        //NotificationUtils.createForeNotification(this,"0","name");
    }

    //视频内核
    private void initPlayer(){
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        //IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
    }
}
