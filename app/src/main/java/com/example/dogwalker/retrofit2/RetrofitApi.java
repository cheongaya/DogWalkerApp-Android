package com.example.dogwalker.retrofit2;

import com.example.dogwalker.data.DogDTO;
import com.example.dogwalker.data.WalkerDTO;
import com.example.dogwalker.data.WalkerlistDTO;
import com.example.dogwalker.retrofit2.response.BookingServiceDTO;
import com.example.dogwalker.retrofit2.response.NonServiceDateDTO;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.ResultStrDTO;
import com.example.dogwalker.retrofit2.response.UserOwnerDTO;
import com.example.dogwalker.retrofit2.response.WalkPriceDTO;
import com.example.dogwalker.retrofit2.response.WalkableTypeDTO;
import com.example.dogwalker.retrofit2.response.WalkerLocationDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RetrofitApi {

//    public static String BASE_URL = "http://192.168.179.129/";
    public static String BASE_URL = "http://13.125.0.82/";

    //공통
    //회원가입 (아이디/비밀번호/이름/전화번호)
    @FormUrlEncoded
    @POST("common/join_user.php")
    Call<ResultDTO> joinUser(@FieldMap HashMap<String, Object> parameters);
    //로그인
    @FormUrlEncoded
    @POST("common/login_user.php")
    Call<ResultDTO> loginUser(@FieldMap HashMap<String, Object> parameters);
    //회원가입 아이디 중복체크
    @GET("common/join_duplicate_id_check.php")
    Call<ResultStrDTO> duplicateIdCheck(@Query("tableName") String tableName, @Query("editId") String editId);

    //도그워커 관련 API

    //도그워커 이미지 저장
    @Multipart
    @POST("common/insert_walker_image.php")
    Call<ResultDTO> updateWalkerImageData(@Query("id") String id, @Part MultipartBody.Part file);
    //서비스위치 데이터 수정 (도그워커 아이디 / 주소 / 위도 / 경도)
    @GET("walker/update_location.php")
    Call<ResultDTO> updateWalkerLocation(@Query("id") String id, @Query("location") String location, @Query("latitude") String latitude, @Query("longitude") String longitude);
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
    //서비스불가날짜 저장
    @FormUrlEncoded
    @POST("walker/insert_walker_non_service_date.php")
    Call<ResultDTO> insertWalkerNonServiceDates(@FieldMap HashMap<String, Object> parameters);
    //서비스불가날짜 조회
    @GET("walker/select_walker_non_service_date.php")
    Call<List<NonServiceDateDTO>> selectWalkerNonServiceDates(@Query("walker_id") String walker_id);
    //서비스불가날짜 해제 (삭제)
    @GET("walker/delete_walker_non_service_data.php")
    Call<ResultDTO> deleteWalkerNonServiceDates(@Query("walker_id") String walker_id, @Query("date") String date);
    //도그워커 위치 데이터 조회
    @GET("owner/select_walker_location_data.php")
    Call<List<WalkerLocationDTO>> selectWalkerLocation(@Query("owner_location") String owner_location);

//    @POST("walker/update_walker_tb.php")
//    Call<ResultDTO> updateWalkerData(@Query("tableName") String tableName,
//                                     @Query("updateCol") String updateCol, @Query("updateVal") String updateVal,
//                                     @Query("primaryCol") String primaryCol, @Query("primaryVal") String primaryVal);
    //반려인 관련 API

    //강아지 정보 저장
//    @FormUrlEncoded
//    @POST("owner/insert_dog_data.php")
//    Call<ResultDTO> insertDogData(@FieldMap HashMap<String, Object> parameters);
    @FormUrlEncoded
    @POST("owner/insert_dog_data.php")
    Call<ResultDTO> insertDogData(@FieldMap HashMap<String, Object> parameters);
    //강아지 정보 조회
    @GET("owner/select_dog_data.php")
    Call<List<DogDTO>> selectDogData(@Query("owner_id") String owner_id);
    //강아지 정보 삭제
    @GET("owner/delete_dog_data.php")
    Call<ResultDTO> deleteDogData(@Query("owner_id") String owner_id, @Query("name") String name);
    //강아지 이미지 저장
    @Multipart
    @POST("common/insert_dog_image.php")
    Call<ResultDTO> updateDogImageData(@Query("id") String id, @Part MultipartBody.Part file);
    //반려인 정보 조회
    @GET("owner/select_owner_data.php")
    Call<List<UserOwnerDTO>> selectOwnerData(@Query("id") String id);
    //반려인 이미지 저장
    @Multipart
    @POST("common/insert_owner_image.php")
    Call<ResultDTO> updateOwnerImageData(@Query("id") String id, @Part MultipartBody.Part file);
    //도그워커 정보 조회
    @GET("walker/select_walkerlist_data.php")
    Call<List<WalkerlistDTO>> selectWalkerlistData(@Query("owner_id") String owner_id);

    //반려인이 -> 도그워커 정보(프로필에서 도그워커 한명의 상세정보) 조회
    @GET("owner/select_walker_profile_data.php")
    Call<List<WalkerDTO>> selectWalkerProfileData(@Query("walker_id") String walker_id);
    //예약 정보 저장
    @FormUrlEncoded
    @POST("owner/insert_booking_service_data.php")
    Call<ResultDTO> insertBookingServiceData(@FieldMap HashMap<String, Object> parameters);
    //예약 정보 조회 (도그워커)
    @GET("walker/select_booking_service_data.php")
    Call<List<BookingServiceDTO>> selectWalkerBookingServiceData(@Query("walker_id") String walker_id);
    //예약 정보 조회 (반려인)
    @GET("owner/select_booking_service_data.php")
    Call<List<BookingServiceDTO>> selectOwnerBookingServiceData(@Query("owner_id") String owner_id);
    //북마크 생성
    @GET("owner/insert_owner_bookmark.php")
    Call<ResultDTO> insertOwnerBookmark(@Query("bmk_user_id") String bmk_user_id, @Query("bmk_walker_id") String bmk_walker_id);
    //북마크 조회
    @GET("owner/select_owner_bookmark.php")
    Call<ResultDTO> selectOwnerBookmark(@Query("bmk_user_id") String bmk_user_id, @Query("bmk_walker_id") String bmk_walker_id);
    //북마크 삭제
    @GET("owner/delete_owner_bookmark.php")
    Call<ResultDTO> deleteOwnerBookmark(@Query("bmk_user_id") String bmk_user_id, @Query("bmk_walker_id") String bmk_walker_id);
    //산책 기록 저장 (다중 이미지 저장)
    @Multipart
    @POST("common/insert_walk_done_recode.php")
    Call<ResultDTO> insertWalkDoneRecodeData(@Part ArrayList<MultipartBody.Part> partArrayList);


//    @Multipart
//    @POST("common")
//    Call<ResultDTO> insertImage(@Header("Authorization") String authorization, @Part("file\"; filename=\"pp.png\" ") RequestBody file ,
//                              @Part("FirstName") RequestBody fname, @Part("Id") RequestBody id);
}
