package com.example.dogwalker.retrofit2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static Retrofit retrofit;
    private static RetrofitApi retrofitApi;

    public RetrofitUtil(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(1, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.SECONDS)
//                .writeTimeout(15, TimeUnit.SECONDS)
//                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_URL)
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        retrofitApi = retrofit.create(RetrofitApi.class);
    }

    public RetrofitApi getRetrofitApi(){
        return retrofitApi;
    }

//    public static Retrofit getApiClient(){
//        if(retrofit == null){
//            retrofit = new Retrofit.Builder()
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl(BASE_URL)
//                    .build();
//            }
//
//        return retrofit;
//    }
}
