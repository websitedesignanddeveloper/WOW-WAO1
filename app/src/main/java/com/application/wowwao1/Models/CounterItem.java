package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class CounterItem {

	@SerializedName("count_message")
	private String countMessage;

	@SerializedName("count_notification")
	private String countNotification;

	@SerializedName("count_request")
	private String countRequest;

	public void setCountMessage(String countMessage){
		this.countMessage = countMessage;
	}

	public String getCountMessage(){
		return countMessage;
	}

	public void setCountNotification(String countNotification){
		this.countNotification = countNotification;
	}

	public String getCountNotification(){
		return countNotification;
	}

	public void setCountRequest(String countRequest){
		this.countRequest = countRequest;
	}

	public String getCountRequest(){
		return countRequest;
	}
}