package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class MsgListItem {

    @SerializedName("attachment")
    private String attachment;

    @SerializedName("msg_date")
    private String msgDate;

    @SerializedName("read_status")
    private String readStatus;

    @SerializedName("id")
    private String id;

    @SerializedName("msg_time")
    private String msgTime;

    @SerializedName("message")
    private String message;

    @SerializedName("read_count")
    private String readCount;

    @SerializedName("userData")
    private UserData userData;

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    public String getReadStatus() {
        return readStatus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setReadCount(String readCount) {
        this.readCount = readCount;
    }

    public String getReadCount() {
        return readCount;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}