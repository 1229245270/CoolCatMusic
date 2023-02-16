package com.hzc.coolcatmusic.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Font {

    @Id
    private Long id;
    private String name;
    private String path;
    private String examplePath;

    private String localFile;


    @Generated(hash = 458731363)
    public Font(Long id, String name, String path, String examplePath,
            String localFile) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.examplePath = examplePath;
        this.localFile = localFile;
    }

    @Generated(hash = 1833324333)
    public Font() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExamplePath() {
        return examplePath;
    }

    public void setExamplePath(String examplePath) {
        this.examplePath = examplePath;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }
}
