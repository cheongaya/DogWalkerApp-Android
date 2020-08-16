package com.example.dogwalker.retrofit2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static Retrofit retrofit;
    private static RetrofitApi retrofitApi;

    public RetrofitUtil(){
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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
