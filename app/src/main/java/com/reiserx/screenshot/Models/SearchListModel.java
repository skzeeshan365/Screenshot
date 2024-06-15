package com.reiserx.screenshot.Models;

public class SearchListModel {
    String name;
    int type, id, imageCount;

    public SearchListModel(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public SearchListModel(String name, int id, int imageCount, int type) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.imageCount = imageCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
