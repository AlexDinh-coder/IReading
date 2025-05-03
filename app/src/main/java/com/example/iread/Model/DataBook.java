package com.example.iread.Model;


public class DataBook {
    private String content; // uri or text
    private Boolean type; // 0 là uri, 1 là text

    private String chapterId;
    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }


    public DataBook(String content, Boolean type) {
        this.content = content;
        this.type = type;


    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}
