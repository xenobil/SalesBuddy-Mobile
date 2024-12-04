package com.example.salesbuddy.service;

import com.example.salesbuddy.model.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("login")
    Call<User> autenticar(@Body RequestBody user);
}
