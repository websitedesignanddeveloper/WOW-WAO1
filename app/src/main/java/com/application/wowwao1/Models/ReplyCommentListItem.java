package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class ReplyCommentListItem {

	@SerializedName("reply_time")
	private String replyTime;

	@SerializedName("id")
	private String id;

	@SerializedName("reply")
	private String reply;

	@SerializedName("reply_date")
	private String replyDate;

	@SerializedName("userData")
	private UserData userData;

	public void setReplyTime(String replyTime){
		this.replyTime = replyTime;
	}

	public String getReplyTime(){
		return replyTime;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setReply(String reply){
		this.reply = reply;
	}

	public String getReply(){
		return reply;
	}

	public void setReplyDate(String replyDate){
		this.replyDate = replyDate;
	}

	public String getReplyDate(){
		return replyDate;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}