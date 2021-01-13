package com.example.dogwalker.owner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityPaymentDoneBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentDoneActivity extends BaseActivity {

    ActivityPaymentDoneBinding binding;

//    String booking_id;
    String walker_id, owner_dog_name, walk_date, walk_time, payment_method, payment_time;
    int walk_total_time, walk_total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment_done);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_done);
        binding.setActivity(this);

        Intent intent = getIntent();

        walker_id = intent.getStringExtra("walker_id");
        owner_dog_name = intent.getStringExtra("owner_dog_name");
        walk_date = intent.getStringExtra("walk_date");
        walk_time = intent.getStringExtra("walk_time");
        walk_total_time = intent.getIntExtra("walk_total_time", 0);
        walk_total_price = intent.getIntExtra("walk_total_price", 0);
        payment_method = intent.getStringExtra("payment_method");
        payment_time = intent.getStringExtra("payment_time");
//        booking_id = intent.getStringExtra("booking_id");

        //DB에 채팅방 생성
//        insertChatRoom(booking_id);

        //화면에 데이터 표시
        binding.textViewPaymentWalkerId.setText(walker_id);
        binding.textViewPaymentDogName.setText(owner_dog_name);
        binding.textViewPaymentWalkDate.setText(walk_date);
        binding.textViewPaymentWalkTime.setText(walk_time);
        binding.textViewPaymentWalkTotalTime.setText(walk_total_time+" 분");
        binding.textViewPaymentTotalPrice.setText(walk_total_price+" 원");
        if(payment_method.contentEquals("phone")){
            binding.textViewPaymentMethod.setText("휴대폰 결제");
        }else if(payment_method.contentEquals("card")){
            binding.textViewPaymentMethod.setText("카드 결제");
        }
        binding.textViewPaymentTime.setText(payment_time);
    }

    //DB에 채팅방 생성
    public void insertChatRoom(String booking_id){

        int bookingID = Integer.valueOf(booking_id);

        Call<ResultDTO> call = retrofitApi.insertChatRoom(bookingID);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String str = resultDTO.getResponceResult();
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "채팅방 생성 성공 : "+str);
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {
                }.getClass().getEnclosingMethod().getName() + "()", "채팅방 생성 실패 : " + t.toString());
            }
        });
    }

    //확인 버튼 클릭시 메인으로 화면 전환
    public void onClickOK(View view){

        //쌓인 스택을 모두 비우고 메인 예약화면으로 화면전환하기
        Intent intent = new Intent(PaymentDoneActivity.this, OwnerBookingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}