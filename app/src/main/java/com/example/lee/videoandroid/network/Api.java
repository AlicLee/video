package com.example.lee.videoandroid.network;

import android.support.v4.media.AudioAttributesCompat;

import com.google.gson.JsonObject;


import java.io.File;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

public interface Api {
    public String HOST = "http://192.168.103.111:8081/";
    public String userModule = "/user", liveModule = "/live";

    /**
     *
     */
    @POST(userModule + "/login")
    Observable<BaseResponse> requestLogin(@Body RequestBody loginMap);

    /**
     *
     */
    @POST(liveModule + "/getLiveByPages")
    Observable<BaseResponse> requestLiveByPages(@Query("pageIndex") int pageIndex, @Query("pageSize") int pageSize);

    @POST(userModule + "/register")
    Observable<BaseResponse> register(@Body RequestBody registerBody);

    @POST(liveModule + "/updateOrInsert")
    Observable<BaseResponse> updateOrInsert(@Body RequestBody body);

    @Multipart
    @POST("/upload")
    Call<BaseResponse> upload(@Part MultipartBody.Part multipart);

}
