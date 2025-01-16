package com.example.ticketeventandroid.network;

import com.example.ticketeventandroid.BaseActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// création de l'instance Retrofit et l'initialisation du service API

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = BaseActivity.getBASE_URL() + "/api/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInstanceWithAuth(String token) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientProvider.getClient(token)) // Client avec token
                .build();
    }
}
