package com.hzc.coolCatMusic.entity;

public class LookEntity {
    private Long id;
    private String titleImage;
    private String titleText;
    private String authorName;
    private String authorImage;
    private String time;
    private String lookTimes;
    private RecommendSong recommendSong;

    public static class RecommendSong{
        private String musicUrl;
        private String songName;
        private String singer;
        private long size;

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
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

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RecommendSong getRecommendSong() {
        return recommendSong;
    }

    public String getLookTimes() {
        return lookTimes;
    }

    public void setLookTimes(String lookTimes) {
        this.lookTimes = lookTimes;
    }

    public void setRecommendSong(RecommendSong recommendSong) {
        this.recommendSong = recommendSong;
    }
}
