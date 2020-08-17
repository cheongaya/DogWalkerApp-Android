package com.example.dogwalker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.dogwalker.owner.OwnerLoginActivity;
import com.example.dogwalker.owner.OwnerMypageActivity;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerLoginActivity;
import com.example.dogwalker.walker.WalkerScheduleActivity;

public class SplashActivity extends BaseActivity {

    String autoLoginIDStr, autoLoginPWStr, autoLoginTypeStr;
    Boolean autoLoginCheckBol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        //액션바 숨기기
//        getSupportActionBar().hide();

        //쉐어드에서 자동로그인 데이터 불러오기
        autoLoginIDStr = applicationClass.mySharedPref.getStringPref("autoLoginID");
        autoLoginPWStr = applicationClass.mySharedPref.getStringPref("autoLoginPW");
        autoLoginTypeStr = applicationClass.mySharedPref.getStringPref("autoLoginType");
        autoLoginCheckBol = applicationClass.mySharedPref.getBooleanPref("autoLoginCheck");

        applicationClass.currentWalkerID = autoLoginIDStr;
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자동 로그인한 아이디(ApplicationClassID) : " + applicationClass.currentWalkerID);

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자동로그인 autoLoginIDStr : " + autoLoginIDStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자동로그인 autoLoginPWStr : " + autoLoginPWStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자동로그인 autoLoginTypeStr : " + autoLoginTypeStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "자동로그인 autoLoginCheckBol : " + autoLoginCheckBol);

        //액티비티 3초 지속 후 로그인 액티비티로 이동하는 메소드
        startLoading();

    }

    //3초 후 로그인액티비티로 넘어가는 메소드
    public void startLoading(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //WALKER 자동로그인
                if(autoLoginIDStr != "" && autoLoginPWStr != "" && autoLoginCheckBol == true && autoLoginTypeStr.contentEquals("walker")){
                    //자동로그인 O -> 홈 화면으로 전환
                    Intent intent = new Intent(SplashActivity.this, WalkerScheduleActivity.class);
                    startActivity(intent);
                    finish();

                    makeToast("walker 자동로그인 성공");

                //OWNER 자동로그인
                }else if(autoLoginIDStr != "" && autoLoginPWStr != "" && autoLoginCheckBol == true && autoLoginTypeStr.contentEquals("owner")){
                    //자동로그인 O -> 홈 화면으로 전환
                    Intent intent = new Intent(SplashActivity.this, OwnerMypageActivity.class);
                    startActivity(intent);
                    finish();

                    makeToast("owner 자동로그인 성공");
                }else{
                    //자동로그인 X -> 로그인 화면으로 전환
                    Intent intent = new Intent(SplashActivity.this, OwnerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);
    }
}