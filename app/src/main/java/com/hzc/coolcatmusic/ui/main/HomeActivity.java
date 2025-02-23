package com.hzc.coolcatmusic.ui.main;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.hzc.coolcatmusic.R;
import com.hzc.coolcatmusic.databinding.ActivityHomeBinding;
import com.hzc.coolcatmusic.BR;
import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.AppViewModelFactory;
import com.hzc.coolcatmusic.data.DemoRepository;
import com.hzc.coolcatmusic.ui.costom.CircleImage;
import com.hzc.coolcatmusic.ui.costom.NiceImageView;
import com.hzc.coolcatmusic.ui.costom.PlayingListDialog;
import com.hzc.coolcatmusic.ui.costom.SeekArc;
import com.hzc.coolcatmusic.ui.detail.DetailActivity;
import com.hzc.coolcatmusic.service.MusicConnection;
import com.hzc.coolcatmusic.utils.NotificationUtils;
import com.hzc.public_method.PageMethod;

import java.lang.reflect.Field;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.KLog;

public class HomeActivity extends BaseActivity<ActivityHomeBinding,HomeViewModel<DemoRepository>> {

    public DrawerLayout mainDrawerLayout;
    public RelativeLayout mainRelativeLayout;
    public CircleImage progressImage;
    public SeekArc progressSeekArc;
    public FrameLayout mainFrameLayout;
    public NiceImageView mainPlayingList;
    public NiceImageView mainDetail;
    public NavigationView mainNavigationView;

