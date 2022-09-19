package com.hzc.coolCatMusic.ui.detail;

import static com.hzc.coolCatMusic.service.MusicConnection.musicInterface;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hzc.coolCatMusic.BR;
import com.hzc.coolCatMusic.R;
import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.AppViewModelFactory;
import com.hzc.coolCatMusic.databinding.ActivityDetailBinding;
import com.hzc.coolCatMusic.entity.LocalSongEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.ui.costom.SeekArc;
import com.hzc.coolCatMusic.ui.main.HomeViewModel;
import com.hzc.coolCatMusic.utils.DaoUtils.MusicUtils;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.goldze.mvvmhabit.base.BaseActivity;

public class DetailActivity extends BaseActivity<ActivityDetailBinding,DetailViewModel> {
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public DetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(DetailViewModel.class);
    }



    @Override
    public void initData() {
        super.initData();

        viewModel.mainColor = ContextCompat.getColor(getApplication(),R.color.transparent);
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                AppApplication.isUserPressThumb = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(!AppApplication.isPlayComplete){
                    musicInterface.seekTo(binding.seekBar.getProgress());
                }
                AppApplication.isUserPressThumb = false;
            }
        });

        //还原当前正在播放的歌曲
        PlayingMusicEntity playingMusicEntity = MusicUtils.getPlayingMusicEntity();
        initSong(playingMusicEntity);
    }

    private boolean isChangePlayImage = true;
    private boolean isChangeStopImage = true;
    private String nowSongImage = "";
    private int nowSongMax = 0;
    private ObjectAnimator objectAnimator;

    public void initSong(PlayingMusicEntity playingMusicEntity){
        if(playingMusicEntity == null){
            return;
        }
        viewModel.toolbarViewModel.setTitleText(playingMusicEntity.getSongName());
        int duration = playingMusicEntity.getDuration();
        int current = playingMusicEntity.getCurrent();
        boolean isPlay = playingMusicEntity.getIsPlay();
        String songImage = playingMusicEntity.getSongImage();
        if(songImage != null && !nowSongImage.equals(songImage)){
            binding.songImage.setImageURI(Uri.parse(songImage));
            nowSongImage = songImage;
        }
        if(duration != 0 && nowSongMax != duration){
            binding.seekBar.setMax(duration);
            nowSongMax = duration;
        }
        binding.ivMain.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this).load(playingMusicEntity.getSongImage()).error(R.drawable.ceshi)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(24,3))).into(binding.ivMain);

        if(isPlay){
            isChangeStopImage = true;
            if(isChangePlayImage){
                binding.ivPlay.setImageResource(R.drawable.home_music_stop_uncheck);
                isChangePlayImage = false;
                if(objectAnimator == null){
                    objectAnimator = ObjectAnimator.ofFloat(binding.songImage,"rotation",0.0f,360.0f);
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
                binding.ivPlay.setImageResource(R.drawable.home_music_play_uncheck);
                isChangeStopImage = true;
                if(objectAnimator != null){
                    objectAnimator.pause();
                }
            }
        }
        binding.seekBar.setProgress(current);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.changePlaying.observe(this, this::initSong);
    }
}
