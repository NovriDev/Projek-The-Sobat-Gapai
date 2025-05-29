package com.example.the_sobat_gapai.Retrofit;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static final String BASE_URL = "http://<ganti dengan IPLOCAL / Change to You IPLOCAL>:<port>/";
    // example IPLocal and Port = http://192.100.10.1:8080/

    public static Retrofit getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}