    public LinearLayout navigationSleepMode;
    public LinearLayout navigationSensorMode;
    public LinearLayout navigationActionMode;
    public FrameLayout navigationFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        super.onCreate(savedInstanceState);
    }

    private float startX = 0;
    private float startY = 0;
    boolean isBack = false;
    private View showView;
    private View hideView;
    private long beforeTime;
    //每毫秒初始宽度
    private float comeWidth = 0;

    //当前事件是否被消耗,0未消耗，1横向，2竖向
    private int eventEat;
    private int moveY = 0;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //点击音乐条,禁止侧滑
        if(isOpenProgress){
            return super.dispatchTouchEvent(ev);
        }
        HomeFragment homeFragment = HomeFragment.getInstance();
        FragmentManager manager = getSupportFragmentManager();
        int currentItem = homeFragment.mainViewPager.getCurrentItem();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                eventEat = 0;
                moveY = 0;
                //HomeFragment.getInstance().mainViewPager.requestDisallowInterceptTouchEvent(true);
                startX = ev.getX();
                startY = ev.getY();
                isBack = false;
                beforeTime = System.currentTimeMillis();
                comeWidth = 0;
                homeFragment.mainViewPager.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                //来到新的坐标
                float endX = ev.getX();
                float endY = ev.getY();
                //计算与点击时坐标的偏移量
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //竖直方向持续偏移量
                moveY += Math.abs(distanceY);
                //主页
                //当前界面，先拦截activity事件，再拦截fragment事件
                if(manager.getBackStackEntryCount() == 0){
                    if(currentItem == 0){
                        //大于为右滑
                        if(distanceX > 0){

                        }else if(distanceX < 0){
                            //根据业务逻辑判断是否需要禁止父 ViewGroup 拦截事件,禁止了drawerLayout侧滑事件
                            homeFragment.mainViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }else{
                        homeFragment.mainViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(HomeViewModel.class);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initData() {
        super.initData();
        initDrawableLayout();
        initProgress();
        initPlayingList();
        initNavigation();
        mainFrameLayout = binding.mainFrameLayout;
        startFrameLayout(HomeFragment.getInstance(),null);
        initBackStackListener();

        NotificationUtils.requestNotification(this);
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() <= 0 && System.currentTimeMillis() - exitTime > 2000){
            Toast.makeText(this,"再按一次返回",Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            int count = mainFrameLayout.getChildCount();
            if(count >= 2){
                showView = mainFrameLayout.getChildAt(count - 1);
                hideView = mainFrameLayout.getChildAt(count - 2);
            }else{
                showView = null;
                hideView = null;
            }
            if(PageMethod.isTouch){
                super.onBackPressed();
                PageMethod.isTouch = false;
            }else{
                PageMethod.onBackPressed(showView, this, new PageMethod.BackCallback() {
                    @Override
                    public void onBack() {
                        HomeActivity.super.onBackPressed();
                    }
                });
            }

        }
    }

    /**
     * 开启fragment
     * */
    public void startFrameLayout(Fragment showFragment, Bundle bundle){
        FragmentManager manager = getSupportFragmentManager();
        showFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        //动画需要设置在最前面
        //fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,0,R.anim.fragment_pop_enter,0);
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,0);

        if(!showFragment.isAdded()){
            fragmentTransaction.add(R.id.mainFrameLayout, showFragment);
        }
        for(Fragment fragment : manager.getFragments()){
            if(!fragment.isHidden()){
                //fragmentTransaction.hide(fragment);
            }
        }

        //主页展示
        if(showFragment == HomeFragment.getInstance()){
            fragmentTransaction
                    //.replace(R.id.mainFrameLayout, showFragment)
                    //.show(showFragment)
                    .commitNow();
        }else{
            fragmentTransaction
                    //.show(showFragment)
                    //.replace(R.id.mainFrameLayout, showFragment)
                    .addToBackStack(null)
                    .commit();
            manager.executePendingTransactions();
        }
    }

    /**
     * 初始化侧边栏布局
     * */
    private void initDrawableLayout(){
        mainNavigationView = binding.mainNavigationView;
        mainDrawerLayout = binding.mainDrawerLayout;
        mainRelativeLayout = binding.mainRelativeLayout;
        mainDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mainDrawerLayout.setDrawerElevation(0);
        mainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                View mContent = mainDrawerLayout.getChildAt(0);//返回抽屉布局中的索引为0的子view
                float scale = 1 - slideOffset;//偏移量导致scale从1.0-0.0
                float rightScale = 0.8f + scale * 0.2f;//将内容区域从1.0-0.0转化为1.0-0.8
                float drawerScale = 0.8f + slideOffset * 0.2f;//将内容区域从1.0-0.0转化为1.0-0.8
                //mainRelativeLayout.setAlpha(0.6f + 0.4f * scale);//开始这里设置成了这样，导致背景透明度有1.0-0.6
                mainRelativeLayout.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
                mainRelativeLayout.setPivotX(0);
                mainRelativeLayout.setPivotY(mContent.getMeasuredHeight() / 2);
                mContent.invalidate();
                mainRelativeLayout.setScaleX(rightScale);
                mainRelativeLayout.setScaleY(rightScale);
                drawerView.setScaleX(drawerScale);
                drawerView.setScaleY(drawerScale);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        mainDetail = binding.mainDetail;
        mainDetail.setTransitionName("song");
        mainDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this, Pair.create(mainDetail, "song"));
                startActivity(intent, activityOptions.toBundle());
            }
        });
    }

    /**
     * 初始化监听返回栈，兼容侧边栏滑动
     * */
    private void initBackStackListener(){
        setMEdgeSize(true);
        FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                setMEdgeSize(true);
            }
        });
    }

    /**
     * 反射，修改侧边栏响应范围
     * */
    public void setMEdgeSize(boolean isOpen){
        FragmentManager manager = getSupportFragmentManager();
        if(manager.getBackStackEntryCount() > 0){
            isOpen = false;
        }
        try {
            if(mainDrawerLayout == null){
                return;
            }
            Field leftDraggerField = mainDrawerLayout.getClass().getDeclaredField("mLeftDragger");//通过反射获得DrawerLayout类中mLeftDragger字段
            leftDraggerField.setAccessible(true);//私有属性要允许修改
            ViewDragHelper vdh = (ViewDragHelper) leftDraggerField.get(mainDrawerLayout);//获取ViewDragHelper的实例, 通过ViewDragHelper实例获取mEdgeSize字段
            Field edgeSizeField = vdh.getClass().getDeclaredField("mEdgeSize");//依旧是通过反射获取ViewDragHelper类中mEdgeSize字段, 这个字段控制边缘触发范围
            edgeSizeField.setAccessible(true);//依然是私有属性要允许修改
            int edgeSize = edgeSizeField.getInt(vdh);//这里获得mEdgeSize真实值
            //KLog.d("mEdgeSize: "+edgeSize);//这里可以打印一下看看值是多少

            //Start 获取手机屏幕宽度，单位px
            Point point = new Point();
            getWindowManager().getDefaultDisplay().getRealSize(point);
            //End 获取手机屏幕
            //isOpen:true,point: 1080,edgeSize:55
            KLog.d("isOpen:" + isOpen + ",point: " + point.x + ",edgeSize:" + edgeSize);//依然可以打印一下看看值是多少
            edgeSizeField.setInt(vdh, Math.max(isOpen ? edgeSize : 0,isOpen ? point.x : 0));//这里设置mEdgeSize的值！！！，Math.max函数取得是最大值，也可以自己指定想要触发的范围值，如: 500,注意单位是px
            //写到这里已经实现了，但是你会发现，如果长按触发范围，侧边栏也会弹出来，而且速度不太同步，稳定

            //Start 解决长按会触发侧边栏
            //长按会触发侧边栏是DrawerLayout类的私有类ViewDragCallback中的mPeekRunnable实现导致，我们通过反射把它置空
            Field leftCallbackField = mainDrawerLayout.getClass().getDeclaredField("mLeftCallback");//通过反射拿到私有类ViewDragCallback字段
            leftCallbackField.setAccessible(true);//私有字段设置允许修改
            ViewDragHelper.Callback vdhCallback = (ViewDragHelper.Callback) leftCallbackField.get(mainDrawerLayout);//ViewDragCallback类是私有类，我们返回类型定义成他的父类
            Field peekRunnableField = vdhCallback.getClass().getDeclaredField("mPeekRunnable");//我们依然是通过反射拿到关键字段，mPeekRunnable
            peekRunnableField.setAccessible(true);//不解释了
            //定义一个空的实现
            Runnable nullRunnable = new Runnable(){
                @Override
                public void run() {

                }
            };
            peekRunnableField.set(vdhCallback, nullRunnable);//给mPeekRunnable字段置空
            //End 解决长按触发侧边栏
        } catch (NoSuchFieldException | IllegalAccessException e) {
            KLog.e(e);
        }
    }

    /**
     * 初始化进度栏
     * */
    private boolean isOpenProgress = false;
    private void initProgress(){
        progressImage = binding.mainProgress.findViewById(R.id.progress_image);
        progressSeekArc = binding.mainProgress.findViewById(R.id.progress_seekArc);

        progressSeekArc.setOnLongClick(new SeekArc.OnLongClick() {

            @Override
            public void actionDown() {
                //锁定侧边栏弹出，防止冲突
                mainDrawerLayout.requestDisallowInterceptTouchEvent(true);
                mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                isOpenProgress = true;
            }

            @Override
            public void actionUp() {
                //解锁侧边栏
                mainDrawerLayout.requestDisallowInterceptTouchEvent(false);
                mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                isOpenProgress = false;
            }

            @Override
            public void upSong() {
                try {
                    MusicConnection.musicInterface.upSong();
                }catch (Exception e){
                    KLog.d(e.toString());
                }
            }

            @Override
            public void play() {
                try {
                    if(MusicConnection.musicInterface.isPlaying()){
                        MusicConnection.musicInterface.pause();
                    }else{
                        MusicConnection.musicInterface.continuePlay();
                    }
                }catch (Exception e){
                    KLog.d(e.toString());
                }
            }

            @Override
            public void nextSong() {
                try {
                    MusicConnection.musicInterface.nextSong();
                }catch (Exception e){
                    KLog.d(e.toString());
                }
            }
        });
        progressSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                AppApplication.isUserPressThumb = true;
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                if(!AppApplication.isPlayComplete){
                    MusicConnection.musicInterface.seekTo(seekArc.getProgress());
                }
                AppApplication.isUserPressThumb = false;
            }
        });
    }

    /**
     * 初始化音乐列表点击
     * */
    private void initPlayingList(){
        mainPlayingList = binding.mainPlayingList;
        mainPlayingList.setOnClickListener(view -> {
            PlayingListDialog dialog = new PlayingListDialog(this);
            dialog.show();
        });
    }

    /**
     * 初始化侧边栏跳转
     * */
    private void initNavigation(){
        navigationSleepMode = binding.mainNavigation.navigationSleepMode;
        navigationSensorMode = binding.mainNavigation.navigationSensorMode;
        navigationActionMode = binding.mainNavigation.navigationActionMode;
        switchNavigationView(NavigationSleepFragment.getInstance(),0);
        navigationSensorMode.setBackgroundColor(Color.TRANSPARENT);
        navigationActionMode.setBackgroundColor(Color.TRANSPARENT);
        navigationSleepMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));

        navigationSleepMode.setOnClickListener(view -> {
            navigationSensorMode.setBackgroundColor(Color.TRANSPARENT);
            navigationActionMode.setBackgroundColor(Color.TRANSPARENT);
            navigationSleepMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));
            switchNavigationView(NavigationSleepFragment.getInstance(),0);
            navigationIndex = 0;
        });
        navigationSensorMode.setOnClickListener(view -> {
            navigationSleepMode.setBackgroundColor(Color.TRANSPARENT);
            navigationActionMode.setBackgroundColor(Color.TRANSPARENT);
            navigationSensorMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));
            switchNavigationView(NavigationSensorFragment.getInstance(),1);
            navigationIndex = 1;
        });
        navigationActionMode.setOnClickListener(view -> {
            navigationSleepMode.setBackgroundColor(Color.TRANSPARENT);
            navigationSensorMode.setBackgroundColor(Color.TRANSPARENT);
            navigationActionMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));
            switchNavigationView(NavigationThemeFragment.getInstance(),2);
            navigationIndex = 2;
        });
    }

    private int navigationIndex = 0;
    private void switchNavigationView(Fragment showFragment,int index){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        //动画需要设置在最前面
        if(navigationIndex < index){
            fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,R.anim.fragment_exit,R.anim.fragment_pop_enter,R.anim.fragment_pop_exit);
        }else{
            fragmentTransaction.setCustomAnimations(R.anim.fragment_pop_enter,R.anim.fragment_pop_exit,R.anim.fragment_enter,R.anim.fragment_exit);
        }
        if(!showFragment.isAdded()){
            fragmentTransaction.add(R.id.navigationFrameLayout, showFragment);
        }
        Fragment sleepFragment = NavigationSleepFragment.getInstance();
        Fragment sensorFragment = NavigationSensorFragment.getInstance();
        Fragment actionFragment = NavigationThemeFragment.getInstance();
        if(!sleepFragment.isHidden()){
            fragmentTransaction.hide(sleepFragment);
        }
        if(!sensorFragment.isHidden()){
            fragmentTransaction.hide(sensorFragment);
        }
        if(!actionFragment.isHidden()){
            fragmentTransaction.hide(actionFragment);
        }
        fragmentTransaction
                .show(showFragment)
                .commit();
    }

    private boolean isChangePlayImage = true;
    private boolean isChangeStopImage = true;
    private String nowSongImage = "";
    private int nowSongMax = 0;
    private ObjectAnimator objectAnimator;

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.isOpenDrawerLayout.observe(this, aBoolean -> {
            mainDrawerLayout.openDrawer(GravityCompat.START);
        });
        //实时更新进度
        viewModel.changePlaying.observe(this,playingMusicEntity -> {
            int duration = playingMusicEntity.getDuration();
            int current = playingMusicEntity.getCurrent();
            boolean isPlay = playingMusicEntity.getIsPlay();
            String songImage = playingMusicEntity.getSongImage();
            if(songImage != null && !nowSongImage.equals(songImage)){
                progressImage.setImageURI(Uri.parse(songImage));
                nowSongImage = songImage;
            }
            if(duration != 0 && nowSongMax != duration){
                progressSeekArc.setMax(duration);
                nowSongMax = duration;
            }
            if(isPlay){
                isChangeStopImage = true;
                if(isChangePlayImage){
                    progressSeekArc.setPlayImage(R.drawable.home_music_stop_uncheck);
                    isChangePlayImage = false;
                    if(objectAnimator == null){
                        objectAnimator = ObjectAnimator.ofFloat(progressImage,"rotation",0.0f,360.0f);
                        objectAnimator.setDuration(3000);
                        LinearInterpolator linearInterpolator = new LinearInterpolator();
                        objectAnimator.setInterpolator(linearInterpolator);
                        objectAnimator.setRepeatCount(-1);
                        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
                    }
                    if(objectAnimator.isPaused()){
                        objectAnimator.resume();
                    }else{
                        objectAnimator.start();
                    }
                }
            }else{
                isChangePlayImage = true;
                if(isChangeStopImage){
                    progressSeekArc.setPlayImage(R.drawable.home_music_play_uncheck);
                    isChangeStopImage = true;
                    if(objectAnimator != null){
                        objectAnimator.pause();
                    }
                }
            }
            progressSeekArc.setProgress(current);
        });

        viewModel.changeFragment.observe(this,event -> {
            Fragment showFragment = event.getShowFragment();
            Bundle bundle = event.getBundle();
            startFrameLayout(showFragment,bundle);
        });

    }

}
