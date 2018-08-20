package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StatePojo{

	@SerializedName("data")
	private List<StateItem> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<StateItem> data){
		this.data = data;
	}

	public List<StateItem> getData(){
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