package com.example.iread.Model;

import com.example.iread.basemodel.NewPublishedChapterModel;

import java.util.ArrayList;
import java.util.List;

public class Book {

    private int id;
    private String name;

    private String summary;
    private String poster;
    private int bookType;
    //0: done , 4:da xuat ban
    private int status;
    private int ageLimitType;
    private String createDate;
    private String modifyDate;
    private String createBy;
    private int categoryId;
    private String userId;

    private String subCategory;

    private int price;

    private List<Category> listCategories = new ArrayList<>();

    private boolean isNewPublishedChapter;
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    private NewPublishedChapterModel newPublishedChapter;

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public List<Category> getListCategories() {
        return listCategories;
    }

    public void setListCategories(List<Category> listCategories) {
        this.listCategories = listCategories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPoster() {
        return poster;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAgeLimitType() {
        return ageLimitType;
    }

    public void setAgeLimitType(int ageLimitType) {
        this.ageLimitType = ageLimitType;
    }

    public boolean isNewPublishedChapter() {
        return isNewPublishedChapter;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setNewPublishedChapter(boolean newPublishedChapter) {
        isNewPublishedChapter = newPublishedChapter;
    }

    public NewPublishedChapterModel getNewPublishedChapter() {
        return newPublishedChapter;
    }

    public void setNewPublishedChapter(NewPublishedChapterModel newPublishedChapter) {
        this.newPublishedChapter = newPublishedChapter;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
