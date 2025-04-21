package com.example.iread.Model;

import com.google.gson.annotations.SerializedName;

public class UserTranscationBookModel {
    private int id;
    private String paymentName;
    private int price;
    private String createDate;

    private String createDateFormat;

    private PaymentNameEnum paymentNameEnum;
    public enum PaymentNameEnum {
        @SerializedName("0")
        Deposit, // value = 0
        @SerializedName("1")
        Pay      // value = 1
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCreateDateFormat() {
        return createDateFormat;
    }

    public void setCreateDateFormat(String createDateFormat) {
        this.createDateFormat = createDateFormat;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public PaymentNameEnum getPaymentNameEnum() {
        return paymentNameEnum;
    }

    public void setPaymentNameEnum(PaymentNameEnum paymentNameEnum) {
        this.paymentNameEnum = paymentNameEnum;
    }
}
