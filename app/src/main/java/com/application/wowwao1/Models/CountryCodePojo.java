package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryCodePojo{

	@SerializedName("data")
	private List<CountryCodeItem> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(List<CountryCodeItem> data){
		this.data = data;
	}

	public List<CountryCodeItem> getData(){
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