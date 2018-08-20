package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class ConversationItem {

    @SerializedName("attachment")
    private String attachment;

    @SerializedName("msg_date")
    private String msgDate;

    @SerializedName("receiver_id")
    private String receiverId;

    @SerializedName("id")
    private String id;

    @SerializedName("msg_time")
    private String msgTime;

    @SerializedName("message")
    private String message;

    @SerializedName("sender_id")
    private String senderId;

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

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
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

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }
}