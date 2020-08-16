package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerLoginBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerLoginActivity extends BaseActivity {

    private ActivityWalkerLoginBinding binding;

    String loginIDStr, loginPWStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_login);
        binding.setActivity(this);

//        //액션바 숨기기
//        getSupportActionBar().hide();

//        binding.EditTextLoginID;
//        binding.EditTextLoginPW;
//        binding.ButtonLoginOK;
    }

    //로그인 완료 후 도그워커 산책 페이지로 이동
    public void onLoginOK(View view){

        loginIDStr = binding.editTextLoginID.getText().toString();
        loginPWStr = binding.editTextLoginPW.getText().toString();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("loginID", loginIDStr);
        parameters.put("loginPW", loginPWStr);

        //레트로핏 결과 처리
        Call<ResultDTO> call = retrofitApi.loginUserWalker(parameters);
        //비동기 네트워크 처리
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신성공 : " + "onResponse");

//                makeToast(response.body().toString());

                ResultDTO resultDTO = response.body();
                String resultCodeStr = resultDTO.getResponceResult();

                if(resultCodeStr.contentEquals("ok")){
                    makeToast("로그인이 완료되었습니다.");

                    //쉐어드에 자동로그인 데이터 저장
                    applicationClass.mySharedPref.saveStringPref("autoLoginID", loginIDStr);
                    applicationClass.mySharedPref.saveStringPref("autoLoginPW", loginPWStr);
                    applicationClass.mySharedPref.saveBooleanPref("autoLoginCheck", true);

                    //application 현재 로그인한 아이디 데이터 저장
                    applicationClass.currentWalkerID = loginIDStr;
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "로그인한 아이디(ApplicationClassID) : " + applicationClass.currentWalkerID);

                    Intent intent = new Intent(WalkerLoginActivity.this, WalkerDogwalkingActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    makeToast("아이디 혹은 비밀번호가 틀립니다.");
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "통신실패 : " + "onFailure");
                makeToast("실패");
            }
        });

//        Intent intent = new Intent(this, WalkerDogwalkingActivity.class);
//        startActivity(intent);
//        finish();
    }

    //회원가입 페이지로 이동
    public void onJoinGO(View view){
        Intent intent = new Intent(this, WalkerJoinActivity.class);
        startActivity(intent);
//        finish();
    }
}