package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReplyCommentListPojo{

	@SerializedName("data")
	private List<ReplyCommentListItem> data;

	@SerializedName("total_records")
	private int totalRecords;

	@SerializedName("total_page")
	private int totalPage;

	@SerializedName("message")
	private String message;

	@SerializedName("current_page")
	private String currentPage;

	@SerializedName("status")
	private boolean status;

	public void setData(List<ReplyCommentListItem> data){
		this.data = data;
	}

	public List<ReplyCommentListItem> getData(){
		return data;
	}

	public void setTotalRecords(int totalRecords){
		this.totalRecords = totalRecords;
	}

	public int getTotalRecords(){
		return totalRecords;
	}

	public void setTotalPage(int totalPage){
		this.totalPage = totalPage;
	}

	public int getTotalPage(){
		return totalPage;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setCurrentPage(String currentPage){
		this.currentPage = currentPage;
	}

	public String getCurrentPage(){
		return currentPage;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ReplyCommentListPojo{" + 
			"data = '" + data + '\'' + 
			",total_records = '" + totalRecords + '\'' + 
			",total_page = '" + totalPage + '\'' + 
			",message = '" + message + '\'' + 
			",current_page = '" + currentPage + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}