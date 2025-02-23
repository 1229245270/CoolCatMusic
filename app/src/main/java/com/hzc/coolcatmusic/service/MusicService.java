package com.hzc.coolcatmusic.service;


import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.SPUtilsConfig;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.data.source.http.service.DemoApiService;
import com.hzc.coolcatmusic.entity.ChatGPTEntity;
import com.hzc.coolcatmusic.entity.ChatGPTRequest;
import com.hzc.coolcatmusic.entity.ChatGPTResponse;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.entity.TimingEntity;
import com.hzc.coolcatmusic.ui.chatgpt.ChatFloating;
import com.hzc.coolcatmusic.utils.DaoUtils.ChatGPTUtils;
import com.hzc.coolcatmusic.utils.DaoUtils.MusicUtils;
import com.hzc.coolcatmusic.utils.LongTimeRetrofitClient;
import com.hzc.coolcatmusic.utils.NotificationUtils;
import com.hzc.coolcatmusic.utils.RetrofitClient;
import com.hzc.public_method.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseBean;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.NetCallback;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class MusicService extends Service {
    public MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private ConnectivityManager connectivityManager;
    private Timer timer;
    //private ScheduledExecutorService scheduledExecutorService;

    private ScreenListener screenListener;

    private TimingEntity timingEntity;
    private DemoApiService demoApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);//设定CUP锁定
        //播放结束监听事件
        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            KLog.d("播放结束");
            AppApplication.isPlayComplete = true;
        });

        demoApiService = LongTimeRetrofitClient.getInstance().create(DemoApiService.class);

        initScreenListener();
        initSensorListener();
        initNetworkListener();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                    Integer.parseInt(NotificationUtils.MUSIC_CHANNEL_ID),
                    NotificationUtils.musicNotification(null,null,null),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }else{
            startForeground(
                    Integer.parseInt(NotificationUtils.MUSIC_CHANNEL_ID),
                    NotificationUtils.musicNotification(null,null,null));
        }
    }

    /**
     * 初始化屏幕监听
     * */
    private void initScreenListener(){
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                KLog.d("屏幕打开了");
                ToastUtils.showShort("屏幕打开了");
            }

            @Override
            public void onScreenOff() {
                KLog.d("屏幕关闭了");
            }

            @Override
            public void onUserPresent() {
                KLog.d("屏幕解锁了");
                ToastUtils.showShort("屏幕解锁了");
            }
        });
    }

    private final int timeInterval = 1000;
    private long lastTime = 0;
    private float lastX,lastY,lastZ;
    /**
     * 初始化水平传感器监听
     * */
    private void initSensorListener(){
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime < timeInterval) {
                        return;//如果两次回调间隔过小，直接忽略
                    }
                    //读取传感器监听到加速度 m/(s^2)
                    float[] values = event.values;
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];

                    //计算和上次的变化值
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;
                    float deltaZ = z - lastZ;

                    //更新变化值
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    //计算灵敏度 m/(s^3)
                    //KLog.d("111111 deltaX:" + deltaX + ",deltaY:" + deltaY + ",deltaZ:" + deltaZ);
                    double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
                    //KLog.d("speed:" + speed);
                    lastTime = System.currentTimeMillis();
                    if (speed > 5) {
                        //回调执行
                        //KLog.d("111111 speed > 300");
                        lastTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * 初始化网络监听
     * */
    private void initNetworkListener(){
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                boolean isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                boolean isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                if(isWifi){
                    //ToastUtils.showShort("当前正在使用Wifi");
                }else if(isCellular){
                    ToastUtils.showShort("当前正在使用流量");
                }
                AppApplication.hasNetWork = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                ToastUtils.showShort("当前无网络连接");
                AppApplication.hasNetWork = false;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyMusicBinder();
    }

    @Override
    public void onDestroy() {//关闭时释放资源
        super.onDestroy();
        KLog.d("onDestroy");
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        audioManager.abandonAudioFocus(audioFocusChangeListener);//释放焦点

        screenListener.unregisterListener();
        connectivityManager.unregisterNetworkCallback(new ConnectivityManager.NetworkCallback());
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if(i == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){//指示申请的AudioFocus是短暂性的，很快释放
                pause();
            }else if(i == AudioManager.AUDIOFOCUS_GAIN){//指示申请得到的AudioFocus不知道会持续多久，一般是长期占有
                continuePlay();
            }else if(i == AudioManager.AUDIOFOCUS_LOSS){//失去AudioFocus，直接释放资源最好
                audioManager.abandonAudioFocus(audioFocusChangeListener);
                stop();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.d("onStartCommand");
        String notificationType = intent.getStringExtra(NotificationUtils.NOTIFICATION_TYPE);
        if(notificationType != null){
            KLog.d("notificationType:" + notificationType);
            switch (notificationType){
                case NotificationUtils.NOTIFICATION_UP_SONG:
                    upSong();
                    break;
                case NotificationUtils.NOTIFICATION_PLAY_SONG:
                    if(isPlaying()){
                        pause();
                    }else{
                        continuePlay();
                    }
                    break;
                case NotificationUtils.NOTIFICATION_NEXT_SONG:
                    nextSong();
                    break;
            }
        }
        return START_REDELIVER_INTENT;
    }

    class MyMusicBinder extends Binder implements MusicInterface{

        @Override
        public void play(Intent intent,String filePath,int playingNum) {
            MusicService.this.play(intent,filePath,playingNum);
        }

        @Override
        public void nextSong() {
            MusicService.this.nextSong();
        }

        @Override
        public void upSong() {
            MusicService.this.upSong();
        }

        @Override
        public void pause() {
            MusicService.this.pause();
        }

        @Override
        public void continuePlay() {
            MusicService.this.continuePlay();
        }

        @Override
        public void seekTo(int progress) {
            MusicService.this.seekTo(progress);
        }

        @Override
        public boolean isPlaying() {
            return MusicService.this.isPlaying();
        }

        @Override
        public int getCurrentPosition() {
            return MusicService.this.getCurrentPosition();
        }

        @Override
        public int getDuration() {
            return MusicService.this.getDuration();
        }

        @Override
        public void sendChatGPTEntity(ChatGPTEntity chatGPTEntity) {
            MusicService.this.sendChatGPTEntity(chatGPTEntity);
        }

    }

    private boolean requestFocus(){
        //获得焦点
        int result = audioManager.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public TimingEntity getTimingEntity(){
        return timingEntity;
    }

    public void setTimingEntity(TimingEntity timingEntity) {
        this.timingEntity = timingEntity;
    }

    //播放音乐
    public void play(final Intent intent, String filePath,int playingNum){
        if(mediaPlayer != null){
            mediaPlayer.reset();//重置
        }
        //睡眠定时
        if(timingEntity != null && timingEntity.isTiming()){
            long nowTime = System.currentTimeMillis();
            if(nowTime - timingEntity.getNowTime() >= timingEntity.getTiming()){
                return;
            }
        }

        if(requestFocus()){
            try{
                mediaPlayer.setDataSource(filePath);
                mediaPlayer.prepareAsync();//异步准备，开启子线程加载资源
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    //prepare()方法准备完毕后，此方法调用
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        KLog.d("开始播放中");
                        SeekPlayMessage(intent,playingNum);
                        SeekPlayCurrent();
                    }
                });
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"未找到文件路径，请重新扫描",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void playingSong(String next){
        int playingNum = SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_NUM);
        if(playingNum < 0){
            return;
        }
        List<PlayingMusicEntity> list = MusicUtils.getPlayingMusicEntityList();
        if(list.size() == 0){
            return;
        }
        switch (SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_ORDER)){
            case SPUtilsConfig.ORDER_PLAY:
                if(next.equals("nextSong")){
                    playingNum++;
                }else{
                    playingNum--;
                }
                break;
            case SPUtilsConfig.RANDOM_PLAY:
                int total = list.size();
                playingNum = (int)(Math.random() * total);
                break;
            case SPUtilsConfig.LOOP_PLAY:
                break;
        }
        if(list.size() <= playingNum || playingNum < 0){
            playingNum = 0;
        }
        PlayingMusicEntity entity = list.get(playingNum);
        Intent intent = new Intent();
        intent.putExtra("src",entity.getSrc());
        intent.putExtra("allName",entity.getAllName());
        intent.putExtra("singer",entity.getSinger());
        intent.putExtra("singerImage",entity.getSingerImage());
        intent.putExtra("songName",entity.getSongName());
        intent.putExtra("songImage",entity.getSongImage());
        intent.putExtra("lyrics",entity.getLyrics());
        intent.putExtra("yearIssue",entity.getYearIssue());
        play(intent, entity.getSrc(),playingNum);
    }


    public void nextSong() {
        playingSong("nextSong");
    }

    public void upSong() {
        playingSong("upSong");
    }

    //继续播放
    public void continuePlay(){
        if(mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
            KLog.d("继续播放中");
            SeekPlayCurrent();
        }
    }

    //暂停播放
    public void pause(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            KLog.d("暂停播放");
            mediaPlayer.pause();
        }
    }

    //放弃播放
    public void stop(){
        if(mediaPlayer != null){
            KLog.d("放弃播放");
            mediaPlayer.stop();
        }
    }

    //更新播放进度
    public void seekTo(int progress){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(progress);
        }
    }


    //发送进度信息
    public void SeekPlayCurrent(){
        AppApplication.isPlayComplete = false;
        if(timer == null){
            KLog.d("创建音乐timer对象");
            //timer就是开启子线程执行任务，与纯粹的子线程不同的是可以控制子线程执行的时间
        }else{
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PlayingMusicEntity entity = MusicUtils.getPlayingMusicEntity();
                if(entity != null){
                    boolean isPlay = mediaPlayer.isPlaying();
                    if(!AppApplication.isUserPressThumb){
                        entity.setIsPlay(isPlay);
                        entity.setCurrent(getCurrentPosition());
                        RxBus.getDefault().post(entity);
                    }
                    if(!isPlay){
                        this.cancel();
                        //播放完成后自动下一曲
                        if(AppApplication.isPlayComplete && getCurrentPosition() >= getDuration() - 500 && getDuration() > 0 && getCurrentPosition() > 0){
                            nextSong();
                        }
                    }
                    //通知变化
                    if(AppApplication.isOldPlaying != isPlay){
                        AppApplication.isOldPlaying = isPlay;
                        NotificationUtils.sendMusicNotification(null,null,null);
                    }
                }
            }//开始计时任务后的200毫秒后第一次执行run方法，以后每500毫秒执行一次
        },200,500);
    }

    public static final String SRC = "src";
    public static final String ALL_NAME = "allName";
    public static final String SINGER = "singer";
    public static final String SINGER_IMAGE = "singerImage";
    public static final String SONG_NAME = "songName";
    public static final String SONG_IMAGE = "songImage";
    public static final String LYRICS = "lyrics";
    public static final String YEAR_ISSUE = "yearIssue";

    //发送播放信息
    public void SeekPlayMessage(Intent intent,int playingNum){
        String src = intent.getStringExtra(SRC);
        String allName = intent.getStringExtra(ALL_NAME);
        String singer = intent.getStringExtra(SINGER);
        String singerImage = intent.getStringExtra(SINGER_IMAGE);
        String songName = intent.getStringExtra(SONG_NAME);
        String songImage = intent.getStringExtra(SONG_IMAGE);
        int current = getCurrentPosition();
        int duration = getDuration();
        String lyrics = intent.getStringExtra(LYRICS);
        String yearIssue = intent.getStringExtra(YEAR_ISSUE);
        //判断歌曲是否已存在播放列表
        PlayingMusicEntity entity = MusicUtils.getPlayingMusicEntity(src);
        if(entity == null){
            entity = new PlayingMusicEntity();
            entity.setSrc(src);
            entity.setAllName(allName);
            entity.setSinger(singer);
            entity.setSingerImage(singerImage);
            entity.setSongName(songName);
            entity.setSongImage(songImage);
            entity.setLyrics(lyrics);
            entity.setYearIssue(yearIssue);
            MusicUtils.insertOrReplacePlayingMusicEntity(entity);
            List<PlayingMusicEntity> list = MusicUtils.getPlayingMusicEntityList();
            SPUtils.getInstance().put(SPUtilsConfig.PLAYING_NUM,list.size() - 1);
        }else{
            SPUtils.getInstance().put(SPUtilsConfig.PLAYING_NUM,playingNum);
        }
        //无需保存到数据库
        entity.setCurrent(current);
        entity.setDuration(duration);
        entity.setIsPlay(isPlaying());
        RxBus.getDefault().post(entity);

        NotificationUtils.sendMusicNotification(songName,singer,null);
    }

    //获取播放状态
    public boolean isPlaying(){
        boolean isPlaying = false;
        try {
            isPlaying = mediaPlayer.isPlaying();
        }catch (Exception e){
            e.printStackTrace();
        }
        return isPlaying;
    }

    //获取当前播放位置
    public int getCurrentPosition(){
        int position = 0;
        try {
            position = mediaPlayer.getCurrentPosition();
        }catch (Exception e){
            e.printStackTrace();
        }
        return position;
    }

    //获取播放对象总时长
    public int getDuration(){
        int duration = 0;
        try{
            duration = mediaPlayer.getDuration();
        }catch (Exception e){
            e.printStackTrace();
        }
        return duration;
    }

    private String toTime(int time){
        time = time/1000;
        String m;
        String s;
        int intS = time % 60;
        int intM = time / 60;
        if(intM >= 10 && intM < 60) {
            m = String.valueOf(intM);
        } else {
            m = "0" + intM;
        }
        if(intS <10) {
            s = "0" + intS;
        } else {
            s = String.valueOf(intS);
        }
        return m + ":" + s;
    }

    /**
     * 发送chatgpt聊天
     * @param chatGPTEntity
     */
    public void sendChatGPTEntity(ChatGPTEntity chatGPTEntity){
        List<ChatGPTRequest.Messages> messagesList = new ArrayList<>();
        List<ChatGPTEntity> historyList = ChatGPTUtils.getChatGPTEntityForChatForm(chatGPTEntity.getChatForm());
        //添加发送数据,空窗口时新增窗口id
        historyList.add(chatGPTEntity);
        long chatForm = ChatGPTUtils.addChatGPTEntity(chatGPTEntity);
        AppApplication.chatGPTRead.put(chatForm,false);

        for(int i = 0;i < historyList.size();i++){
            ChatGPTEntity gptEntity = historyList.get(i);
            ChatGPTRequest.Messages messages = new ChatGPTRequest.Messages();
            messages.setContent(gptEntity.getContent());
            messages.setRole(gptEntity.getRole());
            if(!"error".contains(messages.getRole())){
                messagesList.add(messages);
            }
        }

        RequestMethod.requestApi(integer -> {
            ChatGPTRequest request = new ChatGPTRequest();
            request.setModel("gpt-3.5-turbo");
            request.setMessages(messagesList);
            return demoApiService.chatGPTV1ChatCompletions(request);
        },new NetCallback<BaseBean>(){
            @Override
            public void onSuccess(BaseBean result) {
                ChatGPTResponse chatGPTResponse = result.getResultBean(ChatGPTResponse.class);
                String content = chatGPTResponse.getChoices().get(0).getMessage().getContent();
                if(content.length() > 2 && "\n\n".equals(content.substring(0,2))){
                    content = content.substring(2);
                }
                ChatGPTEntity entity = new ChatGPTEntity();
                entity.setContent(content);
                entity.setRole(chatGPTResponse.getChoices().get(0).getMessage().getRole());
                entity.setChatForm(chatForm);
                ChatGPTUtils.addChatGPTEntity(entity);
            }

            @Override
            public void onFailure(String msg) {
                ChatGPTEntity entity = new ChatGPTEntity();
                entity.setContent(msg);
                entity.setRole("error");
                entity.setChatForm(chatForm);
                ChatGPTUtils.addChatGPTEntity(entity);
            }

            @Override
            public void onFinish() {
                AppApplication.chatGPTRead.put(chatForm,true);
            }
        });
    }

    public interface MusicInterface{
        void play(Intent intent,String filePath,int playingNum);
        void nextSong();
        void upSong();
        void pause();
        void continuePlay();
        void seekTo(int progress);
        boolean isPlaying();
        int getCurrentPosition();
        int getDuration();

        void sendChatGPTEntity(ChatGPTEntity chatGPTEntity);
    }

}
