package com.example.dogwalker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.dogwalker.owner.OwnerBookingActivity;
import com.example.dogwalker.owner.OwnerLoginActivity;
import com.example.dogwalker.owner.OwnerMypageActivity;
import com.example.dogwalker.retrofit2.response.ChatListDTO;
import com.example.dogwalker.walker.WalkerChatListActivity;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerLoginActivity;
import com.example.dogwalker.walker.WalkerScheduleActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    String autoLoginIDStr, autoLoginPWStr, autoLoginTypeStr;
    Boolean autoLoginCheckBol;

//    Intent serviceIntent; //채팅 Msg 서비스

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
                    Intent intent = new Intent(SplashActivity.this, WalkerDogwalkingActivity.class);
                    startActivity(intent);
                    finish();
                    //채팅 서비스 시작
                    startChatService(autoLoginIDStr);

                    makeToast("walker 자동로그인 성공");

                //OWNER 자동로그인
                }else if(autoLoginIDStr != "" && autoLoginPWStr != "" && autoLoginCheckBol == true && autoLoginTypeStr.contentEquals("owner")){
                    //자동로그인 O -> 홈 화면으로 전환
                    Intent intent = new Intent(SplashActivity.this, OwnerBookingActivity.class);
                    startActivity(intent);
                    finish();
                    //채팅 서비스 시작
                    startChatService(autoLoginIDStr);

                    makeToast("owner 자동로그인 성공");
                }else{
                    //자동로그인 X -> 로그인 화면으로 전환
                    Intent intent = new Intent(SplashActivity.this, WalkerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1000);
    }

    //채팅 서비스 시작
    //로그인한 유저의 채팅방 리스트 데이터 조회
    public void startChatService(String chatUser){

        Call<List<ChatListDTO>> call = retrofitApi.selectChatRoomList(chatUser);
        call.enqueue(new Callback<List<ChatListDTO>>() {
            @Override
            public void onResponse(Call<List<ChatListDTO>> call, Response<List<ChatListDTO>> response) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[자동로그인] 채팅방 리스트 데이터 조회 = 성공");
                List<ChatListDTO> chatListDTOList = response.body();    //List

                //도그워커 데이터 setText 해주기 (position 번째)
                if(chatListDTOList.size() > 0){

                    String roomArr = "";
                    for(int i=0; chatListDTOList.size() > i ; i++){
                        roomArr += chatListDTOList.get(i).getRoomNum()+"/";
                    }

                    //서비스를 시작시킨다 -> 소켓을 연결하고 조회한 방 번호들을 모두 보낸다
                    ApplicationClass.serviceIntent = new Intent(SplashActivity.this, MsgService.class);
//                    serviceIntent.putExtra("command", "socket");
                    ApplicationClass.serviceIntent.putExtra("status", "connect");
                    ApplicationClass.serviceIntent.putExtra("chatUser", chatUser);
                    ApplicationClass.serviceIntent.putExtra("roomArr", roomArr);
                    startService(ApplicationClass.serviceIntent); //서비스를 실행하는 함수

                }else{
                    //검색된 도그워커 데이터가 없을때
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[자동로그인] 채팅방 리스트 데이터 조회 == null");

                }

            }

            @Override
            public void onFailure(Call<List<ChatListDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[자동로그인] 채팅방 리스트 데이터 조회 = 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "[자동로그인] t.toString() : " + t.toString());

            }
        });

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //서비스 종료
//        stopService(serviceIntent); //서비스를 중단시키는 함수
//    }
}