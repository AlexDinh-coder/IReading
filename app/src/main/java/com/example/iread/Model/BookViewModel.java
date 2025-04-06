package com.example.iread.Model;

public class BookViewModel {
    private int id;
    private int bookId;
    private String chapterId;
    //0: open, 1: close
    private int status;
    //0: doc, 1: noi
    private int bookTypeStatus;
    private String createBy;
    private String userId;
    private String modifyDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBookTypeStatus() {
        return bookTypeStatus;
    }

    public void setBookTypeStatus(int bookTypeStatus) {
        this.bookTypeStatus = bookTypeStatus;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
