package com.example.dogwalker.owner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerLoginBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerJoinActivity;
import com.example.dogwalker.walker.WalkerLoginActivity;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerLoginActivity extends BaseActivity {

    private ActivityOwnerLoginBinding binding;

    String loginIDStr , loginPWStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_login);
        binding.setActivity(this);
    }

    //로그인 완료 후 반려인 메인화면으로 이동
    public void onLoginOK(View view){

        loginIDStr = binding.editTextLoginID.getText().toString();
        loginPWStr = binding.editTextLoginPW.getText().toString();

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", "user_owner");
        parameters.put("loginID", loginIDStr);
        parameters.put("loginPW", loginPWStr);

        //레트로핏 결과 처리
        Call<ResultDTO> call = retrofitApi.loginUser(parameters);
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
                    applicationClass.mySharedPref.saveStringPref("autoLoginType", "owner");
                    applicationClass.mySharedPref.saveBooleanPref("autoLoginCheck", true);

                    //application 현재 로그인한 아이디 데이터 저장
                    applicationClass.currentWalkerID = loginIDStr;
                    makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "로그인한 아이디(ApplicationClassID) : " + applicationClass.currentWalkerID);

                    Intent intent = new Intent(OwnerLoginActivity.this, OwnerMypageActivity.class);
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

    }

    //회원가입 페이지로 이동
    public void onJoinGO(View view){
        Intent intent = new Intent(this, OwnerJoinActivity.class);
        startActivity(intent);
//        finish();
    }
}