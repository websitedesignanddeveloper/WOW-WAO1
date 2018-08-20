package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CommentLIstItem {

    @SerializedName("userData")
    private UserData userData;

    @SerializedName("reply")
    private ArrayList<ReplyCommentListItem> reply;

    @SerializedName("comment")
    private String comment;

    @SerializedName("id")
    private String id;

    @SerializedName("cmnt_time")
    private String cmntTime;

    @SerializedName("cmnt_date")
    private String cmntDate;

    @SerializedName("total_reply")
    private String totalReply;

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCmntTime(String cmntTime) {
        this.cmntTime = cmntTime;
    }

    public String getCmntTime() {
        return cmntTime;
    }

    public void setCmntDate(String cmntDate) {
        this.cmntDate = cmntDate;
    }

    public String getCmntDate() {
        return cmntDate;
    }

    public void setTotalReply(String totalReply) {
        this.totalReply = totalReply;
    }

    public String getTotalReply() {
        return totalReply;
    }

    public ArrayList<ReplyCommentListItem> getReply() {
        return reply;
    }

    public void setReply(ArrayList<ReplyCommentListItem> reply) {
        this.reply = reply;
    }
}