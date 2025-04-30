package com.example.iread.basemodel;

import java.util.ArrayList;

public class BookChapterModel {
    private String id;
    private String chapterName;
    private int chapterNumber;
    private String summary;
    private String fileName;
    private String content;
    private ArrayList<SegmentModel> segmentWithTimes;
    private String audioUrl;
    private String modifyDate;
    private int bookType;
    private int price;
    private int priceVoice;
    private String createBy;
    private int bookId;
    private boolean isPaidChapter;
    private boolean isPaidVoice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public boolean isPaidVoice() {
        return isPaidVoice;
    }

    public void setPaidVoice(boolean paidVoice) {
        isPaidVoice = paidVoice;
    }

    public boolean isPaidChapter() {
        return isPaidChapter;
    }

    public void setPaidChapter(boolean paidChapter) {
        isPaidChapter = paidChapter;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public int getPriceVoice() {
        return priceVoice;
    }

    public void setPriceVoice(int priceVoice) {
        this.priceVoice = priceVoice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public ArrayList<SegmentModel> getSegmentWithTimes() {
        return segmentWithTimes;
    }

    public void setSegmentWithTimes(ArrayList<SegmentModel> segmentWithTimes) {
        this.segmentWithTimes = segmentWithTimes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
}
