package com.example.iread.Model;

public class UserProfile {
    private String userName;
    private String fullName;
    private String avatar;
    private String paymentName;
    private String createDate;

    private String expireDate;

    private long clamPoint;

    private long minuteRead;
    private long minuteListen;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public long getClamPoint() {
        return clamPoint;
    }

    public void setClamPoint(long claimPoint) {
        this.clamPoint = claimPoint;
    }

    public long getMinuteRead() {
        return minuteRead;
    }

    public void setMinuteRead(long minuteRead) {
        this.minuteRead = minuteRead;
    }

    public long getMinuteListen() {
        return minuteListen;
    }

    public void setMinuteListen(long minuteListen) {
        this.minuteListen = minuteListen;
    }

}
