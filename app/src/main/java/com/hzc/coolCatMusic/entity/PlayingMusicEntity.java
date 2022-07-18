package com.hzc.coolCatMusic.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PlayingMusicEntity {

    @Id(autoincrement = true)
    private Long id;

    //歌曲名
    private String songName;
    //歌手名
    private String singer;
    //全名
    private String allName;
    //歌手头像
    private String singerImage;
    //歌曲图片
    private String songImage;
    //歌词
    private String lyrics;
    //链接
    private String src;
    //发行年份
    private String yearIssue;
    //进度条
    private int duration;
    //歌曲时间
    private int current;
    //播放状态
    private boolean isPlay;

    @Generated(hash = 437746908)
    public PlayingMusicEntity(Long id, String songName, String singer,
            String allName, String singerImage, String songImage, String lyrics,
            String src, String yearIssue, int duration, int current,
            boolean isPlay) {
        this.id = id;
        this.songName = songName;
        this.singer = singer;
        this.allName = allName;
        this.singerImage = singerImage;
        this.songImage = songImage;
        this.lyrics = lyrics;
        this.src = src;
        this.yearIssue = yearIssue;
        this.duration = duration;
        this.current = current;
        this.isPlay = isPlay;
    }

    @Generated(hash = 1919123585)
    public PlayingMusicEntity() {
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAllName() {
        return allName;
    }

    public void setAllName(String allName) {
        this.allName = allName;
    }

    public String getSingerImage() {
        return singerImage;
    }

    public void setSingerImage(String singerImage) {
        this.singerImage = singerImage;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getYearIssue() {
        return yearIssue;
    }

    public void setYearIssue(String yearIssue) {
        this.yearIssue = yearIssue;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsPlay() {
        return this.isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    @Override
    public String toString() {
        return "PlayingMusicEntity{" +
                "id=" + id +
                ", songName='" + songName + '\'' +
                ", singer='" + singer + '\'' +
                ", allName='" + allName + '\'' +
                ", singerImage='" + singerImage + '\'' +
                ", songImage='" + songImage + '\'' +
                ", lyrics='" + lyrics + '\'' +
                ", src='" + src + '\'' +
                ", yearIssue='" + yearIssue + '\'' +
                ", duration=" + duration +
                ", current=" + current +
                ", isPlay=" + isPlay +
                '}';
    }
}
