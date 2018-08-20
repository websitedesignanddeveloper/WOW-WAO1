package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class FeedsImageItem {

    @SerializedName("path")
    private String path;

    @SerializedName("id")
    private String id;

    public FeedsImageItem(String id, String path) {
        this.id = id;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}