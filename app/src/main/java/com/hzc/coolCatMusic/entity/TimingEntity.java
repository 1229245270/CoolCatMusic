package com.hzc.coolCatMusic.entity;

public class TimingEntity {

    private long nowTime;
    private long timing;
    private boolean isTiming = false;

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public long getTiming() {
        return timing;
    }

    public void setTiming(long timing) {
        this.timing = timing;
    }

    public boolean isTiming() {
        return isTiming;
    }

    public void setTiming(boolean timing) {
        isTiming = timing;
    }
}
