package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedsItem {

    @SerializedName("image")
    private List<FeedsImageItem> image;

    @SerializedName("share_user_id")
    private String shareUserId;

    @SerializedName("total_like")
    private String totalLike;

    @SerializedName("userData")
    private UserData userData;

    @SerializedName("share_post_text")
    private String sharePostText;

    @SerializedName("share_post_date")
    private String share_post_date;

    @SerializedName("share_post_time")
    private String share_post_time;

    @SerializedName("total_comment")
    private String totalComment;

    @SerializedName("share_userData")
    private SharedUserData shareUserData;

    @SerializedName("post_text")
    private String postText;

    @SerializedName("post_time")
    private String postTime;

    @SerializedName("post_date")
    private String postDate;

    @SerializedName("post_type")
    private String postType;

    @SerializedName("id")
    private String id;

    @SerializedName("share_post_id")
    private String sharePostId;

    @SerializedName("is_liked")
    private String isLiked;

    @SerializedName("is_reported")
    private String is_reported;

    @SerializedName("diff")
    private String diff;

    public void setImage(List<FeedsImageItem> image) {
        this.image = image;
    }

    public List<FeedsImageItem> getImage() {
        return image;
    }

    public void setShareUserId(String shareUserId) {
        this.shareUserId = shareUserId;
    }

    public String getShareUserId() {
        return shareUserId;
    }

    public void setTotalLike(String totalLike) {
        this.totalLike = totalLike;
    }

    public String getTotalLike() {
        return totalLike;
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }


    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setSharePostText(String sharePostText) {
        this.sharePostText = sharePostText;
    }

    public String getSharePostText() {
        return sharePostText;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public void setShareUserData(SharedUserData shareUserData) {
        this.shareUserData = shareUserData;
    }

    public SharedUserData getShareUserData() {
        return shareUserData;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostType() {
        return postType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setSharePostId(String sharePostId) {
        this.sharePostId = sharePostId;
    }

    public String getSharePostId() {
        return sharePostId;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public String getShare_post_date() {
        return share_post_date;
    }

    public void setShare_post_date(String share_post_date) {
        this.share_post_date = share_post_date;
    }

    public String getShare_post_time() {
        return share_post_time;
    }

    public void setShare_post_time(String share_post_time) {
        this.share_post_time = share_post_time;
    }

    public String getIs_reported() {
        return is_reported;
    }

    public void setIs_reported(String is_reported) {
        this.is_reported = is_reported;
    }
}