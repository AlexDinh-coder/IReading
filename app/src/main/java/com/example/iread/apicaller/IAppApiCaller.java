package com.example.iread.apicaller;


import com.example.iread.Model.Account;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.Model.Category;
import com.example.iread.Model.CommentModel;
import com.example.iread.basemodel.ReponderModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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


    @POST("Account/Login")
    Call<ReponderModel<String>> login(
            @Body Account account
    );

    @GET("Book/GetCommentByBook")
    Call<ReponderModel<CommentModel>>listCommentBook(
            @Query("bookId") int bookId
    );

    @POST("Book/UpdateComment")
    Call<ReponderModel<Object>>commentBook(
            @Body CommentModel commentModel
    );
    @POST("Book/CreateViewBook")
    Call<ReponderModel<Integer>>createBookView(
            @Body BookViewModel bookViewModel
    );
    @GET("Book/GetViewNo")
    Call<ReponderModel<Integer>> totalViewBook(
            @Query("bookId") int bookId,
            @Query("bookTypeStatus") int statusType
    );

    @GET("/Book/AddFavouriteBook")
    Call<ReponderModel<String>> addFavoriteBook(
            @Query("") int bookId

    );
}
