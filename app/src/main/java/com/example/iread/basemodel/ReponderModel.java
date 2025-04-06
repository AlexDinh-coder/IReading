package com.example.iread.basemodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReponderModel<T> {
    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("isSussess")
    private boolean isSussess;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data;

    @SerializedName("dataList")
    private List<T> dataList;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isSussess() {
        return isSussess;
    }

    public void setSussess(boolean sussess) {
        isSussess = sussess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
