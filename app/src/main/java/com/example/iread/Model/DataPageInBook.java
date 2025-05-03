package com.example.iread.Model;

import java.util.List;

public class DataPageInBook {
    private int IdPage; // id Page

    private List<DataBook> Data; // data in page (uri and text)

    private BookChapter BookChapter;





    public DataPageInBook(int idPage, List<DataBook> data,BookChapter bookChapter ) {
        IdPage = idPage;
        Data = data;
        BookChapter = bookChapter;

    }
    public BookChapter getBookChapter() {
        return BookChapter;
    }

    public void setBookChapter(BookChapter bookChapter) {
        this.BookChapter = bookChapter;
    }



    public int getIdPage() {
        return IdPage;
    }

    public void setIdPage(int idPage) {
        IdPage = idPage;
    }

    public List<DataBook> getData() {
        return Data;
    }

    public void setData(List<DataBook> data) {
        Data = data;
    }
}
