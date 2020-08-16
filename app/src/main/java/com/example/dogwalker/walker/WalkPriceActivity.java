package com.example.dogwalker.walker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityWalkPriceBinding;
import com.example.dogwalker.databinding.ActivityWalkableTypeBinding;
import com.example.dogwalker.retrofit2.response.WalkPriceDTO;
import com.example.dogwalker.retrofit2.response.WalkableTypeDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalkPriceActivity extends BaseActivity {

    private ActivityWalkPriceBinding binding;
    Intent intent;

//    private ActivityWalkerPrice binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_walk_price);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_price);
        binding.setActivity(this);

        intent = getIntent();

        //DB에 저장된 가격표 데이터 불러와서 setText() 하기
        loadWalkPriceDataToDB();
    }

    //DB에 가격표 데이터 저장(수정) 하기
    public void onDataSave(View view){

        int price01 = Integer.parseInt(binding.editTextThirtyMinutes.getText().toString());
        int price02 = Integer.parseInt(binding.editTextSixtyMinutes.getText().toString());
        int price03 = Integer.parseInt(binding.editTextAddLargeSize.getText().toString());
        int price04 = Integer.parseInt(binding.editTextAddOneDog.getText().toString());
        int price05 = Integer.parseInt(binding.editTextAddHoliday.getText().toString());

        makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()",
                "업로드할 가격표 : " + price01 + " " + price02 + " " + price03 + " " + price04 + " " + price05);

        intent.putExtra("priceInt01", price01);
        intent.putExtra("priceInt02", price02);
        intent.putExtra("priceInt03", price03);
        intent.putExtra("priceInt04", price04);
        intent.putExtra("priceInt05", price05);

        setResult(RESULT_OK, intent);
        finish();;
    }

    //DB에 저장된 가격표 데이터 불러오기
    public void loadWalkPriceDataToDB(){

        String id = applicationClass.currentWalkerID;

        Call<WalkPriceDTO> call = retrofitApi.selectWalkPrice(id);
        call.enqueue(new Callback<WalkPriceDTO>() {
            @Override
            public void onResponse(Call<WalkPriceDTO> call, Response<WalkPriceDTO> response) {

                WalkPriceDTO walkPriceDTO = response.body();
                int priceValInt1 = walkPriceDTO.getPrice_thirty_minutes();
                int priceValInt2 = walkPriceDTO.getPrice_sixty_minutes();
                int priceValInt3 = walkPriceDTO.getAddprice_large_size();
                int priceValInt4 = walkPriceDTO.getAddprice_one_dog();
                int priceValInt5 = walkPriceDTO.getAddprice_holiday();

                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()",
                        "서버에서 받은 가격 데이터 : " + priceValInt1 + " " + priceValInt2 + " " + priceValInt3 + " " + priceValInt4 + " " + priceValInt5);

                binding.editTextThirtyMinutes.setText(priceValInt1+"");
                binding.editTextSixtyMinutes.setText(priceValInt2+"");
                binding.editTextAddLargeSize.setText(priceValInt3+"");
                binding.editTextAddOneDog.setText(priceValInt4+"");
                binding.editTextAddHoliday.setText(priceValInt5+"");
            }

            @Override
            public void onFailure(Call<WalkPriceDTO> call, Throwable t) {

            }
        });

    }
}