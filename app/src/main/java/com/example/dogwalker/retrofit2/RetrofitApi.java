package com.example.dogwalker.retrofit2;

import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.ResultStrDTO;
import com.example.dogwalker.retrofit2.response.UserOwnerDTO;
import com.example.dogwalker.retrofit2.response.WalkPriceDTO;
import com.example.dogwalker.retrofit2.response.WalkableTypeDTO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    public static String BASE_URL = "http://192.168.179.129/";

    //도그워커 관련 API

    //회원가입 (아이디/비밀번호/이름/전화번호)
    @FormUrlEncoded
    @POST("walker/join_walker.php")
    Call<ResultDTO> createUserWalker(@FieldMap HashMap<String, Object> parameters);
    //로그인
    @FormUrlEncoded
    @POST("walker/login_walker.php")
    Call<ResultDTO> loginUserWalker(@FieldMap HashMap<String, Object> parameters);
    //서비스위치 데이터 수정
    @GET("walker/update_location.php")
    Call<ResultDTO> updateWalkerLocation(@Query("id") String id, @Query("location") String location);
//    //서비스 위치 데이터 조회
//    @GET("walker/select_location.php")
//    Call<ResultStrDTO> selectWalkerLocation(@Query("id") String id);
    //자기소개 데이터 수정 (1개 칼럼 수정 가능)
    @FormUrlEncoded
    @POST("walker/update_walker_data.php")
    Call<ResultDTO> updateWalkerData(@FieldMap HashMap<String, Object> parameters);
    //자기소개 데이터 조회 (1개 칼럼 조회 가능)
    @GET("walker/select_walker_data.php")
    Call<ResultStrDTO> selectWalkerData(@Query("tableName") String tableName, @Query("selectCol") String selectCol, @Query("primaryCol") String primaryCol, @Query("primaryVal") String primaryVal);
    //user_walker 데이터 조회 (1개 칼럼 조회 가능)
    @GET("walker/select_walker_data_1column.php")
    Call<ResultDTO> selectWalkerData1Column(@Query("tableName") String tableName, @Query("selectCol") String selectCol, @Query("primaryCol") String primaryCol, @Query("primaryVal") String primaryVal);
    //산책가능 유형 데이터 수정 (3개 칼럼 수정 가능)
    @FormUrlEncoded
    @POST("walker/update_walker_data_3column.php")
    Call<ResultDTO> updateWalkerData3Column(@FieldMap HashMap<String, Object> parameters);
    //자기소개 데이터 조회 (1개 칼럼 조회 가능)
    @GET("walker/select_walker_data_3column.php")
    Call<WalkableTypeDTO> selectWalkerData3Column(@Query("tableName") String tableName, @Query("selectCol1") String selectCol1,
                                                  @Query("selectCol2") String selectCol2, @Query("selectCol3") String selectCol3,
                                                  @Query("primaryCol") String primaryCol, @Query("primaryVal") String primaryVal);
    //가격표 데이터 수정
    @FormUrlEncoded
    @POST("walker/update_walk_price.php")
    Call<ResultDTO> updateWalkPrice(@FieldMap HashMap<String, Object> parameters);
    //가격표 데이터 조회
    @GET("walker/select_walk_price.php")
    Call<WalkPriceDTO> selectWalkPrice(@Query("id") String id);

    //서비스불가날짜 저장 (3개 칼럼 저장 가능)
    @FormUrlEncoded
    @POST("walker/insert_walker_data_3column.php")
    Call<ResultDTO> insertWalkerData3Column(@FieldMap HashMap<String, Object> parameters);

//    @POST("walker/update_walker_tb.php")
//    Call<ResultDTO> updateWalkerData(@Query("tableName") String tableName,
//                                     @Query("updateCol") String updateCol, @Query("updateVal") String updateVal,
//                                     @Query("primaryCol") String primaryCol, @Query("primaryVal") String primaryVal);
    //반려인 관련 API
    @FormUrlEncoded
    @POST("/join_owner.php")
    Call<UserOwnerDTO> createUserOwner(@FieldMap HashMap<String, Object> parameters);
}
