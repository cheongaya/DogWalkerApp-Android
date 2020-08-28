package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkerIntroduceBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.retrofit2.response.ResultStrDTO;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkerIntroduceActivity extends BaseActivity {

    private ActivityWalkerIntroduceBinding binding;

    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walker_introduce);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walker_introduce);
        binding.setActivity(this);

        intent = getIntent();

        //DB에 저장된 자기소개 데이터 불러와서 setText() 하기
        loadIntroduceDataToDB();
    }

    //등록버튼 클릭시 입력한 데이터 저장하는 메소드
    public void onSaveData(View view){

        String tvIntroduceStr = binding.editTextIntroduce.getText().toString();

        //전 화면으로 돌아가기
        intent.putExtra("updateIntroduceData", tvIntroduceStr);
        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "인텐트로 보내는 자기소개 수정 데이터 : " + tvIntroduceStr);
        setResult(RESULT_OK, intent);
        finish();
    }

    //DB에 저장된 자기소개 데이터 불러와서 setText() 하는 메소드
    public void loadIntroduceDataToDB(){

        String tableName = "user_walker";
        String selectCol = "introduce";
        String primaryCol = "id";
        String primaryVal = applicationClass.currentWalkerID;

        Call<ResultStrDTO> call = retrofitApi.selectWalkerData(tableName, selectCol, primaryCol, primaryVal);

        call.enqueue(new Callback<ResultStrDTO>() {
            @Override
            public void onResponse(Call<ResultStrDTO> call, Response<ResultStrDTO> response) {

                ResultStrDTO resultData = response.body();
                String resultDataString = resultData.toString();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에서 받아온 결과 : " + resultDataString);
//                binding.editTextIntroduce.setText(resultDataStr.toString());

                if(resultDataString.contentEquals("null")){
//                    makeToast("데이터 조회 완료 : null");
                }else{
                    binding.editTextIntroduce.setText(resultDataString);
//                    makeToast("데이터 조회 완료");
                }


            }
            @Override
            public void onFailure(Call<ResultStrDTO> call, Throwable t) {

            }
        });

    }

}