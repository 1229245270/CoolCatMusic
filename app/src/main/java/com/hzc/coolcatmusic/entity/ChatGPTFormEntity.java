package com.hzc.coolcatmusic.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.DaoException;

@Entity
public class ChatGPTFormEntity {
    @Id(autoincrement = true)
    private Long id;
    private long createDate;
    private String name;
    private String image;
    @ToMany(referencedJoinProperty = "chatForm")
    private List<ChatGPTEntity> list;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1004282017)
    private transient ChatGPTFormEntityDao myDao;
    @Generated(hash = 1375729353)
    public ChatGPTFormEntity(Long id, long createDate, String name, String image) {
        this.id = id;
        this.createDate = createDate;
        this.name = name;
        this.image = image;
    }
    @Generated(hash = 1758894842)
    public ChatGPTFormEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getCreateDate() {
        return this.createDate;
    }
    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1528084471)
    public List<ChatGPTEntity> getList() {
        if (list == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChatGPTEntityDao targetDao = daoSession.getChatGPTEntityDao();
            List<ChatGPTEntity> listNew = targetDao
                    ._queryChatGPTFormEntity_List(id);
            synchronized (this) {
                if (list == null) {
                    list = listNew;
                }
            }
        }
        return list;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 589833612)
    public synchronized void resetList() {
        list = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImage() {
        return this.image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 618065075)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getChatGPTFormEntityDao() : null;
    }
    
}
