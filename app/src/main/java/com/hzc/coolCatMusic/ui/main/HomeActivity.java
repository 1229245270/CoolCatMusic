package com.hzc.coolCatMusic.ui.main;

import static com.hzc.coolCatMusic.service.MusicConnection.musicInterface;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.app.SPUtilsConfig;
import com.hzc.coolCatMusic.databinding.ActivityHomeBinding;
import com.hzc.coolCatMusic.ui.costom.CircleImage;
import com.hzc.coolCatMusic.ui.costom.NiceImageView;
import com.hzc.coolCatMusic.ui.costom.PlayingListDialog;
import com.hzc.coolCatMusic.ui.costom.SeekArc;
import com.hzc.coolCatMusic.ui.detail.DetailActivity;

import java.lang.reflect.Field;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.SPUtils;

public class HomeActivity extends BaseActivity<ActivityHomeBinding,HomeViewModel> {

    public DrawerLayout mainDrawerLayout;
    public RelativeLayout mainRelativeLayout;
    public CircleImage progressImage;
    public SeekArc progressSeekArc;
    public FrameLayout mainFrameLayout;
    public NiceImageView mainPlayingList;
    public NiceImageView mainDetail;

    public LinearLayout navigationSleepMode;
    public LinearLayout navigationSensorMode;
    public LinearLayout navigationActionMode;
    public FrameLayout navigationFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initTypeface();
        /*if(SPUtils.getInstance().getString(SPUtilsConfig.Theme_TEXT_FONT).equals(SPUtilsConfig.THEME_TEXT_FONT_MI_SANS_NORMAL)){
            setTheme(R.style.AppTheme);
        }else{
            setTheme(R.style.AppTheme2);
        }*/
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        super.onCreate(savedInstanceState);
    }

    /*private void initTypeface(){
        AssetManager assetManager = getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "font/mi_sans_normal.ttf");
        try {
            Field field = Typeface.class.getDeclaredField("SERIF");
            field.setAccessible(true);
            field.set(null,typeface);
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("initTypeface:" + e.toString());
        }
    }*/

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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        RxBus.getDefault().post("onRestart");
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if(!HomeFragment.getInstance().isHidden() && System.currentTimeMillis() - exitTime > 2000){
            Toast.makeText(this,"再按一次返回",Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            super.onBackPressed();
        }
    }

    public void startFrameLayout(Fragment showFragment, Bundle bundle){
        FragmentManager manager = getSupportFragmentManager();
        showFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        //动画需要设置在最前面
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,R.anim.fragment_exit,R.anim.fragment_pop_enter,R.anim.fragment_pop_exit);
        if(!showFragment.isAdded()){
            fragmentTransaction.add(R.id.mainFrameLayout, showFragment);
        }
        for(Fragment fragment : manager.getFragments()){
            if(!fragment.isHidden()){
                fragmentTransaction.hide(fragment);
            }
        }
        if(showFragment == HomeFragment.getInstance()){
            fragmentTransaction
                    .show(showFragment)
                    .commit();
        }else{
            fragmentTransaction
                    .show(showFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void initDrawableLayout(){
        mainDrawerLayout = binding.mainDrawerLayout;
        mainRelativeLayout = binding.mainRelativeLayout;
        mainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                KLog.d("slideOffset" + slideOffset);
                View mContent = mainDrawerLayout.getChildAt(0);//返回抽屉布局中的索引为0的子view
                float scale = 1 - slideOffset;//偏移量导致scale从1.0-0.0
                float rightScale = 0.8f + scale * 0.2f;//将内容区域从1.0-0.0转化为1.0-0.8

                //if (drawerView.getTag().equals("LEFT"))
                //{

                    float leftScale = 1 - 0.3f * scale;//0.7-1.0
                    mainRelativeLayout.setScaleX(leftScale);
                    mainRelativeLayout.setScaleY(leftScale);
                    mainRelativeLayout.setAlpha(0.6f + 0.4f * (1 - scale));//开始这里设置成了这样，导致背景透明度有1.0-0.6
//                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * scale);
                    mainRelativeLayout.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
                    mainRelativeLayout.setPivotX(0);
                    mainRelativeLayout.setPivotY(mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    mainRelativeLayout.setScaleX(rightScale);
                    mainRelativeLayout.setScaleY(rightScale);
                /*} else
                {
                    mainRelativeLayout.setTranslationX(-drawerView.getMeasuredWidth() * slideOffset);
                    //设置大小变化的中心
                    mainRelativeLayout.setPivotX(mContent.getMeasuredWidth());
                    mainRelativeLayout.setPivotY(mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    mainRelativeLayout.setScaleX(rightScale);
                    mainRelativeLayout.setScaleY(rightScale);
                }*/
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mainDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });


        mainDetail = binding.mainDetail;
        mainDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle());
            }
        });
    }

    private void initProgress(){
        progressImage = binding.mainProgress.findViewById(R.id.progress_image);
        progressSeekArc = binding.mainProgress.findViewById(R.id.progress_seekArc);

        progressSeekArc.setOnLongClick(new SeekArc.OnLongClick() {
            @Override
            public void upSong() {
                try {
                    musicInterface.upSong();
                }catch (Exception e){
                    KLog.d(e.toString());
                }
            }

            @Override
            public void play() {
                try {
                    if(musicInterface.isPlaying()){
                        musicInterface.pause();
                    }else{
                        musicInterface.continuePlay();
                    }
                }catch (Exception e){
                    KLog.d(e.toString());
                }
            }

            @Override
            public void nextSong() {
                try {
                    musicInterface.nextSong();
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
                    musicInterface.seekTo(seekArc.getProgress());
                }
                AppApplication.isUserPressThumb = false;
            }
        });
    }

    private void initPlayingList(){
        mainPlayingList = binding.mainPlayingList;
        mainPlayingList.setOnClickListener(view -> {
            PlayingListDialog dialog = new PlayingListDialog(this);
            dialog.show();
        });
    }

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
