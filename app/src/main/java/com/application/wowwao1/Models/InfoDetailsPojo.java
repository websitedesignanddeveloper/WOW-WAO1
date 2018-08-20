package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class InfoDetailsPojo{

	@SerializedName("data")
	private InfoDetailsData data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(InfoDetailsData data){
		this.data = data;
	}

	public InfoDetailsData getData(){
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