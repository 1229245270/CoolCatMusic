package com.hzc.coolcatmusic.entity;

import java.util.List;

public class ExpandedTabEntity<T> {
    private String title;
    private String tip;
    private List<T> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

}
