package com.example.salesbuddy.service;

import com.example.salesbuddy.model.Sale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceNumberSale {
    @POST("numbersale")
    Call<Sale> numberSale(@Body RequestBody object);
}
