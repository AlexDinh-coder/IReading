package com.example.iread.Interface;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImgurApi {
    @Multipart
    @POST("3/image")
    Call<JsonObject> uploadImage(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image
    );
}
