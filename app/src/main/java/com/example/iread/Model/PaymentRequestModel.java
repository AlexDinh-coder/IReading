package com.example.iread.Model;

import com.example.iread.Payment.PaymentItemModel;
import com.example.iread.basemodel.PaymentItem;

import java.util.ArrayList;
import java.util.List;

public class PaymentRequestModel {
    private int amount;
    private String description;
    private String domain;
    private List<PaymentItem> items= new ArrayList<>();

    private String buyerEmail;
    private int type;
    private String paymentKey;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<PaymentItem> getItems() {
        return items;
    }

    public void setItems(List<PaymentItem> items) {
        this.items = items;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
}
