package com.hzc.coolcatmusic.utils.DaoUtils;

import com.hzc.coolcatmusic.app.AppApplication;
import com.hzc.coolcatmusic.app.SPUtilsConfig;
import com.hzc.coolcatmusic.entity.Font;
import com.hzc.coolcatmusic.entity.PlayingMusicEntity;
import com.hzc.coolcatmusic.entity.PlayingMusicEntityDao;

import java.util.List;

import me.goldze.mvvmhabit.utils.SPUtils;

public class MusicUtils {

    /**
     * 获取当前播放歌曲
     * @return
     */
    public static PlayingMusicEntity getPlayingMusicEntity(){
        int playingNum = SPUtils.getInstance().getInt(SPUtilsConfig.PLAYING_NUM,0);
        List<PlayingMusicEntity> list = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();
        if(list != null && list.size() >= playingNum + 1) {
            return list.get(playingNum);
        }
        return null;
    }

    /**
     * 获取当前播放歌曲
     * @param src
     * @return
     */
    public static PlayingMusicEntity getPlayingMusicEntity(String src){
        List<PlayingMusicEntity> list = AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .where(PlayingMusicEntityDao.Properties.Src.eq(src))
                .list();
        if(list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取播放歌曲列表
     * @return
     */
    public static List<PlayingMusicEntity> getPlayingMusicEntityList(){
        return AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .queryBuilder()
                .list();
    }

    /**
     * 插入歌曲
     */
    public static void insertOrReplacePlayingMusicEntity(PlayingMusicEntity playingMusicEntity){
        AppApplication.daoSession
                .getPlayingMusicEntityDao()
                .insertOrReplace(playingMusicEntity);
    }

    /**
     * 插入歌曲
     */
    public static void deleteAllPlayingMusicEntity(){
        AppApplication.daoSession.getPlayingMusicEntityDao().deleteAll();
    }
}
