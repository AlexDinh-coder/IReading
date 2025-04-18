package com.example.iread.basemodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewPublishedChapterModel implements Serializable {
    private String id;
    private String name;
    @SerializedName("newPulishedDateTime")
    private String newPublishedDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewPublishedDateTime() {
        return newPublishedDateTime;
    }

    public void setNewPublishedDateTime(String newPublishedDateTime) {
        this.newPublishedDateTime = newPublishedDateTime;
    }
}
