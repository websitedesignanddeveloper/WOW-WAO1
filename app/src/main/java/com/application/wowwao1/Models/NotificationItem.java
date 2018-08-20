package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class NotificationItem {

	@SerializedName("noti_type")
	private String notiType;

	@SerializedName("read_status")
	private String readStatus;

	@SerializedName("id")
	private String id;

	@SerializedName("noti_message")
	private String notiMessage;

	@SerializedName("userData")
	private UserData userData;

	public void setNotiType(String notiType){
		this.notiType = notiType;
	}

	public String getNotiType(){
		return notiType;
	}

	public void setReadStatus(String readStatus){
		this.readStatus = readStatus;
	}

	public String getReadStatus(){
		return readStatus;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setNotiMessage(String notiMessage){
		this.notiMessage = notiMessage;
	}

	public String getNotiMessage(){
		return notiMessage;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}