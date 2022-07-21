package com.hzc.coolCatMusic.entity;

public class Font {
    private Long id;
    private String name;
    private String path;
    private String examplePath;

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
}
