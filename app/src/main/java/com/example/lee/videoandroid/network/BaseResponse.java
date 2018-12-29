package com.example.lee.videoandroid.network;

import java.io.Serializable;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class BaseResponse<T> implements Serializable {
    public int code;
    public T data;
    public String message;
}
