package com.example.salesbuddy.service;

import com.example.salesbuddy.model.Sale;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceSendEmail {
    @POST("envio/{id}")
    Call<Sale> sendEmail(@Path("id") int saleId);
}
