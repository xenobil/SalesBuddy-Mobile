package com.example.salesbuddy.service;

import com.example.salesbuddy.model.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ServiceInterfaceAuthenticate {

    @Headers("Content-Type: application/json")
    @POST("login")
    Call<User> authenticateUser(@Body RequestBody requestBody);

    // Adicione outro método para autenticar com o token
    @Headers({"Content-Type: application/json", "Authorization: Bearer YourAuthToken"})
    @POST("login")
    Call<User> authenticateUserWithToken(RequestBody objectJson);
}
