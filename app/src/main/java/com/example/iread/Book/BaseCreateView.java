package com.example.iread.Book;

import android.util.Log;

import com.example.iread.apicaller.IAppApiCaller;
import com.example.iread.basemodel.ReponderModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseCreateView {
//    public void getBookTotalReview(int bookId) {
//        iAppApiCaller.totalViewBook(bookId, 0).enqueue(new Callback<ReponderModel<Integer>>() {
//            @Override
//            public void onResponse(Call<ReponderModel<Integer>> call, Response<ReponderModel<Integer>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//
//                    int viewCount = response.body().getData();
//                    totalReadView.setText(viewCount + "");
//
//                }else {
//                    Log.d("APIView", "Khong co du lieu");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ReponderModel<Integer>> call, Throwable t) {
//                Log.e("APIView", "Lỗi API GetViewNo: " + t.getMessage());
//            }
//        });
//    }
}
