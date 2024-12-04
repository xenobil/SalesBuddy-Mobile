package com.example.salesbuddy.service;

import com.example.salesbuddy.model.Sale;
import com.example.salesbuddy.model.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceInterfaceRegisterSale {
    @POST("registersales")
    Call<Sale> registerSalePortal(@Body RequestBody object);
}
