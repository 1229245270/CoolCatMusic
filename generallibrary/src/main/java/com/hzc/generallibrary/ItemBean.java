package com.hzc.generallibrary;

import android.os.Bundle;

/**
 * @author huangzhichao
 */
public class ItemBean {
    private String text;
    private String id;
    private String flag;
    private boolean isSelect = false;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
