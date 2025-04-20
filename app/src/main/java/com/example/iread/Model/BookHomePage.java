package com.example.iread.Model;

import java.util.List;

public class BookHomePage {
    private int categoryId;
    private String categoryName;
    private List<Book> books;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBook(List<Book> book) {
        this.books = book;
    }
}
