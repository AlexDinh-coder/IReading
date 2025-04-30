package com.example.iread.OpenBook;

import com.example.iread.Model.BookChapter;

import java.util.List;

public class ChapterDataHolder {
    private static volatile ChapterDataHolder instance;
    private List<BookChapter> chapterList;

    private ChapterDataHolder() {}

    public static ChapterDataHolder getInstance() {
        if (instance == null) {
            synchronized (ChapterDataHolder.class) {
                if (instance == null) {
                    instance = new ChapterDataHolder();
                }
            }
        }
        return instance;
    }

    public void setChapterList(List<BookChapter> chapters) {
        this.chapterList = chapters;
    }

    public List<BookChapter> getChapterList() {
        return chapterList;
    }
}
