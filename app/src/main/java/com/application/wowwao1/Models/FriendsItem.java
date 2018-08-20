package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class FriendsItem {

	@SerializedName("phone_no")
	private String phoneNo;

	@SerializedName("country")
	private String country;

	@SerializedName("email_verified")
	private String emailVerified;

	@SerializedName("gender")
	private String gender;

	@SerializedName("city")
	private String city;

	@SerializedName("stateID")
	private String stateID;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("cityID")
	private String cityID;

	@SerializedName("countryID")
	private String countryID;

	@SerializedName("profile_img")
	private String profileImg;

	@SerializedName("phone_verified")
	private String phoneVerified;

	@SerializedName("dob")
	private String dob;

	@SerializedName("id")
	private String id;

	@SerializedName("state")
	private String state;

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

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
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

	public void setCity(String city){
		this.city = city;
	}

	public String getCity(){
		return city;
	}

	public void setStateID(String stateID){
		this.stateID = stateID;
	}

	public String getStateID(){
		return stateID;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setCityID(String cityID){
		this.cityID = cityID;
	}

	public String getCityID(){
		return cityID;
	}

	public void setCountryID(String countryID){
		this.countryID = countryID;
	}

	public String getCountryID(){
		return countryID;
	}

	public void setProfileImg(String profileImg){
		this.profileImg = profileImg;
	}

	public String getProfileImg(){
		return profileImg;
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

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
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