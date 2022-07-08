package com.hzc.coolCatMusic.ui.main;

import static com.hzc.coolCatMusic.service.MusicConnection.musicInterface;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.ActivityHomeBinding;
import com.google.android.material.tabs.TabLayout;
import com.hzc.coolCatMusic.service.MusicService;
import com.hzc.coolCatMusic.ui.adapter.HomeCollectionAdapter;
import com.hzc.coolCatMusic.ui.costom.CircleImage;
import com.hzc.coolCatMusic.ui.costom.NiceImageView;
import com.hzc.coolCatMusic.ui.costom.PlayingListDialog;
import com.hzc.coolCatMusic.ui.costom.SeekArc;
import com.hzc.coolCatMusic.ui.homefragment1.LocalMusicFragment;
import com.hzc.coolCatMusic.utils.NotificationUtils;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.HashMap;
import java.util.Map;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.KLog;

public class HomeActivity extends BaseActivity<ActivityHomeBinding,HomeViewModel> {

    public DrawerLayout mainDrawerLayout;
    public CircleImage progressImage;
    public SeekArc progressSeekArc;
    public FrameLayout mainFrameLayout;
    public NiceImageView mainPlayingList;

    public LinearLayout navigationSleepMode;
    public LinearLayout navigationSensorMode;
    public FrameLayout navigationFrameLayout;

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
        navigationSleepMode.setOnClickListener(view -> {
            navigationSleepMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));
            navigationSensorMode.setBackgroundColor(Color.TRANSPARENT);
            switchNavigationView(NavigationSleepFragment.getInstance());
        });
        navigationSensorMode.setOnClickListener(view -> {
            navigationSleepMode.setBackgroundColor(Color.TRANSPARENT);
            navigationSensorMode.setBackground(ContextCompat.getDrawable(this,R.drawable.navigation_button_check));
            switchNavigationView(NavigationSensorFragment.getInstance());
        });
        navigationFrameLayout = binding.mainNavigation.navigationFrameLayout;

    }

    private void switchNavigationView(Fragment showFragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        //动画需要设置在最前面
        fragmentTransaction.setCustomAnimations(R.anim.fragment_enter,R.anim.fragment_exit,R.anim.fragment_pop_enter,R.anim.fragment_pop_exit);
        if(!showFragment.isAdded()){
            fragmentTransaction.add(R.id.navigationFrameLayout, showFragment);
        }
        Fragment sleepFragment = NavigationSleepFragment.getInstance();
        Fragment sensorFragment = NavigationSensorFragment.getInstance();
        if(!sleepFragment.isHidden()){
            fragmentTransaction.hide(sleepFragment);
        }
        if(!sensorFragment.isHidden()){
            fragmentTransaction.hide(sensorFragment);
        }
        if(showFragment == HomeFragment.getInstance()){
            fragmentTransaction
                    .show(showFragment)
                    .commit();
        }
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
