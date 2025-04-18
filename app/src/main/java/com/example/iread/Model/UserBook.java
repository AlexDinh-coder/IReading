package com.example.iread.Model;

import com.example.iread.basemodel.UserBookType;

public class UserBook {
    private int id;
    private String userName;

    private String userId;
    private int bookId;
    private Account user;

    private Book book;
    private UserBookType bookType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserBookType getBookType() {
        return bookType;
    }

    public void setBookType(UserBookType bookType) {
        this.bookType = bookType;
    }
}
