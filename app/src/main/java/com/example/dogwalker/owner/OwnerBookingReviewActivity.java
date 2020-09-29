package com.example.dogwalker.owner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.dogwalker.BaseActivity;
import com.example.dogwalker.R;
import com.example.dogwalker.databinding.ActivityOwnerBookingReviewBinding;
import com.example.dogwalker.retrofit2.response.ResultDTO;
import com.example.dogwalker.walker.WalkerDogwalkingActivity;
import com.example.dogwalker.walker.WalkerDogwalkingDoneActivity;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingReviewActivity extends BaseActivity {

    ActivityOwnerBookingReviewBinding  binding;

    int booking_id;          //예약 번호
    String booking_dog_name; //산책예약 강아지 이름
    String walker_id, owner_id;
    int satisfationProgress = 0;    //seekBar 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_owner_booking_review);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_owner_booking_review);
        binding.setActivity(this);

        //booking_id (예약번호) 데이터 불러오기
        Intent intent = getIntent();
        booking_id = intent.getIntExtra("booking_id", 0);
        booking_dog_name = intent.getStringExtra("booking_dog_name");
        walker_id = intent.getStringExtra("walker_id");
        owner_id = intent.getStringExtra("owner_id");

        //데이터 화면에 표시
        binding.textViewReviewWalkerName.setText(walker_id);
        binding.textViewReviewBookingContent.setText(booking_dog_name+"(반려견) 도그워커 서비스 예약");

        //서비스 이용 만족도 평가 seekBar 리스너
        seekBarChangeListener();

    }

    //서비스 이용 만족도 seekBar 리스너
    public void seekBarChangeListener(){
        //만족도 seekBar
        binding.seekBarReviewSatisfationScore.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //seekBar 변할 때
                binding.textViewReivewSatisfationScore.setText("만족도 "+progress);
                satisfationProgress = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //버튼 - 리뷰 사진/동영상 업로드
    public void btnReviewFileUpload(View view){
        makeToast("서비스 준비중입니다");
    }

    //버튼 - 리뷰쓰기 취소
    public void btnReviewCancel(View view){
        //리뷰 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
        confirmReviewCancelAlertDialog();
    }

    //버튼 - 리뷰쓰기 등록
    public void btnReviewUpload(View view){
        //리뷰 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
        confirmReviewUploadAlertDialog();
    }

    //리뷰 취소 버튼 클릭시 한번더 취소여부를 물어보는 다이얼로그
    public void confirmReviewCancelAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("리뷰 취소")
                .setMessage("리뷰 작성을 취소하시겠습니까?");

        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    //리뷰 등록 버튼 클릭시 한번더 등록여부를 물어보는 다이얼로그
    public void confirmReviewUploadAlertDialog(){

        //산책 리뷰 데이터 추가
        Map<String, RequestBody> partMap = new HashMap<>();
        partMap.put("booking_id", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(booking_id)));
        partMap.put("walker_id", RequestBody.create(MediaType.parse("text/plain"), walker_id));
        partMap.put("owner_id", RequestBody.create(MediaType.parse("text/plain"), owner_id));
        partMap.put("booking_dog_name", RequestBody.create(MediaType.parse("text/plain"), booking_dog_name));
        partMap.put("review_satisfation_score", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(satisfationProgress)));
        partMap.put("review_memo", RequestBody.create(MediaType.parse("text/plain"), binding.editTextReivewMemo.getText().toString()));
        partMap.put("review_files_url", RequestBody.create(MediaType.parse("text/plain"), "null"));

        //서버에 저장
        Call<ResultDTO> call = retrofitApi.insertBookingReviewData(partMap);
        call.enqueue(new Callback<ResultDTO>() {
            @Override
            public void onResponse(Call<ResultDTO> call, Response<ResultDTO> response) {
                ResultDTO resultDTO = response.body();
                String resultDataStr = resultDTO.getResponceResult();
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "워커 리뷰 서버 저장 성공");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", resultDataStr);

                if(resultDataStr.contentEquals("ok")){
                    //쌓인 스택을 모두 비우고 메인으로 화면전환하기
                    Intent intent = new Intent(OwnerBookingReviewActivity.this, OwnerBookingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResultDTO> call, Throwable t) {
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", "워커 리뷰 서버 저장 실패");
                makeLog(new Object() {}.getClass().getEnclosingMethod().getName() + "()", t.toString());
            }
        });

    }
}