package com.example.salesbuddy.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;            

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    //private static final String API_BASE_URL = "https://sales-buddy-15ec2be4a757.herokuapp.com/";
    private static final String API_BASE_URL = "http://192.168.124.19:3333/";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String AUTH_TOKEN_KEY = "AuthToken";

    private static Gson gson = new Gson();

    public static <S> S createService(Class<S> serviceClass, Context context) {
        // Recuperando a token JWT das SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString(AUTH_TOKEN_KEY, "");

        // Instancia do interceptador das requisições
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS);

        httpClient.addInterceptor(loggingInterceptor);

        // Adicione um interceptor para incluir o token de autenticação em todas as solicitações
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + authToken) // Adicione o token como cabeçalho de autorização
                    .method(original.method(), original.body());
            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        // Instância do retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        return retrofit.create(serviceClass);
    }

    public static void saveAuthToken(Context context, String authToken) {
        // Salvando a token JWT em SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("AuthToken", "Token antes de salvar: " + authToken);
        editor.putString(AUTH_TOKEN_KEY, authToken);
        editor.apply();
        Log.d("AuthToken", "Token após salvar: " + sharedPreferences.getString(AUTH_TOKEN_KEY, ""));
    }

    public static void saveAuthTokenFromSetCookie(Context context, String setCookieHeader) {
        // Extrair o valor do token do cabeçalho Set-Cookie
        String[] parts = setCookieHeader.split(";");
        String token = null;
        for (String part : parts) {
            if (part.trim().startsWith("token=")) {
                token = part.trim().substring("token=".length());
                break;
            }
        }

        // Salvar o token no SharedPreferences
        if (token != null) {
            saveAuthToken(context, token);
        }
    }

    public static Gson getGson() {
        return gson;
    }
}
