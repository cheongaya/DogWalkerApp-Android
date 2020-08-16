package com.example.dogwalker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.dogwalker.retrofit2.RetrofitApi;
import com.example.dogwalker.retrofit2.RetrofitUtil;

public class BaseActivity extends AppCompatActivity {

    public static ApplicationClass applicationClass;
    private static final String TAG = "DeveloperLog";
    String className = getClass().getSimpleName().trim();

    public static RetrofitApi retrofitApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Log.d(TAG, className + "onCreate()");

        //ApplicationClass 객체 생성
        applicationClass = (ApplicationClass) getApplicationContext();
        //객체와 인터페이스 연결
        retrofitApi = new RetrofitUtil().getRetrofitApi();

    }

    //로그 : 액티비티명 + 함수명 + 원하는 데이터를 한번에 보기위한 로그
    public void makeLog(String methodData, String strData) {
        Log.d(TAG, className + "_" + methodData + "_" + strData);
    }

    //토스트메세지 : 귀찮음을 없애기 위해 토스트를 띄우는 함수를 만듦
    public void makeToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, className + "_onStart()");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, className + "-onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, className + "_onPause()");
    }

    @Override
    protected void onStop() {
        Log.d(TAG, className + "_onStop()");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, className + "_onRestart()");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, className + "_onDestroy()");
        super.onDestroy();
    }
}