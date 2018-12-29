package com.example.lee.videoandroid.network;

import com.google.gson.JsonObject;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    public String HOST = "http://192.168.103.111:8080/";
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

    @POST(liveModule + "updateOrInsert")
    Observable<BaseResponse> updateOrInsert(@Body RequestBody body);

    @POST("/upload")
    Observable<BaseResponse> upload(@Part MultipartBody.Part multipart);

}
