package com.example.iread.apicaller;


import com.example.iread.Model.Account;
import com.example.iread.Model.Book;
import com.example.iread.Model.BookChapter;
import com.example.iread.Model.BookViewModel;
import com.example.iread.Model.Category;
import com.example.iread.Model.CommentModel;
import com.example.iread.Model.PaymentRequestModel;
import com.example.iread.Payment.PaymentItemModel;
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

    @GET("Book/AddFavouriteBook")
    Call<ReponderModel<String>> addFavoriteBook(
            @Query("bookId") int bookId,
             @Query("username") String username

    );
    @GET("Payment/GetListPayment")
    Call<ReponderModel<PaymentItemModel>> getListPayment(
            @Query("type") int type
    );
    @POST("Payment/CreatePaymentLink")
    Call<ReponderModel<String>> createPaymentLink(
            @Body PaymentRequestModel paymentRequestModel
    );

    @POST("Account/LoginWithGoogle")
    Call<ReponderModel<String>> loginWithGoogle(
            @Body Account account
    );

    @GET("Book/Audio/{fileName}")
    Call<String> getAudioLink(
            @Query("fileName") String fileName
    );

    @GET("Book/SearchBook")
    Call<ReponderModel<BookChapter>> searchBook(
            @Query("type") int type
    );
    @GET("Book/GetBookChapterWithVoice")
    Call<ReponderModel<BookChapter>> getBookChapterWithVoice(
            @Query("chapterId") String chapterId
    );
}
