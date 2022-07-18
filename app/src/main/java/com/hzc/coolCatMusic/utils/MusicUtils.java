package com.hzc.coolCatMusic.utils;

import com.hzc.coolCatMusic.app.AppApplication;
import com.hzc.coolCatMusic.app.SPUtilsConfig;
import com.hzc.coolCatMusic.entity.PlayingMusicEntity;
import com.hzc.coolCatMusic.entity.PlayingMusicEntityDao;

import java.util.List;

import me.goldze.mvvmhabit.utils.SPUtils;

public class MusicUtils {

    //获取当前播放歌曲
    public static PlayingMusicEntity getPlayingMusicEntity(){
        int playingNum = SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_NUM,0);
        List<PlayingMusicEntity> list = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();
        if(list != null && list.size() >= playingNum + 1) return list.get(playingNum);
        return null;
    }

    //获取当前播放歌曲
    public static PlayingMusicEntity getPlayingMusicEntity(String src){
        List<PlayingMusicEntity> list = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .where(PlayingMusicEntityDao.Properties.Src.eq(src))
                .list();
        if(list != null && list.size() > 0) return list.get(0);
        return null;
    }

    //获取播放歌曲列表
    public static List<PlayingMusicEntity> getPlayingMusicEntityList(){
        return AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();
    }
}
