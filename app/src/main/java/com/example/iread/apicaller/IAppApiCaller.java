package com.example.iread.apicaller;


import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.Category;
import com.example.iread.basemodel.ReponderModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IAppApiCaller {
    @GET("Book/GetCategories")
        Call<ReponderModel<Category>> getCategories();
    @GET("Book/GetAllBookByUser")
    Call<ReponderModel<Book>> getBooks(
            @Query("userName") String userName
    );

    @GET("Book/GetBook")
    Call<ReponderModel<Book>> getBookById(
            @Query("id") int bookId
    );

    @GET("Book/GetAllBookByCategory")
    Call<ReponderModel<Book>> getBookByCategory(
            @Query("category") String categoryName
    );

    @GET("Book/GetListBookChapter")
    Call<ReponderModel<BookChapter>> getListByBookId(
            @Query("bookId") int listBookId
    );
}
