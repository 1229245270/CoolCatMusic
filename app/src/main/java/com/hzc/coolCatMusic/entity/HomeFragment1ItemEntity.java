package com.hzc.coolCatMusic.entity;

import android.graphics.drawable.Drawable;

public class HomeFragment1ItemEntity {
    private Drawable localImage;
    private String name;

    public HomeFragment1ItemEntity(Drawable localImage, String name) {
        this.localImage = localImage;
        this.name = name;
    }

    public Drawable getLocalImage() {
        return localImage;
    }

    public void setLocalImage(Drawable localImage) {
        this.localImage = localImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
