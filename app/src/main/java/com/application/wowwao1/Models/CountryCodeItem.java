package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class CountryCodeItem {

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("country_name")
    private String countryName;

    @SerializedName("phone_code")
    private String phoneCode;

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    @Override
    public String toString() {
        return countryName + " (" + phoneCode + ")";
    }
}