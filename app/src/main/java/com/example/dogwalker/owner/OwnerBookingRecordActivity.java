package com.example.dogwalker.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerBookingRecordBinding;
import com.example.dogwalker.retrofit2.response.BookingDoneRecordDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingRecordActivity extends BaseActivity {

    ActivityOwnerBookingRecordBinding binding;

    int booking_id;          //예약 번호
    String booking_dog_name; //산책예약 강아지 이름
    String walker_id, owner_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking_record);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_booking_record);
        binding.setActivity(this);

        //booking_id (예약번호) 데이터 불러오기
        Intent intent = getIntent();
        booking_id = intent.getIntExtra("booking_id", 0);
        booking_dog_name = intent.getStringExtra("booking_dog_name");
        walker_id = intent.getStringExtra("walker_id");
        owner_id = intent.getStringExtra("owner_id");

        //DB에서 해당 예약 기록 데이터 불러오기
        selectBookingDoneRecordToDB();
    }

    //DB에서 해당 예약 기록 데이터 불러오기
    public void selectBookingDoneRecordToDB(){

        Call<List<BookingDoneRecordDTO>> call = retrofitApi.selectBookingDoneRecordData(booking_id);
        call.enqueue(new Callback<List<BookingDoneRecordDTO>>() {
            @Override
            public void onResponse(Call<List<BookingDoneRecordDTO>> call, Response<List<BookingDoneRecordDTO>> response) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "산책기록 데이터 조회 성공");
                List<BookingDoneRecordDTO> bookingDoneRecordDTOList = response.body();
                //view에 데이터 표시하기
                binding.textViewWalkDoneCurrentTime.setText(bookingDoneRecordDTOList.get(0).getDone_current_time());
                binding.textViewWalkDoneDogName.setText(booking_dog_name);
                binding.textViewWalkDoneWalkingTime.setText(bookingDoneRecordDTOList.get(0).getDone_walking_time());
                binding.textViewWalkDoneDistance.setText(bookingDoneRecordDTOList.get(0).getDone_distance());
                binding.textViewWalkDonePooCount.setText(bookingDoneRecordDTOList.get(0).getDone_poo_count()+"");
                binding.textViewWalkDoneMemo.setText(bookingDoneRecordDTOList.get(0).getDone_memo());
                //TODO: 산책 이미지 setImage 해야함
            }

            @Override
            public void onFailure(Call<List<BookingDoneRecordDTO>> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "산책기록 데이터 조회 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "에러 : " + t.toString());

            }
        });

    }

    //도그워커 후기 쓰기 버튼
    public void btnWriteWalkerReview(View view){

        //도그워커 후기 남길지 여부 물어보는 다이얼로그
        addWalkerReviewAlertDialog();

    }

    //도그워커 후기 남길지 여부 물어보는 다이얼로그
    public void addWalkerReviewAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("도그워커 평가하기")
                .setMessage("이용하신 도그워커의 후기를 남기시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //후기 남기는 액티비티 다이얼로그 형태로 띄우기
                Intent reviewIntent = new Intent(OwnerBookingRecordActivity.this, OwnerBookingReviewActivity.class);
                reviewIntent.putExtra("booking_id", booking_id);
                reviewIntent.putExtra("booking_dog_name", booking_dog_name);
                reviewIntent.putExtra("walker_id", walker_id);
                reviewIntent.putExtra("owner_id", owner_id);
                startActivity(reviewIntent);
            }
        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}