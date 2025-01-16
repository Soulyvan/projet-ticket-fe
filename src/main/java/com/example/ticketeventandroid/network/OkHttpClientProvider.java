package com.example.ticketeventandroid.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpClientProvider {
    private static OkHttpClient client = null;

    public static OkHttpClient getClient(String token) {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();

                        // Ajout du token si présent
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Token " + token);
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    })
                    .build();
        }
        return client;
    }
}
