package com.example.dogwalker.walker;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkableTypeBinding;
import com.example.dogwalker.retrofit2.response.WalkableTypeDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkableTypeActivity extends BaseActivity {

    private ActivityWalkableTypeBinding binding;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walkable_type);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walkable_type);
        binding.setActivity(this);

        intent = getIntent();

        //DB에 저장된 산책가능유형 데이터 불러와서 setChecked() 하기
        loadWalkableTypeDataToDB();
    }

    //DB에 산책가능유형 데이터 저장(수정) 하기
    public void onDataSave(View view){

        Boolean checkSizeS = binding.checkBoxSizeS.isChecked();
        Boolean checkSizeM = binding.checkBoxSizeM.isChecked();
        Boolean checkSizeL = binding.checkBoxSizeL.isChecked();

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "체크 상태 : " + checkSizeS + " " + checkSizeM + " " + checkSizeL);

        intent.putExtra("checkedSizeS", checkSizeS);
        intent.putExtra("checkedSizeM", checkSizeM);
        intent.putExtra("checkedSizeL", checkSizeL);

        setResult(RESULT_OK, intent);
        finish();;
    }

    //DB에 저장된 산책가능유형 데이터 불러오기
    public void loadWalkableTypeDataToDB(){

        String tableName = "user_walker";
        String selectCol1 = "walkable_type_s";
        String selectCol2 = "walkable_type_m";
        String selectCol3 = "walkable_type_l";
        String primaryCol = "id";
        String primaryVal = applicationClass.currentWalkerID;

        Call<WalkableTypeDTO> call = retrofitApi.selectWalkerData3Column(tableName, selectCol1, selectCol2, selectCol3, primaryCol, primaryVal);
        call.enqueue(new Callback<WalkableTypeDTO>() {
            @Override
            public void onResponse(Call<WalkableTypeDTO> call, Response<WalkableTypeDTO> response) {
                WalkableTypeDTO walkableTypeDTO = response.body();
                int intSizeS = walkableTypeDTO.getWalkable_type_s();
                int intSizeM = walkableTypeDTO.getWalkable_type_m();
                int intSizeL = walkableTypeDTO.getWalkable_type_l();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "서버에서 받아온 int 데이터 : " + intSizeS + " " + intSizeM + " " + intSizeL);

                if(intSizeS == 0){
                    binding.checkBoxSizeS.setChecked(false);
                }else{
                    binding.checkBoxSizeS.setChecked(true);
                }
                if(intSizeM == 0){
                    binding.checkBoxSizeM.setChecked(false);
                }else{
                    binding.checkBoxSizeM.setChecked(true);
                }
                if(intSizeL == 0){
                    binding.checkBoxSizeL.setChecked(false);
                }else{
                    binding.checkBoxSizeL.setChecked(true);
                }
            }

            @Override
            public void onFailure(Call<WalkableTypeDTO> call, Throwable t) {

            }
        });

    }


}