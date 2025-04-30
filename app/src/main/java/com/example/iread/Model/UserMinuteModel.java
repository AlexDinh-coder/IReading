package com.example.iread.Model;

import java.time.DayOfWeek;

public class UserMinuteModel {
    private String username;
    private int readMinute;

    private int listenMinute;

    private DayOfWeek dayOfWeek;

    private String dayOfWeekStr;

    private String day;

    private boolean isToday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReadMinute() {
        return readMinute;
    }

    public void setReadMinute(int readMinute) {
        this.readMinute = readMinute;
    }

    public int getListenMinute() {
        return listenMinute;
    }

    public void setListenMinute(int listenMinute) {
        this.listenMinute = listenMinute;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfWeekStr() {
        return dayOfWeekStr;
    }

    public void setDayOfWeekStr(String dayOfWeekStr) {
        this.dayOfWeekStr = dayOfWeekStr;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}
