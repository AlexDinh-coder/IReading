package com.example.iread.basemodel;

import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;

import java.util.ArrayList;
import java.util.List;

public class BookModel {
    Book book;
    List<BookChapter> bookChapters = new ArrayList<BookChapter>();

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<BookChapter> getBookChapters() {
        return bookChapters;
    }

    public void setBookChapters(List<BookChapter> bookChapters) {
        this.bookChapters = bookChapters;
    }
}
