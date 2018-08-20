package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class AccSettingDetailsData {

	@SerializedName("req_acpt")
	private String reqAcpt;

	@SerializedName("user_follow")
	private String userFollow;

	@SerializedName("post_cmnt")
	private String postCmnt;

	@SerializedName("post_like")
	private String postLike;

	@SerializedName("req_rece")
	private String reqRece;

	@SerializedName("user_msg")
	private String user_msg;

	public void setReqAcpt(String reqAcpt){
		this.reqAcpt = reqAcpt;
	}

	public String getReqAcpt(){
		return reqAcpt;
	}

	public void setUserFollow(String userFollow){
		this.userFollow = userFollow;
	}

	public String getUserFollow(){
		return userFollow;
	}

	public void setPostCmnt(String postCmnt){
		this.postCmnt = postCmnt;
	}

	public String getPostCmnt(){
		return postCmnt;
	}

	public void setPostLike(String postLike){
		this.postLike = postLike;
	}

	public String getPostLike(){
		return postLike;
	}

	public void setReqRece(String reqRece){
		this.reqRece = reqRece;
	}

	public String getReqRece(){
		return reqRece;
	}

	public String getUser_msg() {
		return user_msg;
	}

	public void setUser_msg(String user_msg) {
		this.user_msg = user_msg;
	}
}