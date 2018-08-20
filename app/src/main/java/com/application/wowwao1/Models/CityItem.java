package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class CityItem {

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private String id;

    @SerializedName("lat")
    private String lat;

    @SerializedName("long")
    private String jsonMemberLong;

    public CityItem(String id, String name) {
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

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setJsonMemberLong(String jsonMemberLong) {
        this.jsonMemberLong = jsonMemberLong;
    }

    public String getJsonMemberLong() {
        return jsonMemberLong;
    }

    @Override
    public String toString() {
        return name;
    }
}