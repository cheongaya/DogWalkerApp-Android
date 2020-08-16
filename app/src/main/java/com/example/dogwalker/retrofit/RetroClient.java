package com.example.dogwalker.retrofit;


import android.content.Context;
import android.util.Log;

import com.example.dogwalker.retrofit.ResponseBody.ResponseGet;
import com.example.dogwalker.retrofit.ResponseBody.UserDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private RetrofitAPI apiService;
    public static String baseUrl = RetrofitAPI.Base_URL;
    private static Context mContext;
    private static Retrofit retrofit;

    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    private static class SingletonHolder {
        private static RetroClient INSTANCE = new RetroClient(mContext);
    }

    public static RetroClient getInstance(Context context) {
        if (context != null) {
            mContext = context;
        }
        return SingletonHolder.INSTANCE;
    }

    //setRetrofitInit
    private RetroClient(Context context) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

//        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(baseUrl)
                    .build();
//        }

//        return retrofit;
    }

    public class NullOnEmptyConverterFactory extends Converter.Factory {

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            final Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
            return new Converter<ResponseBody, Object>() {
                @Override
                public Object convert(ResponseBody body) throws IOException {
                    if (body.contentLength() == 0) return null;
                    return delegate.convert(body);
                }
            };
        }
    }

    public RetroClient createBaseApi() {
        apiService = create(RetrofitAPI.class);
        return this;
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the {@code service} interface.
     */
    public  <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        return retrofit.create(service);
    }

    //.com/posts/1
    public void getFirst(String id, final RetroCallback callback) {
        apiService.getFirst(id).enqueue(new Callback<ResponseGet>() {
            @Override
            public void onResponse(Call<ResponseGet> call, Response<ResponseGet> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGet> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    //.com/posts?userId=2
    public void getSecond(String id, final RetroCallback callback) {
        apiService.getSecond(id).enqueue(new Callback<List<ResponseGet>>() {
            @Override
            public void onResponse(Call<List<ResponseGet>> call, Response<List<ResponseGet>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                } else {
                    callback.onFailure(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGet>> call, Throwable t) {
//                Log.d("cheonga", "get error");
                callback.onError(t);
            }
        });
    }

    //.com/posts
    public void postJoinUserWalker(HashMap<String, Object> parameters, final RetroCallback callback) {

        apiService.postUser(parameters).enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.code(), response.body());
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신성공 : " + "111");
                } else {
                    callback.onFailure(response.code());
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신실패 : " + "222");
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);

    }

}
