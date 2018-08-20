package com.application.wowwao1.Models;


import com.google.gson.annotations.SerializedName;

public class PhoneVerificationData {

    @SerializedName("code")
    private String code;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}