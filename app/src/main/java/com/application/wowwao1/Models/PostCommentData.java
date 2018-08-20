package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class PostCommentData {

	@SerializedName("userData")
	private UserData userData;

	@SerializedName("cmnt_id")
	private String cmntId;

	@SerializedName("cmnt_text")
	private String cmntText;

	@SerializedName("cmnt_date")
	private String cmntDate;

	public void setUserData(UserData userData){
		this.userData = userData;
	}

	public UserData getUserData(){
		return userData;
	}

	public void setCmntId(String cmntId){
		this.cmntId = cmntId;
	}

	public String getCmntId(){
		return cmntId;
	}

	public void setCmntText(String cmntText){
		this.cmntText = cmntText;
	}

	public String getCmntText(){
		return cmntText;
	}

	public void setCmntDate(String cmntDate){
		this.cmntDate = cmntDate;
	}

	public String getCmntDate(){
		return cmntDate;
	}
}