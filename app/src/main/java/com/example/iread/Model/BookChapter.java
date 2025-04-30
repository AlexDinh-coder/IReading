package com.example.iread.Model;

import com.example.iread.basemodel.SegmentModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class BookChapter implements Serializable {
    private String id;
    private String chapterName;

    private int chapterNumber;
    private String summary;
    private String audioUrl;
    private String content;

    private List<SummaryTime> contentWithTime;

    private String modifyDate;
    //0: free, 1: paid; PendingApproval = 2,
    //        Decline = 3,
    private int bookType;

    private int price;

    private String createBy;

    private String userId;
    private int bookId;

    private String fileName;

    private int viewNo;

    private List<SegmentModel> segmentWithTime;

    private int priceVoice;

    private boolean isPaidChapter;
    private boolean isPaidVoice;


    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getViewNo() {
        return viewNo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setViewNo(int viewNo) {
        this.viewNo = viewNo;
    }

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



    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }


    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public List<SummaryTime> getContentWithTime() {
        return contentWithTime;
    }

    public void setContentWithTime(List<SummaryTime> contentWithTime) {
        this.contentWithTime = contentWithTime;
    }

    public List<SegmentModel> getSegmentWithTime() {
        return segmentWithTime;
    }

    public void setSegmentWithTime(List<SegmentModel> segmentWithTime) {
        this.segmentWithTime = segmentWithTime;
    }

    public int getPriceVoice() {
        return priceVoice;
    }

    public void setPriceVoice(int priceVoice) {
        this.priceVoice = priceVoice;
    }

    public boolean isPaidChapter() {
        return isPaidChapter;
    }

    public void setPaidChapter(boolean paidChapter) {
        isPaidChapter = paidChapter;
    }

    public boolean isPaidVoice() {
        return isPaidVoice;
    }

    public void setPaidVoice(boolean paidVoice) {
        isPaidVoice = paidVoice;
    }
}
