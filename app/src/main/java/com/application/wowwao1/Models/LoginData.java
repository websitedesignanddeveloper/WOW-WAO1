package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class LoginData {

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

    @SerializedName("country")
    private String country;

    @SerializedName("state")
    private String state;

    @SerializedName("city")
    private String city;

    @SerializedName("countryID")
    private String countryID;

    @SerializedName("stateID")
    private String stateID;

    @SerializedName("cityID")
    private String cityID;

    @SerializedName("dob")
    private String dob;

    @SerializedName("phone_code")
    private String phone_code;

    @SerializedName("city_lat")
    private String city_lat;

    @SerializedName("city_long")
    private String city_long;

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getEmailVerified() {
        return emailVerified;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setPhoneVerified(String phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getPhoneVerified() {
        return phoneVerified;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    public String getStateID() {
        return stateID;
    }

    public void setStateID(String stateID) {
        this.stateID = stateID;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public void setPhone_code(String phone_code) {
        this.phone_code = phone_code;
    }

    public String getCity_lat() {
        return city_lat;
    }

    public void setCity_lat(String city_lat) {
        this.city_lat = city_lat;
    }

    public String getCity_long() {
        return city_long;
    }

    public void setCity_long(String city_long) {
        this.city_long = city_long;
    }
}