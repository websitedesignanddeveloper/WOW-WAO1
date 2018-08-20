package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class AddReplyPojo {

	@SerializedName("data")
	private ReplyCommentListItem data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(ReplyCommentListItem data){
		this.data = data;
	}

	public ReplyCommentListItem getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}