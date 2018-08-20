package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class StateItem {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    public StateItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}