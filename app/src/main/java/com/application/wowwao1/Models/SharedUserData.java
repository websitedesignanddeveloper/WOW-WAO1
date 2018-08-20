package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class SharedUserData {

	@SerializedName("phone_no")
	private String phoneNo;

	@SerializedName("profile_img")
	private String profileImg;

	@SerializedName("email_verified")
	private String emailVerified;

	@SerializedName("gender")
	private String gender;

	@SerializedName("phone_verified")
	private String phoneVerified;

	@SerializedName("dob")
	private String dob;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("id")
	private String id;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("email")
	private String email;

	@SerializedName("cover_img")
	private String coverImg;

	@SerializedName("age")
	private String age;

	public void setPhoneNo(String phoneNo){
		this.phoneNo = phoneNo;
	}

	public String getPhoneNo(){
		return phoneNo;
	}

	public void setProfileImg(String profileImg){
		this.profileImg = profileImg;
	}

	public String getProfileImg(){
		return profileImg;
	}

	public void setEmailVerified(String emailVerified){
		this.emailVerified = emailVerified;
	}

	public String getEmailVerified(){
		return emailVerified;
	}

	public void setGender(String gender){
		this.gender = gender;
	}

	public String getGender(){
		return gender;
	}

	public void setPhoneVerified(String phoneVerified){
		this.phoneVerified = phoneVerified;
	}

	public String getPhoneVerified(){
		return phoneVerified;
	}

	public void setDob(String dob){
		this.dob = dob;
	}

	public String getDob(){
		return dob;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setCoverImg(String coverImg){
		this.coverImg = coverImg;
	}

	public String getCoverImg(){
		return coverImg;
	}

	public void setAge(String age){
		this.age = age;
	}

	public String getAge(){
		return age;
	}
}