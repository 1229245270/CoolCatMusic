package com.hzc.coolcatmusic.event;

import com.hzc.coolcatmusic.entity.ChatGPTFormEntity;

import java.util.List;

public class ChatGPTEntityFormEvent {
    private List<ChatGPTFormEntity> list;

    public List<ChatGPTFormEntity> getList() {
        return list;
    }

    public void setList(List<ChatGPTFormEntity> list) {
        this.list = list;
    }
}
