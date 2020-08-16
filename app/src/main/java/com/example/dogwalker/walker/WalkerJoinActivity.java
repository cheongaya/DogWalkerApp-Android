package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.JoinAgreement01Activity;
import com.example.dogwalker.JoinAgreement02Activity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerJoinBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerJoinActivity extends BaseActivity {

    private ActivityWalkerJoinBinding binding;

    String userIDStr, userPWStr, userNameStr, userPhoneNumberStr;

//    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_join);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_join);
        binding.setActivity(this);
//        //액션바 숨기기
//        getSupportActionBar().hide();

    }

    //회원가입 아이디 중복환인 하는 메소드
    public void onDuplicateCheckID(View view){

    }

    //휴대전화 본인 인증하는 메소드
    public void onCertificationPhoneNumber(View view){

    }

    //회원가입 완료 메소드
    public void onJoinOK(View view){
//        Intent intent = new Intent(this, JoinAgreementActivity.class);
//        startActivity(intent);
//        makeToast("POST Clicked");

        //입력한 데이터를 -> String 변수에 담는다
        userIDStr = binding.editTextJoinID.getText().toString();
        userPWStr = binding.editTextJoinPW.getText().toString();
        userNameStr = binding.editTextName.getText().toString();
        userPhoneNumberStr = binding.editTextPhoneNumber.getText().toString();
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "확인 userIDStr : " + userIDStr);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", userIDStr);
        parameters.put("pw", userPWStr);
        parameters.put("name", userNameStr);
        parameters.put("phonenumber", userPhoneNumberStr);

//        UserWalkerDTO userWalkerDTO = new UserWalkerDTO(userIDStr, userPWStr, userNameStr, userPhoneNumberStr);

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "확인 parameters : " + parameters.toString());

        //레트로핏 결과 처리
        Call<ResultDTO> call = retrofitApi.createUserWalker(parameters);
        //비동기 네트워크 처리
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신성공 : " + "onResponse");

                ResultDTO resultDTO = response.body();
                String resultCodeStr = resultDTO.getResponceResult();

                if(resultCodeStr.contentEquals("ok")){
                    makeToast("회원가입이 완료되었습니다.");

                    Intent intent = new Intent(WalkerJoinActivity.this, WalkerLoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    makeToast("회원가입 실패");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신실패 : " + "onFailure");
                makeToast("회원가입 통신 실패");
            }
        });

//test01
//        retroClient.postJoinUserWalker(parameters, new RetroCallback() {
//            @Override
//            public void onError(Throwable t) {
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "콜백 : onError");
//            }
//
//            @Override
//            public void onSuccess(int code, Object receivedData) {
//                ResponseGet data = (ResponseGet) receivedData;
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "콜백 : onSuccess");
//            }
//
//            @Override
//            public void onFailure(int code) {
//                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "콜백 : onFailure");
//            }
//        });

    }

    //서비스 이용약관 팝업창 띄어주는 메소드
    public void onAgreementView01(View view){
        Intent intent = new Intent(this, JoinAgreement01Activity.class);
        startActivity(intent);
    }

    //개인정보 처리방침 팝업창 띄어주는 메소드
    public void onAgreementView02(View view){
        Intent intent = new Intent(this, JoinAgreement02Activity.class);
        startActivity(intent);
    }

}