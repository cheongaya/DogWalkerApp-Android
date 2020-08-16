package com.example.dogwalker.retrofit;

import com.example.dogwalker.retrofit.RequestBody.RequestPut;
import com.example.dogwalker.retrofit.ResponseBody.ResponseGet;
import com.example.dogwalker.retrofit.ResponseBody.UserDTO;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {

//    final String Base_URL = "http://jsonplaceholder.typicode.com";
    final String Base_URL = "http://192.168.179.129/";

    @GET("/posts/{userId}")
    Call<ResponseGet> getFirst(@Path("userId") String id);

//    @GET("/posts")
//    Call<List<ResponseGet>> getSecond(@Query("userId") String id);

    //login.php?idx=?
    @GET("/login.php")
    Call<List<ResponseGet>> getSecond(@Query("id") String id);

//    @FormUrlEncoded
//    @POST("/posts") //{title=foo, body=bar, userId=1}
//    Call<ResponseGet> postFirst(@FieldMap HashMap<String, Object> parameters);

    @FormUrlEncoded
    @POST("/join.php")
    Call<ResponseGet> postFirst(@FieldMap HashMap<String, Object> parameters);

//    @PUT("/posts/1")
//    Call<ResponseGet> putFirst(@Body RequestPut parameters);
//
//    @FormUrlEncoded
//    @PATCH("/posts/1")
//    Call<ResponseGet> patchFirst(@Field("title") String title);
//
//    @DELETE("/posts/1")
//    Call<ResponseBody> deleteFirst();

    /**
     * create my ApiService
     */
    @FormUrlEncoded
    @POST("/join_walker.php")
    Call<UserDTO> postUser(@FieldMap HashMap<String, Object> parameters);
}
