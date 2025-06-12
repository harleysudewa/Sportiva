package com.example.sportiva;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.217.227:6969/";

    private static Retrofit noSessionRetrofit = null;
    private static Retrofit sessionRetrofit = null;

    private static final SessionCookieJar cookieJar = new SessionCookieJar();

    public static void clearCookies() {
        if (cookieJar != null) {
            cookieJar.clear();
        }
    }

    public static Retrofit getNoSessionClient() {
        if (noSessionRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();

            noSessionRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return noSessionRetrofit;
    }

    public static Retrofit getSessionClient() {
        if (sessionRetrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(cookieJar)
                    .build();

            sessionRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sessionRetrofit;
    }
}