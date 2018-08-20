package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class SendMessagePojo{

	@SerializedName("message")
	private String message;

	@SerializedName("data")
	private ConversationItem data;

	@SerializedName("status")
	private boolean status;

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

	public ConversationItem getData() {
		return data;
	}

	public void setData(ConversationItem data) {
		this.data = data;
	}
}