package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ComposeMsgPojo {

    @SerializedName("total_records")
    private int totalRecords;

    @SerializedName("total_page")
    private int totalPage;

    @SerializedName("message")
    private String message;

    @SerializedName("current_page")
    private String currentPage;

    @SerializedName("data")
    private ArrayList<UserData> data;

    @SerializedName("status")
    private boolean status;

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public ArrayList<UserData> getData() {
        return data;
    }

    public void setData(ArrayList<UserData> data) {
        this.data = data;
    }
}