package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nct119 on 14/9/17.
 */

public class SearchItem {

    @SerializedName("user")
    private ArrayList<UserData> user;

    @SerializedName("post")
    private ArrayList<FeedsItem> post;

    public ArrayList<UserData> getUser() {
        return user;
    }

    public void setUser(ArrayList<UserData> user) {
        this.user = user;
    }

    public ArrayList<FeedsItem> getPost() {
        return post;
    }

    public void setPost(ArrayList<FeedsItem> post) {
        this.post = post;
    }
}
