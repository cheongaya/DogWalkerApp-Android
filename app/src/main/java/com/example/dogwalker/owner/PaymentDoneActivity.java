package com.example.dogwalker.owner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityPaymentDoneBinding;

public class PaymentDoneActivity extends AppCompatActivity {

    ActivityPaymentDoneBinding binding;

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

    //확인 버튼 클릭시 메인으로 화면 전환
    public void onClickOK(View view){

        //쌓인 스택을 모두 비우고 메인 예약화면으로 화면전환하기
        Intent intent = new Intent(PaymentDoneActivity.this, OwnerBookingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}