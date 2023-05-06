package com.hzc.coolcatmusic.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class ChatGPTEntity {

    @Id(autoincrement = true)
    private Long id;
    private long chatForm;
    private String role;
    private String content;
    private long createDate;
    @Generated(hash = 1934704915)
    public ChatGPTEntity(Long id, long chatForm, String role, String content,
            long createDate) {
        this.id = id;
        this.chatForm = chatForm;
        this.role = role;
        this.content = content;
        this.createDate = createDate;
    }
    @Generated(hash = 222503186)
    public ChatGPTEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getChatForm() {
        return this.chatForm;
    }
    public void setChatForm(long chatForm) {
        this.chatForm = chatForm;
    }
    public String getRole() {
        return this.role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    
}
